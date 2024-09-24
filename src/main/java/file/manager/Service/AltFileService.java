package file.manager.Service;

import cinapse.cinapse.Entities.File;
import cinapse.cinapse.Entities.Owner;
import cinapse.cinapse.Repository.FileRepository;
import cinapse.cinapse.Repository.OwnerRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AltFileService {
    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    private final OwnerRepository userRepository;

    private final FileRepository mediaFileRepository;

    private String generateFileName(MultipartFile file) {
        return new Date().getTime() + "-" + Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "_");
    }

    @Async
    public CompletableFuture<File> uploadFile(MultipartFile file, String token) {
        String fileName = generateFileName(file);
        Optional<Owner> userOptional = userRepository.findByToken(token);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid token");
        }

        Owner user = userOptional.get();

        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            String url = minioUrl + "/" + bucketName + "/" + fileName;

            var mediaFile = new File();
            mediaFile.setFileName(fileName);
            mediaFile.setFilePath(bucketName + "/" + fileName);
            mediaFile.setUrl(url);
            mediaFile.setOwner(user);

            return CompletableFuture.completedFuture(mediaFileRepository.save(mediaFile));
        } catch (Exception e) {
            throw new MultipartException("Failed to store file.", e);
        }
    }

    @Async
    public CompletableFuture<List<File>> uploadMultipleFiles(MultipartFile[] files, String token) {
        List<CompletableFuture<File>> futures = new ArrayList<>();
        for (MultipartFile file : files) {
            futures.add(uploadFile(file, token));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        List<File> uploadedFiles = new ArrayList<>();
        for (CompletableFuture<File> future : futures) {
            try {
                uploadedFiles.add(future.get());
            } catch (Exception e) {
                throw new MultipartException("Failed to store file.", e);
            }
        }
        return CompletableFuture.completedFuture(uploadedFiles);
    }

    public List<File> listFilesByOwnerToken(String token) {
        var user = userRepository.findByToken(token);
        if(user.isEmpty()) {
            throw new IllegalArgumentException("Invalid token");
        }

        var userId = user.get().getId();
        return mediaFileRepository.findByOwnerId(userId);
    }
    
    @Async
    public CompletableFuture<Void> deleteFilesByUserTokenAndFileId(UUID id, String ownerToken) {
        var user = userRepository.findByToken(ownerToken);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Invalid token");
        }
        var userId = user.get().getId();
        var file = mediaFileRepository.findById(id).orElseThrow();
        if (!Objects.equals(file.getOwner().getId(), userId)) {
            throw new IllegalArgumentException("Invalid Access");
        }
        mediaFileRepository.delete(file);
        return CompletableFuture.completedFuture(null);
    }
}


//    @Async
//    public CompletableFuture<Void> deleteFilesByUserToken(String token) {
//        List<File> files = mediaFileRepository.findByOwnerToken(token);
//        files.forEach(file -> {
//            try {
//                minioClient.removeObject(
//                        RemoveObjectArgs.builder()
//                                .bucket(bucketName)
//                                .object(file.getFilePath().split("/", 2)[1])
//                                .build());
//            } catch (Exception e) {
//                throw new RuntimeException("Error while deleting file: " + file.getFileName(), e);
//            }
//        });
//        mediaFileRepository.deleteByOwnerToken(token);
//        return CompletableFuture.completedFuture(null);
//    }
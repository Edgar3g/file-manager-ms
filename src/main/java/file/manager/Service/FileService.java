package file.manager.Service;

import cinapse.cinapse.Entities.File;
import cinapse.cinapse.Entities.Owner;
import cinapse.cinapse.Repository.FileRepository;
import cinapse.cinapse.Repository.OwnerRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;



    @Autowired
    private final OwnerRepository userRepository;
    @Autowired
    private final FileRepository mediaFileRepository;

    private String generateFileName(MultipartFile file) {
        return new Date().getTime() + "-" + Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "_");
    }

    public File uploadFile(MultipartFile file, String token) {
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

            return mediaFileRepository.save(mediaFile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    public List<File> listFilesByOwnerToken(String token) {
        var user = userRepository.findByToken(token);
        if(user.isEmpty())
        {
            throw new IllegalArgumentException("Invalid token");
        }

            var userId = user.get().getId();
            return mediaFileRepository.findByOwnerId(userId);
    }

    public void deleteFilesByUserToken(String token) {
        List<File> files = mediaFileRepository.findByOwnerToken(token);
        files.forEach(file -> {
            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(file.getFilePath().split("/", 2)[1])
                                .build());
            } catch (Exception e) {
                throw new RuntimeException("Error while deleting file: " + file.getFileName(), e);
            }
        });
        mediaFileRepository.deleteByOwnerToken(token);
    }

    public void deleteFilesByUserTokenAndFileId(UUID id, String OwnerToken) {
        var user = userRepository.findByToken(OwnerToken);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Invalid token");
        }
        var userId = user.get().getId();
        var file = mediaFileRepository.findById(id).orElseThrow();
        if (!Objects.equals(file.getOwner(), userId)) {
            throw new IllegalArgumentException("Invalid Acess");
        }
        mediaFileRepository.delete(file);

    }
}

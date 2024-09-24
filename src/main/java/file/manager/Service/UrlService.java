package file.manager.Service;

import cinapse.cinapse.Entities.File;
import cinapse.cinapse.Repository.FileRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UrlService {
    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.url.time}")
    private int urlTimeValid;

    private final FileRepository fileRepository;

    public String generatePresidedUrl(UUID fileId) {
        Optional<File> fileOptional = fileRepository.findById(fileId);

        if (fileOptional.isEmpty()) {
            throw new IllegalArgumentException("File not found with id: " + fileId);
        }

        File file = fileOptional.get();
        String objectName = file.getFileName();
        try {

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(urlTimeValid)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL for file: " + fileId, e);
        }
    }
}

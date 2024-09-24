package file.manager.Controller;

import cinapse.cinapse.Entities.File;
import cinapse.cinapse.Service.AltFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/altfiles")
public class AltFileController {
    @Autowired
    private AltFileService fileService;

    @PostMapping("/upload")
    public CompletableFuture<ResponseEntity<File>> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("token") String token) {
        return fileService.uploadFile(file, token).thenApply(ResponseEntity::ok);
    }

    @PostMapping("/upload/multiple")
    public CompletableFuture<ResponseEntity<List<File>>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("token") String token) {
        return fileService.uploadMultipleFiles(files, token).thenApply(ResponseEntity::ok);
    }

    @GetMapping("/list")
    public ResponseEntity<List<File>> listFiles(@RequestParam("token") String token) {
        List<File> files = fileService.listFilesByOwnerToken(token);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/delete/{fileId}")
    public CompletableFuture<ResponseEntity<Void>> deleteFiles(@RequestParam("token") String token, @PathVariable UUID fileId) {
        return fileService.deleteFilesByUserTokenAndFileId(fileId, token).thenApply(aVoid -> ResponseEntity.noContent().build());
    }
}

package file.manager.Controller;

import file.manager.Entities.File;
import file.manager.Service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<File> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("token") String token) {
        File mediaFile = fileService.uploadFile(file, token);
        return ResponseEntity.ok(mediaFile);
    }

    @GetMapping("/list")
    public ResponseEntity<List<File>> listFiles(@RequestParam("token") String token) {
        List<File> files = fileService.listFilesByOwnerToken(token);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<Void> deleteFiles(@RequestParam("token") String token, @PathVariable UUID fileId) {
        fileService.deleteFilesByUserTokenAndFileId(fileId, token);
        return ResponseEntity.noContent().build();
    }
}

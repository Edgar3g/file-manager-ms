package file.manager.Controller;

import cinapse.cinapse.Service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("files/share")
public class SharedUrlController {
    private final UrlService urlService;

    @GetMapping("/{fileId}/url")
    public String getSharedUrl(@PathVariable UUID fileId) {
        return urlService.generatePresidedUrl(fileId);
    }
}

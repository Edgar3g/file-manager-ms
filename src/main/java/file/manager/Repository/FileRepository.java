package file.manager.Repository;

import cinapse.cinapse.Entities.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {
    List<File> findByOwnerToken(String token);
    List<File> findByOwnerId(UUID id);
    void deleteByOwnerToken(String token);
}

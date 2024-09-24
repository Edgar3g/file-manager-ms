package file.manager.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity(name = "file")
@Data
@Table(name = "file")

public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

}

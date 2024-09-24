package file.manager.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "owner")
@Table(name = "owner")
@Data
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "owner_id")
    private UUID id;

    @Column(unique = true)
    private String token;
}

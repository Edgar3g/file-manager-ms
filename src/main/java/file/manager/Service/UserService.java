package file.manager.Service;

import file.manager.Entities.Owner;
import file.manager.Repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private OwnerRepository userRepository;

    public Owner createUser(Owner user) {
        user.setId(UUID.randomUUID());
        return userRepository.save(user);
    }

    public Owner getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<Owner> getAllUsers() {
        return userRepository.findAll();
    }

    public Owner updateUser(UUID id, Owner user) {
        Optional<Owner> existingUserOptional = userRepository.findById(id);
        if (existingUserOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Owner existingUser = existingUserOptional.get();
        existingUser.setToken(user.getToken());
        return userRepository.save(existingUser);
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
    }
}

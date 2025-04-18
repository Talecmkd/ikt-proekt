package finki.ikt.iktproekt.Service;

import finki.ikt.iktproekt.model.User;
import finki.ikt.iktproekt.model.enumeration.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    User create(User user);
    User update(Long id, User user);
    void delete(Long id);
    Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findByEmail(String email);
    User registerUser(String name, String email, String password, Role role,LocalDateTime createdAt);


}

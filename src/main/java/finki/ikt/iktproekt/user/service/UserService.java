package finki.ikt.iktproekt.user.service;

import finki.ikt.iktproekt.user.model.User;
import finki.ikt.iktproekt.user.model.dto.UserUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    User create(User user);
    User update(UserUpdateRequest userUpdateRequest);
    void delete(Long id);
    Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findByEmail(String email);

    User getCurrentLoggedInUser();
}

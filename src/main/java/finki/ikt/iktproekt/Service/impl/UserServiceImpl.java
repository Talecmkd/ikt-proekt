package finki.ikt.iktproekt.Service.impl;

import finki.ikt.iktproekt.Service.UserService;
import finki.ikt.iktproekt.model.User;
import finki.ikt.iktproekt.model.enumeration.Role;
import finki.ikt.iktproekt.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
public class UserServiceImpl implements UserService {
    private  final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository
                           ,PasswordEncoder passwordEncoder
                           ) {
        this.userRepository = userRepository;
       this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User user) {
        if (userRepository.existsById(id)) {
            user.setUser_id(id);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User registerUser(String name, String email, String password, Role role, LocalDateTime createdAt) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.role = role;
        //user.setPassword(password);
        user.setCreatedAt(LocalDateTime.now());


        return userRepository.save(user);
    }
}

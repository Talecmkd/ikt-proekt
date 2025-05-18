package finki.ikt.iktproekt.user.service.impl;

import finki.ikt.iktproekt.exception.NotFoundEntityException;

import finki.ikt.iktproekt.security.JwtUtil;

import finki.ikt.iktproekt.user.model.User;

import finki.ikt.iktproekt.user.model.dto.UserUpdateRequest;
import finki.ikt.iktproekt.user.repository.UserRepository;

import finki.ikt.iktproekt.user.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

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
    public User update(UserUpdateRequest userUpdateRequest) {
        User user = getCurrentLoggedInUser();

        user.setEmail(userUpdateRequest.getEmail());
        user.setName(userUpdateRequest.getName());

        return userRepository.save(user);
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


    @Override
    public User getCurrentLoggedInUser() {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String email = jwtUtil.extractUsername(token);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundEntityException(User.class));
    }
}

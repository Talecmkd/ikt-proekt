package finki.ikt.iktproekt.user.service.impl;

import finki.ikt.iktproekt.exception.NotFoundEntityException;

import finki.ikt.iktproekt.security.JwtUtil;

import finki.ikt.iktproekt.user.model.User;

import finki.ikt.iktproekt.user.model.dto.AuthenticationRequest;
import finki.ikt.iktproekt.user.model.dto.AuthenticationResponse;
import finki.ikt.iktproekt.user.model.dto.RegisterRequest;

import finki.ikt.iktproekt.user.model.enumaration.Role;

import finki.ikt.iktproekt.user.repository.UserRepository;

import finki.ikt.iktproekt.user.service.AuthService;
import finki.ikt.iktproekt.user.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

    private final UserService userService;

    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                        authenticationRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        Optional<User> userOpt = userService.findByEmail(authenticationRequest.getEmail());
        if (userOpt.isEmpty()) {
           throw new NotFoundEntityException(User.class);
        }

        User user = userOpt.get();

        return new AuthenticationResponse(
                jwt,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new NotFoundEntityException(User.class);
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return new AuthenticationResponse(
                jwt,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}

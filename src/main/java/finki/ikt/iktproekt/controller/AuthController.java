package finki.ikt.iktproekt.controller;

import finki.ikt.iktproekt.service.UserService;
import finki.ikt.iktproekt.model.User;
import finki.ikt.iktproekt.model.dto.AuthenticationDTOs.AuthenticationRequest;
import finki.ikt.iktproekt.model.dto.AuthenticationDTOs.AuthenticationResponse;
import finki.ikt.iktproekt.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          UserService userService,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        // Get additional user information
        Optional<User> userOpt = userService.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            AuthenticationResponse response = new AuthenticationResponse(
                    jwt,
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.role != null ? user.role.name() : "USER"
            );
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(new AuthenticationResponse(jwt, null, null, request.getEmail(), null));
    }
}

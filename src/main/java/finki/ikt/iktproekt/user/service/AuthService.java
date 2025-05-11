package finki.ikt.iktproekt.user.service;

import finki.ikt.iktproekt.user.model.User;

import finki.ikt.iktproekt.user.model.dto.AuthenticationRequest;
import finki.ikt.iktproekt.user.model.dto.AuthenticationResponse;

import finki.ikt.iktproekt.user.model.dto.RegisterRequest;
import finki.ikt.iktproekt.user.model.enumaration.Role;

import java.time.LocalDateTime;

public interface AuthService {

    AuthenticationResponse login(AuthenticationRequest authenticationRequest);

    AuthenticationResponse register(RegisterRequest registerRequest);
}

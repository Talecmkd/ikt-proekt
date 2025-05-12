package finki.ikt.iktproekt.user.service;

import finki.ikt.iktproekt.user.model.dto.AuthenticationRequest;
import finki.ikt.iktproekt.user.model.dto.AuthenticationResponse;
import finki.ikt.iktproekt.user.model.dto.RegisterRequest;

public interface AuthService {

    AuthenticationResponse login(AuthenticationRequest authenticationRequest);

    AuthenticationResponse register(RegisterRequest registerRequest);
}

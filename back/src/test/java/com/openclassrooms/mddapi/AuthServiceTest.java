package com.openclassrooms.mddapi;

import com.openclassrooms.mddapi.dto.AuthResponse;
import com.openclassrooms.mddapi.dto.LoginRequest;
import com.openclassrooms.mddapi.dto.RegisterRequest;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.JwtTokenProvider;
import com.openclassrooms.mddapi.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @InjectMocks private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .username("testuser")
                .password("encodedPassword")
                .build();
    }

    // ─── register ────────────────────────────────────────────────────────────────

    @Test
    void register_doitCreerUtilisateurEtRetournerToken() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@test.com")
                .username("testuser")
                .password("Password1!")
                .build();

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("Password1!")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtTokenProvider.generateToken(auth)).thenReturn("jwt-token-123");

        AuthResponse result = authService.register(request);

        assertThat(result.getToken()).isEqualTo("jwt-token-123");
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_doitLeverExceptionSiEmailDejaUtilise() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@test.com")
                .username("testuser")
                .password("Password1!")
                .build();

        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertThat(ex.getMessage()).contains("Email is already in use");
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_doitLeverExceptionSiUsernameDejaUtilise() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@test.com")
                .username("testuser")
                .password("Password1!")
                .build();

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertThat(ex.getMessage()).contains("Username is already taken");
        verify(userRepository, never()).save(any());
    }

    // ─── login ───────────────────────────────────────────────────────────────────

    @Test
    void login_doitAuthentifierEtRetournerToken() {
        LoginRequest request = LoginRequest.builder()
                .emailOrUsername("testuser")
                .password("Password1!")
                .build();

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtTokenProvider.generateToken(auth)).thenReturn("jwt-token-456");
        when(userRepository.findByEmailOrUsername("testuser", "testuser")).thenReturn(Optional.of(testUser));

        AuthResponse result = authService.login(request);

        assertThat(result.getToken()).isEqualTo("jwt-token-456");
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void login_doitLeverExceptionSiUtilisateurIntrouvable() {
        LoginRequest request = LoginRequest.builder()
                .emailOrUsername("inconnu")
                .password("Password1!")
                .build();

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtTokenProvider.generateToken(auth)).thenReturn("token");
        when(userRepository.findByEmailOrUsername("inconnu", "inconnu")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }
}

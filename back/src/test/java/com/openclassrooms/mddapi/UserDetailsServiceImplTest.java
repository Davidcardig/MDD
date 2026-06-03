package com.openclassrooms.mddapi;

import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_doitRetournerUserDetailsSiTrouve() {
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .username("testuser")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmailOrUsername("testuser", "testuser"))
                .thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    void loadUserByUsername_doitLeverExceptionSiIntrouvable() {
        when(userRepository.findByEmailOrUsername("inconnu", "inconnu"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("inconnu"));
    }

    @Test
    void loadUserByUsername_doitFonctionnerAvecEmail() {
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .username("testuser")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmailOrUsername("test@test.com", "test@test.com"))
                .thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("test@test.com");

        assertThat(result.getUsername()).isEqualTo("testuser");
    }
}

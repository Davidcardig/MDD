package com.openclassrooms.mddapi;

import com.openclassrooms.mddapi.dto.ThemeResponse;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.ThemeMapper;
import com.openclassrooms.mddapi.model.Theme;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.ThemeRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.ThemeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThemeServiceTest {

    @Mock private ThemeRepository themeRepository;
    @Mock private UserRepository userRepository;
    @Mock private ThemeMapper themeMapper;
    @InjectMocks private ThemeService themeService;

    private User testUser;
    private Theme testTheme;

    @BeforeEach
    void setUp() {
        // Mock du contexte de sécurité Spring
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        testUser = User.builder()
                .id(1L).username("testuser")
                .email("test@test.com").password("password")
                .subscribedThemes(new HashSet<>())
                .build();

        testTheme = Theme.builder()
                .id(1L).title("Java").description("Programmation Java")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    }

    // ─── getAllThemes ────────────────────────────────────────────────────────────

    @Test
    void getAllThemes_doitRetournerTousLesThemesAvecFlagSubscribed() {
        testUser.getSubscribedThemes().add(testTheme);
        ThemeResponse response = ThemeResponse.builder().id(1L).title("Java").subscribed(true).build();
        when(themeRepository.findAll()).thenReturn(List.of(testTheme));
        when(themeMapper.toResponse(testTheme, true)).thenReturn(response);

        List<ThemeResponse> result = themeService.getAllThemes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isSubscribed()).isTrue();
    }

    @Test
    void getAllThemes_doitMarquerNonAbonneQuandPasAbonne() {
        Theme autre = Theme.builder().id(2L).title("Python").build();
        ThemeResponse response = ThemeResponse.builder().id(2L).title("Python").subscribed(false).build();
        when(themeRepository.findAll()).thenReturn(List.of(autre));
        when(themeMapper.toResponse(autre, false)).thenReturn(response);

        List<ThemeResponse> result = themeService.getAllThemes();

        assertThat(result.get(0).isSubscribed()).isFalse();
    }

    // ─── subscribe ───────────────────────────────────────────────────────────────

    @Test
    void subscribe_doitAjouterLhemeALutilisateur() {
        when(themeRepository.findById(1L)).thenReturn(Optional.of(testTheme));

        themeService.subscribe(1L);

        assertThat(testUser.getSubscribedThemes()).contains(testTheme);
        verify(userRepository).save(testUser);
    }

    @Test
    void subscribe_doitLeverExceptionSiThemeInexistant() {
        when(themeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> themeService.subscribe(99L));
        verify(userRepository, never()).save(any());
    }

    // ─── unsubscribe ─────────────────────────────────────────────────────────────

    @Test
    void unsubscribe_doitRetirerLeThemeDeLutilisateur() {
        testUser.getSubscribedThemes().add(testTheme);
        when(themeRepository.findById(1L)).thenReturn(Optional.of(testTheme));

        themeService.unsubscribe(1L);

        assertThat(testUser.getSubscribedThemes()).doesNotContain(testTheme);
        verify(userRepository).save(testUser);
    }

    @Test
    void unsubscribe_doitLeverExceptionSiThemeInexistant() {
        when(themeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> themeService.unsubscribe(99L));
        verify(userRepository, never()).save(any());
    }

    // ─── getSubscribedThemes ─────────────────────────────────────────────────────

    @Test
    void getSubscribedThemes_doitRetournerSeulementLesThemesAbonnes() {
        testUser.getSubscribedThemes().add(testTheme);
        ThemeResponse response = ThemeResponse.builder().id(1L).title("Java").subscribed(true).build();
        when(themeMapper.toResponse(testTheme, true)).thenReturn(response);

        List<ThemeResponse> result = themeService.getSubscribedThemes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isSubscribed()).isTrue();
        assertThat(result.get(0).getTitle()).isEqualTo("Java");
    }

    @Test
    void getSubscribedThemes_doitRetournerListeVideSiAucunAbonnement() {
        List<ThemeResponse> result = themeService.getSubscribedThemes();
        assertThat(result).isEmpty();
    }
}


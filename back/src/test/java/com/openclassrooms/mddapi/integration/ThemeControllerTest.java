package com.openclassrooms.mddapi.integration;

import com.openclassrooms.mddapi.config.SecurityConfig;
import com.openclassrooms.mddapi.controller.ThemeController;
import com.openclassrooms.mddapi.dto.ThemeResponse;
import com.openclassrooms.mddapi.security.JwtTokenProvider;
import com.openclassrooms.mddapi.security.UserDetailsServiceImpl;
import com.openclassrooms.mddapi.service.ThemeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de la couche HTTP — ThemeController.
 * Vérifie l'accès sans auth (401), la liste des thèmes et l'abonnement.
 */
@WebMvcTest(ThemeController.class)
@Import(SecurityConfig.class)
class ThemeControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private ThemeService themeService;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    // ─── GET /api/themes ──────────────────────────────────────────────────────

    @Test
    void getAllThemes_sansAuthentification_retourne401() throws Exception {
        mockMvc.perform(get("/api/themes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAllThemes_avecUtilisateurAuthentifie_retourne200AvecListe() throws Exception {
        List<ThemeResponse> themes = List.of(
                ThemeResponse.builder().id(1L).title("Java").description("Prog Java").subscribed(false).build(),
                ThemeResponse.builder().id(2L).title("Angular").description("Frontend").subscribed(true).build()
        );

        when(themeService.getAllThemes()).thenReturn(themes);

        mockMvc.perform(get("/api/themes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Java"))
                .andExpect(jsonPath("$[1].subscribed").value(true));
    }

    // ─── POST /api/themes/{id}/subscribe ─────────────────────────────────────

    @Test
    @WithMockUser(username = "testuser")
    void subscribe_avecUtilisateurAuthentifie_retourne200EtAppelleService() throws Exception {
        mockMvc.perform(post("/api/themes/1/subscribe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Abonnement au thème effectué avec succès"));

        verify(themeService).subscribe(1L);
    }
}


package com.openclassrooms.mddapi.integration;

import com.openclassrooms.mddapi.config.SecurityConfig;
import com.openclassrooms.mddapi.controller.PostController;
import com.openclassrooms.mddapi.dto.PostResponse;
import com.openclassrooms.mddapi.security.JwtTokenProvider;
import com.openclassrooms.mddapi.security.UserDetailsServiceImpl;
import com.openclassrooms.mddapi.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de la couche HTTP — PostController.
 * Vérifie l'accès sans auth (401), avec auth (@WithMockUser) et la validation.
 */
@WebMvcTest(PostController.class)
@Import(SecurityConfig.class)
class PostControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private PostService postService;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    // ─── GET /api/posts/feed ──────────────────────────────────────────────────

    @Test
    void getFeed_sansAuthentification_retourne401() throws Exception {
        mockMvc.perform(get("/api/posts/feed"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getFeed_avecUtilisateurAuthentifie_retourne200AvecListe() throws Exception {
        PostResponse post = PostResponse.builder()
                .id(1L)
                .title("Article test")
                .authorUsername("testuser")
                .themeTitle("Java")
                .build();

        when(postService.getFeed()).thenReturn(List.of(post));

        mockMvc.perform(get("/api/posts/feed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Article test"))
                .andExpect(jsonPath("$[0].authorUsername").value("testuser"));
    }

    // ─── POST /api/posts ──────────────────────────────────────────────────────

    @Test
    @WithMockUser(username = "testuser")
    void createPost_avecCorpsVide_retourne400() throws Exception {
        // Corps vide → les @NotBlank/@NotNull du PostRequest échouent → 400
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}


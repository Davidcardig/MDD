package com.openclassrooms.mddapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.config.SecurityConfig;
import com.openclassrooms.mddapi.controller.PostController;
import com.openclassrooms.mddapi.dto.CommentRequest;
import com.openclassrooms.mddapi.dto.CommentResponse;
import com.openclassrooms.mddapi.dto.PostRequest;
import com.openclassrooms.mddapi.dto.PostResponse;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void getFeed_sansAuthentification_retourne403() throws Exception {
        mockMvc.perform(get("/api/posts/feed"))
                .andExpect(status().isForbidden());
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

    @Test
    @WithMockUser(username = "testuser")
    void createPost_avecDonneesValides_retourne200() throws Exception {
        PostRequest request = new PostRequest(1L, "Mon article", "Contenu de l'article");
        PostResponse response = PostResponse.builder()
                .id(1L).title("Mon article").authorUsername("testuser").themeTitle("Java").build();

        when(postService.createPost(any())).thenReturn(response);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Mon article"));
    }

    // ─── GET /api/posts/{id} ──────────────────────────────────────────────────

    @Test
    @WithMockUser(username = "testuser")
    void getPost_avecIdValide_retourne200() throws Exception {
        PostResponse response = PostResponse.builder()
                .id(1L).title("Mon article").authorUsername("testuser").build();

        when(postService.getPost(1L)).thenReturn(response);

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Mon article"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPost_avecIdInexistant_retourne404() throws Exception {
        when(postService.getPost(99L)).thenThrow(new ResourceNotFoundException("Article non trouvé"));

        mockMvc.perform(get("/api/posts/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPost_sansAuthentification_retourne403() throws Exception {
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isForbidden());
    }

    // ─── POST /api/posts/{id}/comments ────────────────────────────────────────

    @Test
    @WithMockUser(username = "testuser")
    void addComment_avecDonneesValides_retourne200() throws Exception {
        CommentRequest request = new CommentRequest("Bravo !");
        CommentResponse response = CommentResponse.builder()
                .id(1L).content("Bravo !").authorUsername("testuser").build();

        when(postService.addComment(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Bravo !"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void addComment_avecCorpsVide_retourne400() throws Exception {
        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}


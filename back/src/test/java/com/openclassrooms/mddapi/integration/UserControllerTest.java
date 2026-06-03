package com.openclassrooms.mddapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.config.SecurityConfig;
import com.openclassrooms.mddapi.controller.UserController;
import com.openclassrooms.mddapi.dto.UpdateUserRequest;
import com.openclassrooms.mddapi.dto.UserResponse;
import com.openclassrooms.mddapi.security.JwtTokenProvider;
import com.openclassrooms.mddapi.security.UserDetailsServiceImpl;
import com.openclassrooms.mddapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    // ─── GET /api/user/me ────────────────────────────────────────────────────────

    @Test
    void getMe_sansAuthentification_retourne403() throws Exception {
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getMe_avecAuthentification_retourne200AvecProfil() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(1L)
                .email("test@test.com")
                .username("testuser")
                .build();

        when(userService.getCurrentUser()).thenReturn(response);

        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    // ─── PUT /api/user/me ────────────────────────────────────────────────────────

    @Test
    void updateMe_sansAuthentification_retourne403() throws Exception {
        mockMvc.perform(put("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateMe_avecDonneesValides_retourne200() throws Exception {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("nouveau@test.com")
                .username("nouveauuser")
                .build();

        UserResponse response = UserResponse.builder()
                .id(1L)
                .email("nouveau@test.com")
                .username("nouveauuser")
                .build();

        when(userService.updateCurrentUser(any())).thenReturn(response);

        mockMvc.perform(put("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("nouveau@test.com"))
                .andExpect(jsonPath("$.username").value("nouveauuser"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateMe_avecEmailInvalide_retourne400() throws Exception {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("pas-un-email")
                .build();

        mockMvc.perform(put("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

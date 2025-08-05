package com.springboot.mongodb.springmongo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.springboot.mongodb.springmongo.model.User;
import com.springboot.mongodb.springmongo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean   // deprecated, use this later  @MockitoBean and @MockitoSpyBean.
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/users/healthcheck"))
                .andExpect(status().isOk())
                .andExpect(content().string("Service is up and running"));
    }

    @Test
    void testCreateUser() throws Exception {
        User user = new User();
        user.setId("101");
        user.setGender("male");

        when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("101"));
    }

    @Test
    void testGetUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }


    @Test
    void testGetUserById() throws Exception {
        User user = new User();
        user.setId("102");

        when(userService.getUserById("102")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("102"));
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/104"))
                .andExpect(status().isOk());

        verify(userService).deleteUser("104");  // because the return type is void
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        when(userService.isExistsById("999")).thenReturn(false);

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new User())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        User updatedUser = new User();
        updatedUser.setId("103");

        when(userService.isExistsById("103")).thenReturn(true);
        when(userService.saveUser(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/103")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("103"));
    }


    @Test
    void testPatchUserWithMap() throws Exception {
        String id = "103";
        Map<String, Object> updates = Map.of("technology", "Spring Boot");
        User updatedUser = new User();
        updatedUser.setId(id);
        updatedUser.setTechnology("Spring Boot");

        when(userService.isExistsById(id)).thenReturn(true);
        when(userService.patchUser(eq(id), anyMap())).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technology").value("Spring Boot"));
    }

    @Test
    void testPatchUserWithMap_UserNotFound() throws Exception {
        String id = "103";
        Map<String, Object> updates = Map.of("technology", "Spring Boot");

        // Mock the service to say user does NOT exist
        when(userService.isExistsById(id)).thenReturn(false);

        mockMvc.perform(patch("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isNotFound());

        // Verify patchUser is never called
        verify(userService, never()).patchUser(anyString(), anyMap());
    }

    @Test
    void testPatchUserWithJsonPatch() throws Exception {
        String id = "103";
        String jsonPatch = """
        [
          { "op": "replace", "path": "/technology", "value": "Spring Boot" }
        ]
        """;

        User updatedUser = new User();
        updatedUser.setId(id);
        updatedUser.setTechnology("Spring Boot");

        when(userService.isExistsById(id)).thenReturn(true);
        when(userService.jsonPatchUpdate(eq(id), any(JsonPatch.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/users/jsonpatch/{id}", id)
                        .contentType("application/json-patch+json")
                        .content(jsonPatch))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technology").value("Spring Boot"));
    }

    @Test
    void patchUser_jsonPatch_UserNotFound() throws Exception {
        String userId = "123";

        when(userService.isExistsById(userId)).thenReturn(false);

        String patchJson = "[{ \"op\": \"replace\", \"path\": \"/technology\", \"value\": \"Spring\" }]";

        mockMvc.perform(patch("/api/users/jsonpatch/{id}", userId)
                        .contentType("application/json-patch+json")
                        .content(patchJson))
                .andExpect(status().isNotFound());

        verify(userService, never()).jsonPatchUpdate(anyString(), any());
    }

    @Test
    void patchUser_jsonPatch_PatchFails_ReturnsInternalServerError() throws Exception {
        String userId = "123";

        when(userService.isExistsById(userId)).thenReturn(true);
        when(userService.jsonPatchUpdate(eq(userId), any(JsonPatch.class))).thenReturn(null);

        String patchJson = "[{ \"op\": \"replace\", \"path\": \"/technology\", \"value\": \"Spring\" }]";

        mockMvc.perform(patch("/api/users/jsonpatch/{id}", userId)
                        .contentType("application/json-patch+json")
                        .content(patchJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Patch failed: "));

        verify(userService).jsonPatchUpdate(eq(userId), any(JsonPatch.class));
    }
}

package com.springboot.mongodb.springmongo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.springboot.mongodb.springmongo.model.User;
import com.springboot.mongodb.springmongo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class UserServiceTest {


    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        // inject ObjectMapper manually since it's not autowired in the test
        userService = new UserService();
        userService.getClass(); // Just to suppress unused warning
        // manually inject dependencies
        injectDependencies();

    }

    private void injectDependencies() {
        try {
            var objectMapperField = UserService.class.getDeclaredField("objectMapper");
            objectMapperField.setAccessible(true);
            objectMapperField.set(userService, objectMapper);

            var userRepositoryField = UserService.class.getDeclaredField("userRepository");
            userRepositoryField.setAccessible(true);
            userRepositoryField.set(userService, userRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void saveUser_shouldReturnSavedUser() {
        User user = new User();
        user.setId("101");

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.saveUser(user);

        assertEquals("101", result.getId());
        verify(userRepository, times(1)).save(user);
    }


    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        List<User> users = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = new User();
        user.setId("105");
        when(userRepository.findById("105")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById("105");

        assertTrue(result.isPresent());
        assertEquals("105", result.get().getId());
    }

    @Test
    void deleteUser_shouldCallDelete() {
        doNothing().when(userRepository).deleteById("101");

        userService.deleteUser("101");

        verify(userRepository).deleteById("101");
    }

    @Test
    void isExistsById_shouldReturnTrue() {
        when(userRepository.existsById("123")).thenReturn(true);

        assertTrue(userService.isExistsById("123"));
    }

    @Test
    void patchUser_shouldUpdateFields() {
        User existingUser = new User();
        existingUser.setTechnology("Java");
        Map<String, Object> updates = new HashMap<>();
        updates.put("technology", "Python");

        when(userRepository.findById("123")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.patchUser("123", updates);

        assertEquals("Python", updated.getTechnology());
    }

    @Test
    void patchUser_shouldReturnNull_whenUserNotFound() {
        // Given
        String id = "999";
        Map<String, Object> updates = new HashMap<>();
        updates.put("technology", "Go");

        // Simulate user not found
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When
        User result = userService.patchUser(id, updates);

        // Then
        assertNull(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void jsonPatchUpdate_shouldApplyJsonPatchCorrectly() throws Exception {
        // Original User
        User originalUser = new User();
        originalUser.setId("100");
        originalUser.setTechnology("Java");

        // Patch JSON (change technology)
        String patchStr = "[{ \"op\": \"replace\", \"path\": \"/technology\", \"value\": \"Python\" }]";
        JsonPatch patch = objectMapper.readValue(patchStr, JsonPatch.class);

        // Mock repo call
        when(userRepository.findById("100")).thenReturn(Optional.of(originalUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Call service method
        User result = userService.jsonPatchUpdate("100", patch);

        // Assert
        assertNotNull(result);
        assertEquals("Python", result.getTechnology());
        verify(userRepository, times(1)).save(result);
    }


    @Test
    void jsonPatchUpdate_shouldReturnNullOnPatchFailure() throws Exception {
        User originalUser = new User();
        originalUser.setId("100");
        originalUser.setTechnology("Java");

        String invalidPatchStr = "[{ \"op\": \"replace\", \"path\": \"/invalidPath\", \"value\": \"Oops\" }]";
        JsonPatch patch = objectMapper.readValue(invalidPatchStr, JsonPatch.class);

        when(userRepository.findById("100")).thenReturn(Optional.of(originalUser));

        User result = userService.jsonPatchUpdate("100", patch);

        assertNull(result);
        verify(userRepository, never()).save(any());
    }

}

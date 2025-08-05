package com.springboot.mongodb.springmongo.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.mongodb.springmongo.model.User;
import com.springboot.mongodb.springmongo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {


    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/users";
        userRepository.deleteAll(); // clean before test
    }

    // Given: User JSON string
    private final String userJson = """
                {
                  "id": "100",
                  "rollnumber": 10,
                  "name": {
                    "firstname": "Ranjith",
                    "lastname": "Kuruppanthara"
                  },
                  "dateofjoining": "2014-07-23T04:46:35+0000",
                  "age": 34,
                  "gender": "male",
                  "technology": "Java",
                  "favoriteFruit": "strawberry",
                  "company": [
                    {
                      "title": "TCS",
                      "email": "ranjith@tcs.com",
                      "phone": "+1 (949) 568-3470",
                      "location": {
                        "country": "India",
                        "address": [
                          {
                            "address1": "Kottayam",
                            "address2": "Kuruppanthara"
                          },
                          {
                            "address1": "Trivandrum",
                            "address2": "Kazhakkuttam"
                          }
                        ]
                      }
                    },
                    {
                      "title": "INFOSYS",
                      "email": "ranjith@infosys.com",
                      "phone": "+61 2 9374 4000",
                      "location": {
                        "country": "Australia",
                        "address": [
                          {
                            "address1": "Austin1",
                            "address2": "Austinaddress1"
                          },
                          {
                            "address1": "Austin2",
                            "address2": "Austinaddress2"
                          }
                        ]
                      }
                    }
                  ],
                  "skillset": [
                    "java",
                    "angular",
                    "react",
                    "spring"
                  ],
                  "active": true
                }
                """;

    @Test
    void testHealthCheck() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/healthcheck", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Service is up and running", response.getBody());
    }

    @Test
    void testCreateAndGetUser() {


        // When: Create user
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(userJson, headers);
        ResponseEntity<User> response = restTemplate.postForEntity(baseUrl, request, User.class);

        // Then: Check creation success
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("100", response.getBody().getId());
        assertEquals("Java", response.getBody().getTechnology());

        // When: Retrieve all users
        ResponseEntity<User[]> getAllResponse = restTemplate.getForEntity(baseUrl, User[].class);

        assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
        assertTrue(getAllResponse.getBody().length > 0);

    }

    @Test
    void testGetUserById() {
        postUser();

        ResponseEntity<User> response = restTemplate.getForEntity(baseUrl + "/100", User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("100", response.getBody().getId());
    }

    @Test
    void testUpdateUserWithPut() {
        postUser();

        User updated = restTemplate.getForEntity(baseUrl + "/100", User.class).getBody();
        updated.setTechnology("Spring Boot");

        HttpEntity<User> request = new HttpEntity<>(updated);
        ResponseEntity<User> response = restTemplate.exchange(baseUrl + "/100", HttpMethod.PUT, request, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Spring Boot", response.getBody().getTechnology());
    }


    @Test
    void testPatchUserWithMap() {
        postUser();

        Map<String, Object> patch = Map.of("technology", "NodeJS");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(patch, headers);

        ResponseEntity<User> response = restTemplate.exchange(baseUrl + "/100", HttpMethod.PATCH, request, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("NodeJS", response.getBody().getTechnology());
    }

    @Test
    void testPatchUserWithJsonPatch() throws Exception {
        postUser();

        String patchJson = """
                [
                  { "op": "replace", "path": "/technology", "value": "GoLang" }
                ]
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json-patch+json"));
        HttpEntity<String> request = new HttpEntity<>(patchJson, headers);

        ResponseEntity<User> response = restTemplate.exchange(
                baseUrl + "/jsonpatch/100", HttpMethod.PATCH, request, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("GoLang", response.getBody().getTechnology());
    }

    @Test
    void testDeleteUser() {
        postUser();

        restTemplate.delete(baseUrl + "/100");

        ResponseEntity<User> response = restTemplate.getForEntity(baseUrl + "/100", User.class);
        assertNull(response.getBody()); // because .orElse(null) in controller
    }




    private void postUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(userJson, headers);
        restTemplate.postForEntity(baseUrl, request, User.class);
    }
}

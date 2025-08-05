package com.springboot.mongodb.springmongo.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.mongodb.springmongo.model.User;
import com.springboot.mongodb.springmongo.repository.UpdateRepository;
import com.springboot.mongodb.springmongo.request.NestedUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UpdateRepository updateRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String baseUrl;

    private final String userJson = """
            {
              "id": "102",
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
              "skillset": ["java", "angular", "react", "spring"],
              "active": true
            }
            """;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/users/update";
        updateRepository.deleteAll();
        postInitialUser();
    }

    void postInitialUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(userJson, headers);
        restTemplate.postForEntity("http://localhost:" + port + "/api/users", request, User.class);
    }

    @Test
    void testUpdateUserFullBodyTypeOne() {
        // Prepare full user update with technology change
        User updatedUser = updateRepository.findById("102").get();
        updatedUser.setTechnology("Updated Full Body Type One");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> request = new HttpEntity<>(updatedUser, headers);

        ResponseEntity<User> response = restTemplate.exchange(
                baseUrl + "/fullbodyrequest/102", HttpMethod.PUT, request, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Full Body Type One", response.getBody().getTechnology());
    }
    @Test
    void testUpdateUserFullBodyTypeTwo() {
        ResponseEntity<User> response = restTemplate.exchange(
                baseUrl + "/partialrequest/102?address1=New Updated Address", HttpMethod.PUT,
                null, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New Updated Address",
                response.getBody().getCompany().get(1).getLocation().getAddress().get(1).getAddress1());
    }

    @Test
    void testUpdateNestedAddressField() {
        NestedUpdateRequest request = new NestedUpdateRequest();
        request.setCompanyIndex(0);
        request.setAddressIndex(1);
        request.setAddress1("Nested Updated Address");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NestedUpdateRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/nested-address/102", HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated successfully", response.getBody());

        // Verify the change
        User updated = updateRepository.findById("102").get();
        String actual = updated.getCompany().get(0).getLocation().getAddress().get(1).getAddress1();
        assertEquals("Nested Updated Address", actual);
    }

    @Test
    void testUpdateUsingAggregation() {
        String url = baseUrl + "/102/company/1/address/0?address1=AggregationUpdate";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated nested address1 successfully", response.getBody());

        User updated = updateRepository.findById("102").get();
        String address = updated.getCompany().get(1).getLocation().getAddress().get(0).getAddress1();
        assertEquals("Austin1", address);
    }

}

package com.springboot.mongodb.springmongo.controller;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.mongodb.springmongo.model.User;
import com.springboot.mongodb.springmongo.request.NestedUpdateRequest;
import com.springboot.mongodb.springmongo.service.UpdateService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UpdateController.class)
public class UpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean   // deprecated, use this later  @MockitoBean and @MockitoSpyBean.
    private UpdateService updateService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUpdateUserFullBodyTypeOne() throws Exception {
        String userId = "102";
        User user = new User();
        user.setId(userId);
        user.setTechnology("Java");

        when(updateService.updateUser(any(User.class))).thenReturn(user);

        mockMvc.perform(put("/api/users/update/fullbodyrequest/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.technology").value("Java"));
    }

    @Test
    void testUpdateUserFullBodyTypeTwo() throws Exception {
        String userId = "102";
        String address1 = "New Address";

        User updatedUser = new User();
        updatedUser.setId(userId);

        when(updateService.updateUserTypeTwo(userId, address1)).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/update/partialrequest/{id}", userId)
                        .param("address1", address1))
                .andExpect(status().isOk());
    }


    @Test
    void testUpdateNestedAddressField() throws Exception {
        String userId = "102";
        NestedUpdateRequest request = new NestedUpdateRequest();
        request.setCompanyIndex(0);
        request.setAddressIndex(1);
        request.setAddress1("Updated Addr");

        when(updateService.updateNestedAddressField(eq(userId), any(NestedUpdateRequest.class))).thenReturn(true);

        mockMvc.perform(put("/api/users/update/nested-address/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated successfully"));
    }

    @Test
    void testUpdateNestedAddressField_NotFound() throws Exception {
        String userId = "102";

        NestedUpdateRequest request = new NestedUpdateRequest();
        request.setCompanyIndex(0);
        request.setAddressIndex(1);
        request.setAddress1("Non-existent update");

        // Simulate update failure
        when(updateService.updateNestedAddressField(eq(userId), any(NestedUpdateRequest.class)))
                .thenReturn(false);

        mockMvc.perform(put("/api/users/update/nested-address/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User or path not found"));
    }

    @Test
    void testUpdateNestedAddress1() throws Exception {
        String userId = "102";
        int companyIndex = 0;
        int addressIndex = 1;
        String address1 = "Updated Aggregation";

        doNothing().when(updateService).updateUsingAggregationUpdate(userId, companyIndex, addressIndex, address1);

        mockMvc.perform(put("/api/users/update/{id}/company/{companyIndex}/address/{addressIndex}",
                        userId, companyIndex, addressIndex)
                        .param("address1", address1))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated nested address1 successfully"));
    }


}

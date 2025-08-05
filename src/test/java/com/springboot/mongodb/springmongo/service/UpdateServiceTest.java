package com.springboot.mongodb.springmongo.service;

import com.mongodb.client.result.UpdateResult;
import com.springboot.mongodb.springmongo.model.Address;
import com.springboot.mongodb.springmongo.model.Company;
import com.springboot.mongodb.springmongo.model.Location;
import com.springboot.mongodb.springmongo.model.User;
import com.springboot.mongodb.springmongo.repository.UpdateRepository;
import com.springboot.mongodb.springmongo.request.NestedUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateServiceTest {

    @InjectMocks
    private UpdateService updateService;

    @Mock
    private UpdateRepository updateRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateUser_shouldSaveAndReturnUser() {
        User user = new User();
        user.setId("101");
        user.setTechnology("Java");

        when(updateRepository.save(user)).thenReturn(user);

        User result = updateService.updateUser(user);

        assertNotNull(result);
        assertEquals("Java", result.getTechnology());
        verify(updateRepository, times(1)).save(user);
    }

    @Test
    void updateUserTypeTwo_shouldUpdateNestedAddressAndSave() {
        Address address = new Address();
        address.setAddress1("Old");

        Location location = new Location();
        location.setAddress(List.of(new Address(), address)); // index 1

        Company company = new Company();
        company.setLocation(location);

        User user = new User();
        user.setId("103");
        user.setCompany(List.of(new Company(), company)); // index 1

        when(updateRepository.findById("103")).thenReturn(Optional.of(user));
        when(updateRepository.save(any())).thenReturn(user);

        User result = updateService.updateUserTypeTwo("103", "New Address");

        assertNotNull(result);
        assertEquals("New Address", result.getCompany().get(1).getLocation().getAddress().get(1).getAddress1());
        verify(updateRepository).save(user);
    }

    @Test
    void updateNestedAddressField_shouldReturnTrueWhenUpdateApplied() {
        NestedUpdateRequest req = new NestedUpdateRequest();
        req.setCompanyIndex(0);
        req.setAddressIndex(1);
        req.setAddress1("Updated Address");

        UpdateResult mockResult = mock(UpdateResult.class);
        when(mockResult.getModifiedCount()).thenReturn(1L);

        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(mockResult);

        boolean updated = updateService.updateNestedAddressField("102", req);

        assertTrue(updated);
    }

    @Test
    void updateNestedAddressField_shouldReturnFalseWhenNoUpdate() {
        NestedUpdateRequest req = new NestedUpdateRequest();
        req.setCompanyIndex(0);
        req.setAddressIndex(1);
        req.setAddress1("Unchanged");

        UpdateResult mockResult = mock(UpdateResult.class);
        when(mockResult.getModifiedCount()).thenReturn(0L);

        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(mockResult);

        boolean updated = updateService.updateNestedAddressField("999", req);

        assertFalse(updated);
    }

    @Mock
    private ExecutableUpdateOperation.ExecutableUpdate<User> executableUpdate;

    @Mock
    private ExecutableUpdateOperation.UpdateWithUpdate<User> updateWithUpdate;

    @Mock
    private ExecutableUpdateOperation.TerminatingUpdate<User> terminatingUpdate;


    @Test
    void updateUsingAggregationUpdate_shouldExecuteUpdateChainSafely() {
        // Given
        String userId = "123";
        int companyIndex = 0;
        int addressIndex = 1;
        String newAddress1 = "Updated Address";

        Query expectedQuery = Query.query(Criteria.where("id").is(userId));

        // Chain mocks safely
        when(mongoTemplate.update(User.class)).thenReturn(executableUpdate);
        when(executableUpdate.matching(any(Query.class))).thenReturn(updateWithUpdate);
        when(updateWithUpdate.apply(any(AggregationUpdate.class))).thenReturn(terminatingUpdate);
        when(terminatingUpdate.first()).thenReturn(null);  // You’re not asserting return value

        // When
        updateService.updateUsingAggregationUpdate(userId, companyIndex, addressIndex, newAddress1);

        // Then
        verify(mongoTemplate).update(User.class);
        verify(executableUpdate).matching(any(Query.class));
        verify(updateWithUpdate).apply(any(AggregationUpdate.class));
        verify(terminatingUpdate).first();
    }

}

package com.springboot.mongodb.springmongo.service;

import com.springboot.mongodb.springmongo.model.User;
import com.springboot.mongodb.springmongo.repository.UpdateRepository;
import com.springboot.mongodb.springmongo.request.NestedUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Criteria;
import com.mongodb.client.result.UpdateResult;

@Service
public class UpdateService {

    @Autowired
    private UpdateRepository updateRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    public User updateUser(User user){
        updateRepository.save(user);
        return user;
    }

    public User updateUserTypeTwo(String id, String address1){
        User user = updateRepository.findById(id).orElseThrow();
        user.getCompany().get(1).getLocation().getAddress().get(1).setAddress1(address1);
        updateRepository.save(user); // replaces full document in Mongo
        return user;
    }

    public boolean updateNestedAddressField(String id, NestedUpdateRequest req) {
        String path = String.format("company.%d.location.address.%d.address1",
                req.getCompanyIndex(), req.getAddressIndex());
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().set(path, req.getAddress1());
        UpdateResult result = mongoTemplate.updateFirst(query, update, User.class);
        return result.getModifiedCount() > 0;
    }


    public void updateUsingAggregationUpdate(String userId, int companyIndex, int addressIndex, String newAddress1) {
        // Dynamically construct the path to the nested field
        String path = String.format("company.%d.location.address.%d.address1", companyIndex, addressIndex);

        AggregationUpdate update = AggregationUpdate.update()
                .set(path).toValue(newAddress1);

        Query query = Query.query(Criteria.where("id").is(userId));

        mongoTemplate.update(User.class)
                .matching(query)
                .apply(update)
                .first();
    }
}

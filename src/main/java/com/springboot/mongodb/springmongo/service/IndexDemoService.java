package com.springboot.mongodb.springmongo.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class IndexDemoService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void createAllIndexes() {

        // Get the raw MongoDB collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("index");

        //  1. Single-field index on "age"
        collection.createIndex(
                Indexes.ascending("age"),
                new IndexOptions().name("age_index")
        );

        //  2. Compound index on "name" and "email"
        collection.createIndex(
                Indexes.compoundIndex(
                        Indexes.ascending("name"),
                        Indexes.descending("email")
                ),
                new IndexOptions().name("name_email_compound")
        );

        //  3. TTL Index on "expiryDate" (auto delete after 1 hour)
//        collection.createIndex(
//                Indexes.ascending("expiryDate"),
//                new IndexOptions()
//                        .expireAfter(3600L, TimeUnit.SECONDS)
//                        .name("ttl_expiryDate")
//        );

        //  4. Text Index on "bio"
        collection.createIndex(
                Indexes.text("bio"),
                new IndexOptions().name("bio_text_index")
        );

        //  5. Geospatial Index on "location"
        collection.createIndex(
                Indexes.geo2dsphere("location"),
                new IndexOptions().name("location_geo_index")
        );

        System.out.println(" All indexes created using MongoDB driver.");
    }

    //  Optionally: List all indexes
    public void listIndexes() {
        MongoCollection<Document> collection = mongoTemplate.getCollection("index");
        System.out.println(" Current Indexes:");
        collection.listIndexes().forEach(doc -> System.out.println(doc.toJson()));
    }
}
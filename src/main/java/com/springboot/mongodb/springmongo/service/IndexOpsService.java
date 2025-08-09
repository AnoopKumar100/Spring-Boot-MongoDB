package com.springboot.mongodb.springmongo.service;

import com.springboot.mongodb.springmongo.model.IndexStudy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.stereotype.Service;

import java.util.List;

//3. indexops  //  it is used to create indexes

    /*

    The mongoTemplate.indexOps() method in Spring Data MongoDB is used to create, drop, or inspect indexes on a MongoDB collection.
    Why Use indexOps()?
    Create custom indexes (e.g., ascending, descending, compound, text, geo)
    Drop specific indexes
    List existing indexes
    Ensure indexes programmatically (not just via annotations)

Method Signature

IndexOperations indexOps(Class<?> entityClass);
IndexOperations indexOps(String collectionName);

Overview of Index Types Covered:
Index Type	                                        Purpose
Single field index	                            Simple field indexing
Compound index	                                Index on multiple fields
Text index	                                    Full-text search
TTL index	                                    Auto-expire documents
Geospatial index	                            Location-based geo queries




     */



@Service
public class IndexOpsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void createAllIndexes() {

        //  1. Single-field index on "age"
        mongoTemplate.indexOps(IndexStudy.class)
                .createIndex(new Index().on("age", Sort.Direction.ASC).named("age_index"));

        //  2. Compound index on "name" and "email" (already in @CompoundIndex annotation)
        // This is optional if already annotated. You can create programmatically like this:
        mongoTemplate.indexOps(IndexStudy.class)
                .createIndex(new Index().on("name", Sort.Direction.ASC).on("email", Sort.Direction.DESC).named("name_email_compound"));

        //  3. TTL Index on expiryDate (auto delete documents after 1 hour)
        mongoTemplate.indexOps(IndexStudy.class)
                .createIndex(new Index().on("expiryDate", Sort.Direction.ASC).expire(3600).named("ttl_expiryDate"));

        //  4. Text Index on "bio"
        TextIndexDefinition textIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                .onField("bio")
                .withDefaultLanguage("english")
                .named("bio_text_index")
                .build();
        mongoTemplate.indexOps(IndexStudy.class).createIndex(textIndex);

        //  5. Geospatial Index on "location"


        System.out.println(" All indexes created!");
    }

    // 🧾 Optionally: List all indexes
    public List<IndexInfo> listIndexes() {
        List<IndexInfo> indexList = mongoTemplate.indexOps(IndexStudy.class).getIndexInfo();
        for (IndexInfo index : indexList) {
            System.out.println("Index: " + index.getName() + ", Fields: " + index.getIndexFields());
        }

        return indexList;
    }
}

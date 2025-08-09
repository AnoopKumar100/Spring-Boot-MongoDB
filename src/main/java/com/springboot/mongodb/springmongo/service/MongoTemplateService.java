package com.springboot.mongodb.springmongo.service;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.lang.NonNullApi;
import com.mongodb.lang.Nullable;
import com.springboot.mongodb.springmongo.model.User;
import lombok.NonNull;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MongoTemplateService {

    @Autowired
    private MongoTemplate mongoTemplate;


    public User saveWithObject(User user) {
        return mongoTemplate.save(user);
    }

    public User saveWithObjectAndCollectionName(User user, String collectionName) {
        return mongoTemplate.save(user,collectionName);
    }


    public User findByIdWithObjectAndEntityClass(String id) {
        return mongoTemplate.findById( id,  User.class);
    }

    public User findByIdWithObjectAndEntityClassAndCollectionName(String id, String collectionName) {
        return mongoTemplate.findById( id,  User.class, collectionName);
    }


    public List<User> findByQueryAndEntityClass(String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name.firstname").is(name));
        return mongoTemplate.find(query,User.class);
    }

    public List<User> findByQueryAndEntityClassAndCollection(String name, String collectionName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name.firstname").is(name));
        return mongoTemplate.find(query,User.class,collectionName);
    }


    public List<User> findAllFromEntityClass() {
        return mongoTemplate.findAll(User.class);
    }



    public List<User> findAllFromEntityClassAndCollection(String collectionName) {
        return mongoTemplate.findAll(User.class, collectionName);
    }


    public List<User> findAllAndRemove() {
        Query query = new Query();
        query.addCriteria(Criteria.where("age").gt(39));
        List<User> removedUsers = mongoTemplate.findAllAndRemove(query, User.class);

        //Also we have
       // mongoTemplate.findAllAndRemove(query, collection);
       // mongoTemplate.findAllAndRemove(query, Entity class, collection);

        return removedUsers;
    }


    public List<String> findDistinctNames() {
        Query query = new Query();
        query.addCriteria(Criteria.where("age").gt(35));
        List<String> distinctNames = mongoTemplate.findDistinct(query, "name.firstname", User.class, String.class );

        //Also we have
        //mongoTemplate.findDistinct(query, filed, collectionName, Entity class, result class );
        //mongoTemplate.findDistinct(query, filed, collectionName,  result class );
        //mongoTemplate.findDistinct(filed,  Entity class, result class );
        return  distinctNames;
    }

    public User findAndModify(String name,String newAge) {
        Query query = new Query(Criteria.where("name.firstname").is(name));
        UpdateDefinition update = new Update().set("age", newAge);
        FindAndModifyOptions options = new FindAndModifyOptions()
                .returnNew(true) // return the updated document
                .upsert(false);  // do not insert if not found
        return mongoTemplate.findAndModify(query, update, options, User.class);

        //Also we have

        // mongoTemplate.findAndModify(query, update, options, User.class,collectionName);
        //mongoTemplate.findAndModify(query, update,Entityclass, collectionName);
        //mongoTemplate.findAndModify(query, update,Entityclass, Entityclass);
    }


    public User findAndRemove(String name) {
        Query query = new Query(Criteria.where("name").is(name));
         return mongoTemplate.findAndRemove(query, User.class);

         //Also we have many findAndRemove   with different arguments, please do it once get time.
    }

    //Find a document, replace it entirely with a new one, and return either the old or new document, depending on your options.
    //You want to replace an entire document in the MongoDB collection (not just update a few fields), and also get back the result.
    public User findAndReplace(String name, User newUser) {
        Query query = new Query(Criteria.where("name.firstname").is(name));

        FindAndReplaceOptions options = FindAndReplaceOptions.options()
                .returnNew(); // return the new (replaced) document
        return mongoTemplate.findAndReplace(query, newUser, options, User.class,User.class);
        // Also we have many findAndReplace()  with different arguments.

    }


    //It finds and returns the first matching document based on the provided query.
    public User findOne(String name) {
        Query query = new Query(Criteria.where("name.firstname").is(name));
        return mongoTemplate.findOne(query, User.class);

        // Also we have another findOne. with different argument.
    }


    /*

    Difference Between save() and insert()
Feature	                                     save()	                                                                insert()
Purpose                         	Insert or update a document	                                        Only insert a new document
If _id exists	                    Performs an update (replace the whole document)             	    Throws a DuplicateKeyException
If _id is missing	                Inserts a new document with a generated _id	                        Inserts a new document with a generated _id
Usage	                            Safe for "upsert" behavior (insert if new, update if existing)	    Safe only if you are sure the document is new



     */

    public User insert(User user) {
        return mongoTemplate.insert( user);
        // Also we have one more insert collection  another argument.


    }



    //boolean collectionExists(Class<?> entityClass);
    //boolean collectionExists(String collectionName);
    public boolean collectionExists() {
        return mongoTemplate.collectionExists(User.class);
    }


    //boolean collectionExists(Class<?> entityClass);
    //boolean collectionExists(String collectionName);
    //It checks if any document exists that matches a given Query.
    public boolean exists(String name) {
        Query query = new Query(Criteria.where("name.firstname").is(name));
        return mongoTemplate.exists(query, User.class);
    }


    //It returns the number of documents that match a given query in a collection.

    //long count(Query query, Class<?> entityClass);
    //long count(Query query, String collectionName);
    public long count(int age) {
        Query query = new Query(Criteria.where("age").gt(age));
        return mongoTemplate.count(query, User.class);

    }

    /*



    // 1. Create collection by entity class
<T> MongoCollection<Document> createCollection(Class<T> entityClass);

// 2. Create collection by name
MongoCollection<Document> createCollection(String collectionName);

// 3. Create collection with options
<T> MongoCollection<Document> createCollection(Class<T> entityClass, CollectionOptions options);
    MongoCollection<Document> createCollection(String collectionName, CollectionOptions options);
     */

    public String  createCollection() {
        if (!mongoTemplate.collectionExists("event_logs")) {
            // Set max size and capped = true (for a capped collection)
            CollectionOptions options = CollectionOptions.empty()
                    .capped()               // enable capped collection
                    .size(1048576)          // max size in bytes (1MB)
                    .maxDocuments(1000);    // max number of documents

            mongoTemplate.createCollection("event_logs", options);

        }
        return " Collection created successfully";
    }

    // No time, so skipping this

        //mongoTemplate.getCollection()
        //mongoTemplate.getCollectionName()
         //mongoTemplate.getCollectionNames()


    //Returns a fast, approximate count of documents in the specified collection — using collection metadata, not a full scan.
    public long estimatedCount() {
        return mongoTemplate.estimatedCount("users");
    }

    //This method is used when you want to accurately count documents that match a query
    public long exactCount() {
        Query query = new Query(Criteria.where("age").gt(25));
        return mongoTemplate.exactCount(query, User.class);

        // Also we have many with different arguments.
    }


    //mongoTemplate.getDb()  return the DB name , not tested due to time issue.


    //mongoTemplate.remove() is used to delete documents from a MongoDB collection based on a filter/query.
    // Not tested due to time issue
    public boolean remove(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        DeleteResult result = mongoTemplate.remove(query, User.class);
        return result.getDeletedCount() > 0;

        // also we have many remove with different arguments.
    }

    /*
    Behavior
    If a user with name = oldName exists → it gets replaced with newUser.

    If no match is found → because of upsert(), it gets inserted.

    Not tested due to time issue
    */
    public UpdateResult findAllFromEntityClassArndCollection(String oldName, User newUser) {
        // Find user with old name
        Query query = new Query(Criteria.where("name.firstname").is(oldName));

        // Replace it with the new user object
       return mongoTemplate.replace(query, newUser, ReplaceOptions.replaceOptions());

        // And also we have many with different arguments.
    }

    /*

    Typical Use Cases:
    Update one or many documents.

    Set, unset, increment fields.

    Use advanced Update operators like $set, $inc, $rename, etc.
     */

    // Not tested due to time issue.

    public long update(String name, int newAge) {
        Query query = new Query(Criteria.where("name.firstname").is(name));

        Update update = new Update().set("age", newAge);

        // Fluent API for updating one document
        UpdateResult result = mongoTemplate
                .update(User.class)              // Define target entity
                .matching(query)                 // Apply query filter
                .apply(update)                   // Apply update definition
                .first();                        // Update only the first match

        // Also we have lot of update, need to explore more.

        return result.getModifiedCount();     // Return how many documents were modified
    }

        //mongoTemplate.updateFirst() is used in Spring Data MongoDB to update the first document that matches a given query.
        // Not tested due to time issue.
        public long updateFirst(String name) {
            Query query = new Query(Criteria.where("name.firstname").is("Alice"));
            Update update = new Update().set("age", 35);

            UpdateResult result = mongoTemplate.updateFirst(query, update, User.class);
            return result.getModifiedCount(); // returns how many documents were modified

            //Also we have another updateFirst with different arguments.
        }

    //mongoTemplate.updateMulti() is used to update multiple documents that match a query in a MongoDB collection.

    // Couldn't test due to timeout issue.
    public long updateMulti(String name) {

        Query query = new Query(Criteria.where("age").lt(18));
        Update update = new Update().set("favoriteFruit", "Apple");

        UpdateResult result = mongoTemplate.updateMulti(query, update, User.class);
        // Also we have many other, with different argument type, need to explore more.
        return result.getModifiedCount();
    }


    //mongoTemplate.insertAll() is used to insert a list of documents into their respective MongoDB collections in a single operation.

    // Couldn't test due to time issue.
    public List<User> insertAll() {
        List<User> users = Arrays.asList(
                new User(),
                new User(),
                new User()
        );

        // insertAll returns the inserted documents (with _id populated)
        return (List<User>) mongoTemplate.insertAll(users);
    }



       // mongoTemplate.createCollection() is used to manually create a new MongoDB collection from your Spring Boot application.
        // mongoTemplate.createCollection()

   // MongoCollection<Document> createCollection(String collectionName);
   // <T> MongoCollection<Document> createCollection(Class<T> entityClass);
   // <T> MongoCollection<Document> createCollection(Class<T> entityClass, CollectionOptions options);

    // Not tested due to time issue
    public String createCustomCollection(String name) {
        if (!mongoTemplate.collectionExists(name)) {
            MongoCollection<Document> collection = mongoTemplate.createCollection(name);
            return "Collection created: " + name;
        } else {
            return "Collection already exists: " + name;
        }
    }

    //save and upsert, I am ok with this
         //mongoTemplate.upsert()
        //mongoTemplate.save()




   // mongoTemplate.dropCollection() is used to delete (drop) an entire collection from your MongoDB database.

    //void dropCollection(String collectionName);
   // <T> void dropCollection(Class<T> entityClass);


    // =============================  Easy types completed, need to work on some complex types below........... ====================================================

//execute
//=======

  //Need to override the method "doInCollection" in the interface  "CollectionCallback "

/*
mongoTemplate.execute() gives you low-level access to the MongoDB driver,
allowing you to perform custom operations directly on a collection that may not be supported by higher-level MongoTemplate methods.

   <T> T execute(Class<?> entityClass, CollectionCallback<T> action);

    entityClass – the class mapped to a MongoDB collection
    CollectionCallback<T> – a functional interface that lets you execute custom logic using the low-level MongoCollection<Document>

Use Cases
========

Native MongoDB operations not available in MongoTemplate
Complex queries or commands
Working with raw MongoDB driver APIs
 */

    // ======== USAGE 1 ==================

    public long customCount() {
        return mongoTemplate.execute(User.class, new CollectionCallback<Long>() {
            @Override
            @NonNull
            public Long doInCollection(@NonNull MongoCollection<Document> collection) {
                return collection.countDocuments();  // Raw MongoDB driver usage
            }
        });
    }

// =======  USAGE 2 ====================================================================================================================

    public long countWithLambda() {
        return mongoTemplate.execute(User.class, MongoCollection::countDocuments);
    }


 // =======  USAGE 3 ====================================================================================================================


    public List<Document> countUsersByStatusOver25() {
        return mongoTemplate.execute(User.class, new CollectionCallback<List<Document>>() {
            @Override
            @NonNull
            public List<Document> doInCollection(@NonNull MongoCollection<Document> collection) {
                List<Document> pipeline = Arrays.asList(
                        new Document("$match", new Document("age", new Document("$gt", 25))),
                        new Document("$group", new Document("_id", "$gender")
                                .append("count", new Document("$sum", 1)))
                );

                AggregateIterable<Document> results = collection.aggregate(pipeline);

                List<Document> output = new ArrayList<>();
                for (Document doc : results) {
                    output.add(doc);
                }
                return output;
            }
        });
    }

//===============================================================================================================================

//   2. bulkOps

/*

mongoTemplate.bulkOps() is used in Spring Data MongoDB to perform bulk write operations like:
Insert
Update
Delete
in a single batch, which is much faster and more efficient than doing them one at a time.

Why Use bulkOps()?
Executes multiple write operations in one network round-trip
Supports ordered or unordered execution
Useful for large-scale data changes

Options:
ORDERED – Stops on first error
UNORDERED – Continues despite errors (faster)


 */


        public BulkWriteResult bulkOps() {
            BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, User.class);

            // 1. Insert new users
            List<User> newUsers = Arrays.asList(
                    new User(),
                    new User()
            );
            ops.insert(newUsers);

            // 2. Update users where age > 20
            Query updateQuery = new Query(Criteria.where("age").gt(20));
            Update update = new Update().set("favoriteFruit", "Mango");
            ops.updateMulti(updateQuery, update);

            // 3. Delete users with status "inactive"
            Query deleteQuery = new Query(Criteria.where("status").is("inactive"));
            ops.remove(deleteQuery);

            //  Execute the batch
            return ops.execute();
        }


//==============================================================================================================














}

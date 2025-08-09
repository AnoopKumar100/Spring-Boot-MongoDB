package com.springboot.mongodb.springmongo.controller;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.springboot.mongodb.springmongo.model.User;
import com.springboot.mongodb.springmongo.service.MongoTemplateService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/mongotemplate")
public class MongoTemplateController {

    @Autowired
    private MongoTemplateService mongoTemplateService;    // For using mongo template , no repository required.

    //http://localhost:8081/api/users/mongotemplate/saveWithObject
    // This will save the user to db, no repository required.
    @PostMapping("/saveWithObject")
    public User saveWithObject(@RequestBody User user){
        return mongoTemplateService.saveWithObject(user);
    }

    //http://localhost:8081/api/users/mongotemplate/saveWithObjectAndCollectionName
   // Note: Here passing the collectionname also, it will be saved into that collection, here object user and
    //we have given @Document(collection = "users")  in the user class, but we are overriding that and saving into employee collection
    //if employee collection is there, it will save otherwise it will create the collection and save.
    @PostMapping("/saveWithObjectAndCollectionName")
    public User saveWithObjectAndCollectionName(@RequestBody User user){
        return mongoTemplateService.saveWithObjectAndCollectionName(user, "employee");
    }

    //http://localhost:8081/api/users/mongotemplate/findByIdWithObjectAndEntityClass/100
    //Note: here passing the id only , but we gave entity class name.see service method.
    @GetMapping("/findByIdWithObjectAndEntityClass/{id}")
    public User findByIdWithObjectAndEntityClass( @PathVariable String id){
        return mongoTemplateService.findByIdWithObjectAndEntityClass(id);
    }

    //http://localhost:8081/api/users/mongotemplate/findByIdWithObjectAndEntityClassAndCollectionName/105
    //Note: here we are adding collection name also, it will pick up from employee collection, Since USer class pointed to
    //@Document(collection = "users") but it will take it from employee collection.
    @GetMapping("/findByIdWithObjectAndEntityClassAndCollectionName/{id}")
    public User findByIdWithObjectAndEntityClassAndCollectionName( @PathVariable String id){
        return mongoTemplateService.findByIdWithObjectAndEntityClassAndCollectionName(id,"employee");
    }

    //http://localhost:8081/api/users/mongotemplate/findByQueryAndEntityClass/Ranjith
    //Note: using query and collection name for search a name, Anything can do on this query.
    @GetMapping("/findByQueryAndEntityClass/{name}")
    public List<User> findByQueryAndEntityClass(@PathVariable String name){
        return mongoTemplateService.findByQueryAndEntityClass(name);
    }


    //http://localhost:8081/api/users/mongotemplate/findByQueryAndEntityClassAndCollection/Soumya1
    //Note: here passing collection name also, it will search in that collection
    @GetMapping("/findByQueryAndEntityClassAndCollection/{name}")
    public List<User> findByQueryAndEntityClassAndCollection(@PathVariable String name){
        return mongoTemplateService.findByQueryAndEntityClassAndCollection(name, "employee");
    }


    //http://localhost:8081/api/users/mongotemplate/findAllFromEntityClass
    //Note: It will take all records from a default collection ie from user
    @GetMapping("/findAllFromEntityClass")
    public List<User> findAllFromEntityClass(){
        return mongoTemplateService.findAllFromEntityClass();
    }


    //http://localhost:8081/api/users/mongotemplate/findAllFromEntityClassAndCollection
    //Note: It will take all records from employee collection, not from users
    @GetMapping("/findAllFromEntityClassAndCollection")
    public List<User> findAllFromEntityClassAndCollection(){
        return mongoTemplateService.findAllFromEntityClassAndCollection("employee");
    }

    //http://localhost:8081/api/users/mongotemplate/findAllAndRemove
    //Note: This is not tested, hope it works
    @GetMapping("/findAllAndRemove")
    public List<User> findAllAndRemove(){
        return mongoTemplateService.findAllAndRemove();
    }

    //http://localhost:8081/api/users/mongotemplate/findDistinctNames
    @GetMapping("/findDistinctNames")
    public List<String> findDistinctNames(){
        return mongoTemplateService.findDistinctNames();
    }

    //http://localhost:8081/api/users/mongotemplate/findAndModify/Ranjith?age=60
    @PatchMapping ("/findAndModify/{name}")
    public User findAndModify(@PathVariable String name, @RequestParam String age){
        return mongoTemplateService.findAndModify(name, age);
    }

    //http://localhost:8081/api/users/mongotemplate/findAndModify/Ranjith
    //Note: The document will be removed if the name matches with Ranjith, since it is removal, haven't tested.
    @PatchMapping ("/findAndRemove/{name}")
    public User findAndRemove(@PathVariable String name){
        return mongoTemplateService.findAndRemove(name);
    }

    //Find a document, replace it entirely with a new one, and return either the old or new document, depending on your options.
    //You want to replace an entire document in the MongoDB collection (not just update a few fields), and also get back the result.
    // Very important, Here the entire document will be replaced but id should be same,

    //http://localhost:8081/api/users/mongotemplate/findAndReplace/Ranjith
    // use the person4.json as the request body,
    //// Very important, Here the entire document will be replaced but id should be same, means id will no replace but everything else.
    @PostMapping ("/findAndReplace/{name}")
    public User findAndReplace(@PathVariable String name, @RequestBody User user){
        return mongoTemplateService.findAndReplace(name, user);
    }


    //http://localhost:8081/api/users/mongotemplate/findOne/Ranjith
    //It finds and returns the first matching document based on the provided query.
    @GetMapping ("/findOne/{name}")
    public User findOne(@PathVariable String name){
        return mongoTemplateService.findOne(name);
    }


    //http://localhost:8081/api/users/mongotemplate/insert

    // see the diff of insert and save in service class - there is a comparison
    // it should insert for new doc, use person6.json as request body
    @PostMapping ("/insert")
    public User insert(@RequestBody User user){
        return mongoTemplateService.insert( user);
    }

    //http://localhost:8081/api/users/mongotemplate/collectionExists
    @GetMapping ("/collectionExists")
    public boolean collectionExists(){
        return mongoTemplateService.collectionExists();
    }


    //http://localhost:8081/api/users/mongotemplate/exists/Soumya1
    @GetMapping ("/exists/{name}")
    public boolean exists(@PathVariable String name){
        return mongoTemplateService.exists(name);
    }

    //It returns the number of documents that match a given query in a collection.
    //http://localhost:8081/api/users/mongotemplate/count/39
    @GetMapping ("/count/{age}")
    public long count(@PathVariable Integer age){
        return mongoTemplateService.count(age);
    }

    //http://localhost:8081/api/users/mongotemplate/createCollection
    @PostMapping ("/createCollection")
    public String createCollection(){
        return mongoTemplateService.createCollection();

    }

    //Returns a fast, approximate count of documents in the specified collection — using collection metadata, not a full scan.
    //http://localhost:8081/api/users/mongotemplate/estimatedCount
    @GetMapping ("/estimatedCount")
    public long estimatedCount(){
        return mongoTemplateService.estimatedCount();
    }

    //This method is used when you want to accurately count documents that match a query
    //http://localhost:8081/api/users/mongotemplate/exactCount
    @GetMapping ("/exactCount")
    public long exactCount(){
        return mongoTemplateService.exactCount();
    }

//    ===================================================   Usage of complex Types  ==================================================

 //  1. execute

    //http://localhost:8081/api/users/mongotemplate/customCount

    @GetMapping ("/customCount")
    public long customCount(){
        return mongoTemplateService.customCount();
    }

    //http://localhost:8081/api/users/mongotemplate/countWithLambda

    @GetMapping ("/countWithLambda")
    public long countWithLambda(){
        return mongoTemplateService.countWithLambda();
    }

    //http://localhost:8081/api/users/mongotemplate/countUsersByStatusOver25
    @GetMapping ("/countUsersByStatusOver25")
    public List<Document> countUsersByStatusOver25(){
        return mongoTemplateService.countUsersByStatusOver25();
    }

    //========================================================================================================================
    //  2. bulkOps
    //http://localhost:8081/api/users/mongotemplate/bulkOps
    @GetMapping ("/bulkOps")
    public BulkWriteResult bulkOps(){
        return mongoTemplateService.bulkOps();
    }


}

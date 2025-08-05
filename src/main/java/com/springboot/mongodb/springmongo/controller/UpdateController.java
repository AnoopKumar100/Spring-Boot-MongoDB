package com.springboot.mongodb.springmongo.controller;

import com.springboot.mongodb.springmongo.model.User;
import com.springboot.mongodb.springmongo.request.NestedUpdateRequest;
import com.springboot.mongodb.springmongo.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/update")
public class UpdateController {

    @Autowired
    private UpdateService updateService;

    /*

Why PUT?
PUT is used to update or replace an existing resource at a known URI.

It is idempotent — sending the same request multiple times results in the same state. for update always use PUT
 */



 // ====================================  TYPE 1   Loading full object ====================================================

   /* 1. Direct Update via Repository Save (Full Replace)


    If you modify the object in Java and call save(),
    Mongo will replace the whole document (or subdocument if embedded).

    Pros:Simple
    Cons: Replaces entire object — not efficient for frequent or partial updates
     */


    /**
     * Here we are passing the full body, and the entire document will be replaced
     * use person3.json for request body, and change something, this will update but in effect the entire document will update.
     * @param id
     * @param user
     * @return
     */

    //http://localhost:8081/api/users/update/fullbodyrequest/102
    @PutMapping("/fullbodyrequest/{id}")
    public User updateUserFullBodyTypeOne(@PathVariable String id, @RequestBody User user) {
        user.setId(id);
        return updateService.updateUser(user);  // replace full document in Mongo.
    }


    //http://localhost:8081/api/users/update/partialrequest/102?address1=Updated Address by updateUserTypeFour

    @PutMapping("/partialrequest/{id}")
    public User updateUserFullBodyTypeTwo(@PathVariable String id, @RequestParam(defaultValue = "", required = false, name = "address1") String address1) {
        return updateService.updateUserTypeTwo(id, address1);  // replace full document in Mongo.
    }

//=============================================== TYPE 2  Efficient, no need to load full object, Updates only the target field =====================

   //GOAL  from person2.json

    //"company[0].location.address[1].address1" = "Updated Trivandrum"

    //For this, we need a class like this below.

    /*
    public class NestedUpdateRequest {
    private int companyIndex;
    private int addressIndex;
    private String address1;
}

and the request body should be below.

{
  "companyIndex": 0,
  "addressIndex": 1,
  "address1": "Updated Trivandrum"
}

//http://localhost:8081/api/users/update/nested-address/102
     */


    @PutMapping("/nested-address/{id}")
    public ResponseEntity<?> updateAddressField(
            @PathVariable String id,
            @RequestBody NestedUpdateRequest request) {

        boolean updated = updateService.updateNestedAddressField(id, request);
        if (updated) {
            return ResponseEntity.ok("Updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or path not found");
        }
    }

 //===========================  TYPE 3  using AggregationUpdate

    // PUT: http://localhost:8081/api/users/update/102/company/0/address/1?address1=Updated AggregationIndex
    @PutMapping("/{id}/company/{companyIndex}/address/{addressIndex}")
    public ResponseEntity<String> updateNestedAddress1(
            @PathVariable String id,
            @PathVariable int companyIndex,
            @PathVariable int addressIndex,
            @RequestParam String address1) {

        updateService.updateUsingAggregationUpdate(id, companyIndex, addressIndex, address1);
        return ResponseEntity.ok("Updated nested address1 successfully");
    }




}

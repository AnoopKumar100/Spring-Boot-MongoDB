package com.springboot.mongodb.springmongo.controller;

import com.github.fge.jsonpatch.JsonPatch;
import com.springboot.mongodb.springmongo.model.User;
import com.springboot.mongodb.springmongo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("healthcheck")
    public String healthCheck() {
        return "Service is up and running";
    }


    //http://localhost:8081/api/users   and request body should be person1.json, person2.json, person3.json  in the resources folder
    @PostMapping
    public User createUser(@RequestBody User user) {
        System.out.println("Received from Postman: LML 333  " + user);
        return userService.saveUser(user);
    }

    //http://localhost:8081/api/users
    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    //http://localhost:8081/api/users/102
    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return userService.getUserById(id).orElse(null);
    }

    //http://localhost:8081/api/users/102
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }


    // PUT – Replace the entire user,  change any value in request body (use person3.json), the entire document will be replaced for that particular id.
    //http://localhost:8081/api/users/102
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        if (!userService.isExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        updatedUser.setId(id); // Keep the original ID

        return ResponseEntity.ok(userService.saveUser(updatedUser));  // save user is just replacing.., This will work in post also, we are calling the save method rt
    }






    // PATCH – Partial update
    //The PATCH endpoint uses Java Reflection for dynamic updates. It works for top-level fields only (like active, age, etc).
    //http://localhost:8081/api/users/102
    /*

    Request body is below.

    {
    "age": 40,
     "active": true
    }

     */
    @PatchMapping("/{id}")
    public ResponseEntity<User> patchUser(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        if (!userService.isExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.patchUser(id, updates));
    }






    // For inner level update, use json patch , dependency also need to add separate.

   // http://localhost:8081/api/users/jsonpatch/102

   // request body is below.

    /*

    [
      { "op": "replace", "path": "/company/1/email", "value": "updated@ibs.com" },
      { "op": "replace", "path": "/company/0/location/address/1/address1", "value": "Updated Trivandrum" },
      { "op": "add", "path": "/skillset/-", "value": "postgresql" }
    ]
     */

    // Note: header should have   Content-Type:     application/json-patch+json

    @PatchMapping(path = "/jsonpatch/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchUser(@PathVariable String id, @RequestBody JsonPatch patch) {
        if (!userService.isExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        User user = userService.jsonPatchUpdate( id, patch);
        if(null == user){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Patch failed: ");
        }else{
            return ResponseEntity.ok(user);
        }
    }


    //==================================================== JSON PATCH OPERATION =================================================
    // Json patch operation study is below.

    // http://localhost:8081/api/users/jsonpatch/103
/*
    JSON Patch Operations (All 6)
    Operation	Description
    add	        Adds a new value to the specified location in the target JSON.
    remove	    Removes the value at the specified path.
    replace	    Replaces the value at the specified path (the path must exist).
    move	    Moves a value from one path to another.
    copy	    Copies a value from one path to another.
    test	    Tests that a value at a given path is equal to the expected value.


    Need to apply all these into person_jsonPatch_test.json

*/

 /*

 1. add

 Add a new address in first company:

[
   { "op": "add", "path": "/company/0/location/address/-", "value": {
      "address1": "NewArea1",
      "address2": "NewLandmark1"
    }
  }
]


===================================
The below is not working because nickname is not available in model class.
So we can use @JsonAnySetter annotation here.
see the below code in User.java

private Map<String, Object> extras = new HashMap<>();

    @JsonAnySetter
    public void setExtraField(String key, Object value) {
        extras.put(key, value);
    }

If try to add any field like "nickname" using json patch , it will add into the map "extras" and will be added into the collection.

[
  { "op": "add", "path": "/nickname", "value": "Bibin" }
]

in collection, it should looks like,

 "extras": {
    "nickname": "Bibin"
  }

And we can access like below.
Object nickname = user.getExtras().get("nickname");
if (nickname != null) {
    System.out.println("Nickname: " + nickname);
}



2. remove
Remove the 2nd company:

[
   { "op": "remove", "path": "/company/1" }
]




3. replace
Update rollnumber, update first company email, and change skillset item:


[
  { "op": "replace", "path": "/rollnumber", "value": 100 },
  { "op": "replace", "path": "/company/0/email", "value": "bibin.updated@ust.com" },
  { "op": "replace", "path": "/skillset/5", "value": "csharp" }
]



4. move
Move skill (index 6) to position 2:

[
  { "op": "move", "from": "/skillset/6", "path": "/skillset/2" }
]



5. copy

JSON Patch Request (copy):

[
  {
    "op": "copy",
    "from": "/technology",
    "path": "/favoriteFruit"
  }
]

So the favoriteFruit will be updated with whatever value in the technology and technology remains same.


Use the below patch body,

[
  {
    "op": "copy",
    "from": "/technology",
    "path": "/primaryTechnology"
  }
]


Since primaryTechnology is not available in user model, then it will go to extras map



6. test
Test that age is 39 before updating gender:

[
  { "op": "test", "path": "/age", "value": 39 },
  { "op": "replace", "path": "/gender", "value": "Female" }
]


If the age is 39, then it will update the gender value with Female

===================================================================================================================================================

HEAD  and OPTIONS request type

🔹 1. HEAD Request
Purpose:
Like GET, but only retrieves headers, not the body.

Used to check resource availability, size, or metadata.

output
======
Status: 200 OK
Content-Type: application/json
Content-Length: 356



 2. OPTIONS Request
 Purpose:
Ask the server: "What methods and features do you support on this resource?"

Often used by browsers in CORS preflight to see if cross-origin requests are allowed.


output
======
Allow: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PATCH

  */

}

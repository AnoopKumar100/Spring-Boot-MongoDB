package com.springboot.mongodb.springmongo.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;
    @JsonProperty("rollnumber")
    private int rollNumber;
    private Name name;
    @JsonProperty("dateofjoining")
    private String dateOfJoining;
    private int age;
    private String gender;
    private String technology;
    private String favoriteFruit;
    private List<Company> company;

    @JsonProperty("skillset")
    private List<String> skillSet;
    private boolean active;

    private Map<String, Object> extras = new HashMap<>();

    @JsonAnySetter
    public void setExtraField(String key, Object value) {
        extras.put(key, value);
    }
}

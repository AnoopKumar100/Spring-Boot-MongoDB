package com.springboot.mongodb.springmongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    private String country;
    private List<Address> address;
}

package com.springboot.mongodb.springmongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {
    private String title;
    private String email;
    private String phone;
    private Location location;
}
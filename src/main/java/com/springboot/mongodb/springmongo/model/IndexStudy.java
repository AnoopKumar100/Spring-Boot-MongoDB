package com.springboot.mongodb.springmongo.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.geo.Point;

import java.util.Date;

@Document("index")
@CompoundIndex(def = "{'name': 1, 'email': -1}", name = "name_email_idx")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexStudy {

    @Id
    private String id;

    @Indexed  //  Single field ascending index
    private String email;

    private String name;

    private int age;

    @TextIndexed  //  Text index for full-text search
    private String bio;

    private Date expiryDate; //  Will use for TTL index

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)  //  Geo index
    private Point location;

    // Constructors, getters, setters
}
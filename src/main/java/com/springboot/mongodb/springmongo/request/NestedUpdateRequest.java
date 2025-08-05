package com.springboot.mongodb.springmongo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NestedUpdateRequest {
    private int companyIndex;
    private int addressIndex;
    private String address1;
}

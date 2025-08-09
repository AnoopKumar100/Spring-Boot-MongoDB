package com.springboot.mongodb.springmongo.controller;

import com.springboot.mongodb.springmongo.service.IndexDemoService;
import com.springboot.mongodb.springmongo.service.IndexOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/index")
public class IndexController {

    @Autowired
    IndexDemoService indexService;

    @Autowired
    IndexOpsService indexOpsService;

    //http://localhost:8081/api/index/create
    // use the index.json as the request body from resources for saving
    //save API not here. either create or directly import this
    @PostMapping("/create")
    public String createIndexes() {
        indexService.createAllIndexes();
        return "Indexes created successfully!";
    }

    //http://localhost:8081/api/index/list
    @GetMapping("/list")
    public String listIndexes() {
        indexService.listIndexes();
        return "Indexes printed in console.";
    }

    //http://localhost:8081/api/index/createops
    @PostMapping("/createops")
    public String createops() {
        indexOpsService.createAllIndexes();
        return "Indexes created successfully!";
    }

    //http://localhost:8081/api/index/listops
    @GetMapping("/listops")
    public List<IndexInfo> listops() {
       return indexOpsService.listIndexes();

    }

}
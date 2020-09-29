package com.sam.orbit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrbitController {

    @GetMapping("/hello")
    public String hello(String name) {
        return "hello " + name;
    }

}

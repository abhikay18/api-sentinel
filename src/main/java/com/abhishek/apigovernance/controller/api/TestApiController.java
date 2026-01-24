package com.abhishek.apigovernance.controller.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestApiController {

    @GetMapping
    public String test() {
        return "API access granted";
    }
}

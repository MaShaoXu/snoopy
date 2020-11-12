package com.sam.oauth2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/current")
    public Principal getUser(Principal principal) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(principal.toString());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
        return principal;
    }

}


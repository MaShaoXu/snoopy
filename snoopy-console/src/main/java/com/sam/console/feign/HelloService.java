package com.sam.console.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("orbit")
public interface HelloService {
    @GetMapping("/hello")
    String hello(@RequestParam(name = "name") String name);
}

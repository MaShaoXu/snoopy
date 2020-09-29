package com.sam.console.controller;

import com.sam.console.feign.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HelloController {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HelloService helloService;

    @GetMapping("/test1")
    public String test1() {
        ServiceInstance serviceInstance = loadBalancerClient.choose("orbit");
        String url = serviceInstance.getUri() + "/hello?name=" + "didi1";
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        return "Invoke : " + url + ", return : " + result;
    }

    @GetMapping("/test2")
    public String test2() {
        String result = restTemplate.getForObject("http://orbit/hello?name=didi2", String.class);
        return "Return : " + result;
    }

    @GetMapping("/test3")
    public String test3() {
        String result = helloService.hello("didi3");
        return "Return : " + result;
    }

}

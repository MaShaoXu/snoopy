package com.sam.console.controller;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/nacos/discovery")
public class NacosDiscoveryController {

    @Autowired
    private NacosDiscoveryProperties properties;
    private NamingService namingService;
    private NamingMaintainService namingMaintainService;

    @PostConstruct
    public void init() {
        namingService = properties.namingServiceInstance();
        namingMaintainService = properties.namingMaintainServiceInstance();
    }

    @GetMapping("/serverStatus")
    public String serverStatus() {
        return namingService.getServerStatus();
    }

}

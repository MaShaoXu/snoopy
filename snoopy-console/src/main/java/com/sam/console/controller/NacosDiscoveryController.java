package com.sam.console.controller;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

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

    @GetMapping("/testMaintain")
    public String queryService(String serviceName){
        try {
            Service service = namingMaintainService.queryService(serviceName);

            return "OK";
        } catch (NacosException e) {
            e.printStackTrace();
            return "ERR";
        }
    }

    @GetMapping("/getAllInstances")
    public List<Instance> getAllInstances(String serviceName){
        try {
            return namingService.getAllInstances(serviceName);
        } catch (NacosException e) {
            e.printStackTrace();
            return null;
        }
    }

}

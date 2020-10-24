package com.sam.console.controller;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.sam.console.model.Instance;
import com.sam.console.model.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("/getServicesOfServer")
    public ListView<String> getServicesOfServer(Integer pageNo, Integer pageSize){
        try {
            return namingService.getServicesOfServer(pageNo, pageSize);
        } catch (NacosException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/getService")
    public Service getService(String serviceName){
        try {
            com.alibaba.nacos.api.naming.pojo.Service service = namingMaintainService.queryService(serviceName);
            List<com.alibaba.nacos.api.naming.pojo.Instance> allInstances = namingService.getAllInstances(serviceName);
            Service cService = new Service();
            BeanUtils.copyProperties(service, cService);
            List<Instance> instances = allInstances.stream().map(instance -> {
                Instance cInstance = new Instance();
                BeanUtils.copyProperties(instance, cInstance);
                return cInstance;
            }).collect(Collectors.toList());
            cService.setInstances(instances);
            return cService;
        } catch (NacosException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/updateService")
    public Boolean updateService(Service cService){
        try {
            String groupName = cService.getGroupName();
            String name = cService.getName();
            float protectThreshold = cService.getProtectThreshold();
            Map<String, String> metadata = cService.getMetadata();

            List<Instance> instances = cService.getInstances();
            for(Instance cInstance: instances){
                com.alibaba.nacos.api.naming.pojo.Instance instance = new com.alibaba.nacos.api.naming.pojo.Instance();
                BeanUtils.copyProperties(cInstance, instance);
                namingMaintainService.updateInstance(name, groupName, instance);
            }
            namingMaintainService.updateService(name, groupName, protectThreshold, metadata);
            return true;
        } catch (NacosException e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/registerInstance")
    public Boolean registerInstance(Service cService){
        try {
            String groupName = cService.getGroupName();
            String name = cService.getName();
            float protectThreshold = cService.getProtectThreshold();
            List<Instance> instances = cService.getInstances();
            for(Instance cInstance: instances){
                com.alibaba.nacos.api.naming.pojo.Instance instance = new com.alibaba.nacos.api.naming.pojo.Instance();
                BeanUtils.copyProperties(cInstance, instance);
                namingService.registerInstance(name, groupName, instance);
            }
            namingMaintainService.createService(name, groupName, protectThreshold);
            return true;
        } catch (NacosException e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/deregisterInstance")
    public Boolean deregisterInstance(Service cService){
        try {
            String groupName = cService.getGroupName();
            String name = cService.getName();
            List<Instance> instances = cService.getInstances();
            for(Instance cInstance: instances){
                com.alibaba.nacos.api.naming.pojo.Instance instance = new com.alibaba.nacos.api.naming.pojo.Instance();
                BeanUtils.copyProperties(cInstance, instance);
                namingService.deregisterInstance(name, groupName, instance);
            }
            return true;
        } catch (NacosException e) {
            e.printStackTrace();
            return false;
        }
    }

}

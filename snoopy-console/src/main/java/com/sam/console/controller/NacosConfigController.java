package com.sam.console.controller;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.sam.console.model.NacosConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/nacos/config")
public class NacosConfigController {

    @Autowired
    private NacosConfigProperties properties;
    private ConfigService configService;

    @PostConstruct
    public void init() {
        configService = properties.configServiceInstance();
    }

    @PostMapping("/publishConfig")
    public Boolean publishConfig(@RequestBody NacosConfig config) {
        String dataId = config.getDataId();
        String group = config.getGroup();
        String content = config.getContent();
        try {
            return configService.publishConfig(dataId, group, content);
        } catch (NacosException e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/getConfig")
    public String getConfig(String dataId, String group) {
        try {
            int timeout = properties.getTimeout();
            return configService.getConfig(dataId, group, timeout);
        } catch (NacosException e) {
            e.printStackTrace();
            return "";
        }
    }

    @GetMapping("/removeConfig")
    public Boolean removeConfig(String dataId, String group) {
        try {
            return configService.removeConfig(dataId, group);
        } catch (NacosException e) {
            e.printStackTrace();
            return false;
        }
    }

}

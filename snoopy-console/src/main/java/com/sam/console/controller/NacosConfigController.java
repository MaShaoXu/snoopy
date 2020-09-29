package com.sam.console.controller;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.sam.console.model.NacosConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/nacos/config")
public class NacosConfigController {

    @Autowired
    private NacosConfigProperties properties;
    private ConfigService configService;

    @Value("${spring.profiles.active:}")
    private String active;

    @PostConstruct
    public void init() {
        try {
            String prefix = properties.getPrefix();
            String fileExtension = properties.getFileExtension();
            String dataId = prefix + "." + fileExtension;
            if (StringUtils.isNotEmpty(active)) {
                dataId = prefix + "-" + active + "." + fileExtension;
            }
            String group = properties.getGroup();
            int timeout = properties.getTimeout();
            configService = properties.configServiceInstance();
            configService.addListener(dataId, group, new AbstractListener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    System.out.println(configInfo);
                }
            });
            configService.removeListener(dataId, group, new AbstractListener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    System.err.println(configInfo);
                }
            });
            configService.getConfigAndSignListener(dataId, group, timeout, new AbstractListener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    System.out.println(configInfo);
                }
            });
        } catch (NacosException e) {
            e.printStackTrace();
        }
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

package com.sam.console.controller;

import com.sam.console.model.NacosConfig;
import org.springframework.web.bind.annotation.*;

@RestController("/nacos")
public class NacosController {

//    @NacosInjected
//    private ConfigService configService;

    @PostMapping("/publishConfig")
    public Boolean publishConfig(@RequestBody NacosConfig config){
        String dataId = config.getDataId();
        String group = config.getGroup();
        String content = config.getContent();
//        try {
//            return configService.publishConfig(dataId, group, content);
//        } catch (NacosException e) {
//            e.printStackTrace();
            return false;
//        }
    }

    @GetMapping("/getConfig")
    public String getConfig(@RequestParam String dataId, @RequestParam String group){
//        try {
//            return configService.getConfig(dataId, group, 3000);
//        } catch (NacosException e) {
//            e.printStackTrace();
            return "";
//        }
    }

    @GetMapping("/removeConfig")
    public Boolean removeConfig(String dataId, String group){
//        try {
//            return configService.removeConfig(dataId, group);
//        } catch (NacosException e) {
//            e.printStackTrace();
            return false;
//        }
    }

}

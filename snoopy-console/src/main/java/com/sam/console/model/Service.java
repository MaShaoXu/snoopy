package com.sam.console.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class Service {
    private String groupName;
    private String name;

    private String appName;
    /**
     * 阈值 = 健康实例/实例总数，当设置阈值小于计算阈值，服务被保护
     */
    private float protectThreshold = 0.0F;

    private Map<String, String> metadata = new HashMap<>();
    private List<Instance> instances = new ArrayList<>();
}

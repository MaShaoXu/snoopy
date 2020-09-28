package com.sam.console.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NacosConfig {
    private String dataId;
    private String group;
    private String content;
}

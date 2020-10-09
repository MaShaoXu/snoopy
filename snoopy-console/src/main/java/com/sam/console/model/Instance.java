package com.sam.console.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Instance {
    private String instanceId;
    private String ip;
    private int port;
    private String clusterName;
    /**
     * 权重
     */
    private double weight = 1.0D;
    private boolean healthy = true;
    private boolean enabled = true;
    /**
     * 短暂的?
     */
    private boolean ephemeral = true;
}

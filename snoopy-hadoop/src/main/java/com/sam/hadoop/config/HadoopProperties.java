package com.sam.hadoop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix="spring.hadoop")
public class HadoopProperties {
    private String zkQuorum;
    private String zkClientPort;
    private String hdfsPath;
}

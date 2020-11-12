package com.sam.oauth2.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix="security.oauth2")
public class SecurityProperties {
    private String jwtSigningKey;
}

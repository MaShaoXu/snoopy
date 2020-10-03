package com.sam.orbit;

import com.sam.hadoop.annotation.EnableHbaseTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableHbaseTable
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.sam")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

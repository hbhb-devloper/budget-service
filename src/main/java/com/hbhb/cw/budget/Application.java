package com.hbhb.cw.budget;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author dxk
 */
@RefreshScope
@EnableScheduling
@EnableFeignClients
@SpringCloudApplication
@ComponentScan("com.hbhb")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
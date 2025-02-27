package com.spoony.spoony_server.global.config;

import com.spoony.spoony_server.SpoonyServerApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = SpoonyServerApplication.class)
public class FeignClientConfig {
}

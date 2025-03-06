package com.spoony.spoony_server.adapter.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deploy")
public class BlueGreenController {

    @Value("${server.port}")
    private int serverPort;

    @GetMapping("/status")
    public String getDeploymentStatus() {
        String status = (serverPort == 8081) ? "blue" : (serverPort == 8082) ? "green" : "unknown";
        return "Current deployment: " + status;
    }
}

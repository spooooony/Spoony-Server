package com.spoony.spoony_server.adapter.test;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deploy")
public class BlueGreenController {

    private final Environment environment;

    @GetMapping("/status")
    public String getDeploymentStatus() {
        String port = environment.getProperty("local.server.port", "8080");
        String status = port.equals("8081") ? "blue" : port.equals("8082") ? "green" : "unknown";
        return "Current Port: " + status;
    }
}

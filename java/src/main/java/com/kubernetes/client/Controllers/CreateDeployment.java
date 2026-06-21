package com.kubernetes.client.Controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateDeployment {
    @PostMapping("/deployment/create")
    public String createDeployment() {
        // Logic to create a deployment
        return "Deployment created successfully";
    }
}

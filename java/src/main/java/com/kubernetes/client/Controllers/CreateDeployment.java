package com.kubernetes.client.Controllers;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kubernetes.client.Models.V1Spec;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.util.Config;

@RestController
public class CreateDeployment {
    @PostMapping("/deployment/create")
    public ResponseEntity<Object> createDeployment(@RequestParam(defaultValue = "default") String namespace,
            @RequestBody V1Spec spec) throws IOException {
        // Logic to create a deployment
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();

        try {
            // TODO - replace the below with properties from the RequestBody spec
            // Define the Pod metadata
            V1ObjectMeta metadata = new V1ObjectMeta()
                    .name(spec.getTemplate())
                    .labels(Collections.singletonMap("app", "java-client-demo"));

            // Define the container
            V1Container container = new V1Container()
                    .name("nginx-container")
                    .image("nginx:1.25")
                    .ports(Collections.singletonList(
                            new V1ContainerPort().containerPort(80)));

            // Define the Pod spec
            V1PodSpec podSpec = new V1PodSpec()
                    .containers(Collections.singletonList(container));

            // Create the Pod object
            V1Pod pod = new V1Pod()
                    .apiVersion("v1")
                    .kind("Pod")
                    .metadata(metadata)
                    .spec(podSpec);

            // Create the deployment using the provided spec
            api.createNamespacedPod(
                    namespace,
                    pod);
            System.out.println("Deployment created successfully in namespace: " + namespace);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating deployment: " + e.getMessage());
        }

        return ResponseEntity.created(null).build();
    }
}

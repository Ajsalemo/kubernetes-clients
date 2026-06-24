package com.kubernetes.client.Controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.util.Config;

@RestController
public class CreateDeployment {
        @PostMapping("/deployment/create")
        public ResponseEntity<Object> createDeployment(@RequestParam(defaultValue = "default") String namespace,
                        @RequestBody com.kubernetes.client.Models.V1Pod v1Pod) throws IOException {
                // Logic to create a deployment
                ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                AppsV1Api api = new AppsV1Api();

                try {
                        V1ObjectMeta metadata = new V1ObjectMeta()
                                        .name(v1Pod.getMetadata().getName())
                                        .labels(Collections.singletonMap("app",
                                                        v1Pod.getMetadata().getName()));

                        // Define the container
                        V1Container[] container = new V1Container[v1Pod.getSpec().getTemplate().getSpec()
                                        .getContainers().size()];
                        for (int i = 0; i < v1Pod.getSpec().getTemplate().getSpec().getContainers().size(); i++) {
                                com.kubernetes.client.Models.Container c = v1Pod.getSpec().getTemplate().getSpec()
                                                .getContainers().get(i);
                                container[i] = new V1Container()
                                                .name(c.getName())
                                                .image(c.getImage())
                                                .ports(Collections.singletonList(
                                                                new io.kubernetes.client.openapi.models.V1ContainerPort()
                                                                                .containerPort(80)));
                        }

                        // Define the Pod spec
                        V1PodSpec podSpec = new V1PodSpec()
                                        .containers(Arrays.asList(container));

                        // Create the Deployment object
                        V1Deployment deployment = new V1Deployment()
                                        .apiVersion("apps/v1")
                                        .kind("Deployment")
                                        .metadata(metadata)
                                        .spec(new V1DeploymentSpec().template(new V1PodTemplateSpec().spec(podSpec)));

                        // Create the deployment using the provided spec
                        api.createNamespacedDeployment(
                                        namespace,
                                        deployment);
                        System.out.println("Deployment created successfully in namespace: " + namespace);
                } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(500).body("Error creating deployment: " + e.getMessage());
                }

                return ResponseEntity.created(null).build();
        }
}

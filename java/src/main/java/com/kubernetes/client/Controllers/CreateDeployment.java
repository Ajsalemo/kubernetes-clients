package com.kubernetes.client.Controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.util.Config;

@RestController
public class CreateDeployment {
        @PostMapping("/deployment/create")
        public ResponseEntity<Object> createDeployment(@RequestParam(defaultValue = "default") String namespace,
                        @RequestBody com.kubernetes.client.Models.V1Pod v1Pod) throws IOException {
                // Create a logger instance
                Logger logger = LoggerFactory.getLogger(CreateDeployment.class);
                // Logic to create a deployment
                ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                AppsV1Api api = new AppsV1Api();

                try {
                        // Labels for selector and template
                        Map<String, String> labels = new HashMap<>();
                        labels.put("app", v1Pod.getSpec().getTemplate().getMetadata().getLabels()
                                        .getApp());
                        V1ObjectMeta metadata = new V1ObjectMeta()
                                        .name(v1Pod.getMetadata().getName());

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
                                                                                .containerPort(c.getPorts()
                                                                                                .getContainerPort())));
                        }

                        // Define the Pod spec
                        V1PodSpec podSpec = new V1PodSpec()
                                        .containers(Arrays.asList(container));

                        // Create the Deployment object
                        V1Deployment deployment = new V1Deployment()
                                        .apiVersion("apps/v1")
                                        .kind("Deployment")
                                        .metadata(metadata)
                                        .spec(new V1DeploymentSpec()
                                                        .replicas(v1Pod.getSpec().getReplicas())
                                                        .selector(new V1LabelSelector().matchLabels(labels))
                                                        .template(new V1PodTemplateSpec()
                                                                        .metadata(new V1ObjectMeta().labels(labels))
                                                                        .spec(podSpec)));

                        // Create the deployment using the provided spec
                        api.createNamespacedDeployment(
                                        namespace,
                                        deployment)
                                        .execute();
                        logger.info("Deployment created successfully in namespace: {}", namespace);
                } catch (Exception e) {
                        logger.error("Error creating deployment: {}", e.getMessage(), e);
                        return ResponseEntity.status(500).body("Error creating deployment: " + e.getMessage());
                }

                return ResponseEntity.created(null).build();
        }
}

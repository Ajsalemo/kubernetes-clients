package com.kubernetes.client.Controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.util.Config;

@RestController
public class GetPod {
    @GetMapping(value = "/pod/get", produces = "application/json")
    public ResponseEntity<String> getPod(@RequestParam(required = true) String podName,
            @RequestParam(defaultValue = "default") String namespace) throws IOException, ApiException {

        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();
        V1PodSpec pod = api.readNamespacedPod(podName, namespace).execute().getSpec();

        return ResponseEntity.ok(pod.toJson());
    }
}

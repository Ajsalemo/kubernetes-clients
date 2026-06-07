package com.kubernetes.client.Controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;

@RestController
public class DeletePod {
    @DeleteMapping("/pod/delete")
    public ResponseEntity<String> deletePod(@RequestParam(required = true) String podName,
            @RequestParam(defaultValue = "default") String namespace) throws IOException, ApiException {
        Logger logger = LoggerFactory.getLogger(DeletePod.class);

        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();
        api.deleteNamespacedPod(podName, namespace).execute();

        logger.info("Pod " + podName + " deleted successfully from namespace " + namespace);
        return ResponseEntity.ok("Pod " + podName + " deleted successfully from namespace " + namespace);
    }
}

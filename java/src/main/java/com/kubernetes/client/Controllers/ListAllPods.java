package com.kubernetes.client.Controllers;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api.APIlistNamespacedPodRequest;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;

@RestController
public class ListAllPods {
    @GetMapping("/pods/list/all")
    public String listAllPods() throws IOException, ApiException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        APIlistNamespacedPodRequest list = api.listNamespacedPod("kube-system");
        for (V1Pod item : list.execute().getItems()) {
            System.out.println(item.getMetadata().getName());
        }
        return "List of all pods in the cluster";
    }
}

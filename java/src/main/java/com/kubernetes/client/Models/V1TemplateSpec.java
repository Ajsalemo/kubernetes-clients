package com.kubernetes.client.Models;

public class V1TemplateSpec {
    private V1Container[] containers;
    private V1Metadata metadata;

    public V1TemplateSpec(V1Container[] containers, V1Metadata metadata) {
        this.containers = containers;
        this.metadata = metadata;
    }

    public V1Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(V1Metadata metadata) {
        this.metadata = metadata;
    }

    public V1Container[] getContainers() {
        return containers;
    }

    public void setContainers(V1Container[] containers) {
        this.containers = containers;
    }
}

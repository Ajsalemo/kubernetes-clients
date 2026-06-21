package com.kubernetes.client.Models;

public class V1Spec {
    private V1Selector selector;
    private V1TemplateSpec template;
    private int replicas;

    public V1Spec(V1Selector selector, V1TemplateSpec template, int replicas) {
        this.selector = selector;
        this.template = template;
        this.replicas = replicas;
    }

    public V1Selector getSelector() {
        return selector;
    }

    public void setSelector(V1Selector selector) {
        this.selector = selector;
    }

    public V1TemplateSpec getTemplate() {
        return template;
    }

    public void setTemplate(V1TemplateSpec template) {
        this.template = template;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }
}

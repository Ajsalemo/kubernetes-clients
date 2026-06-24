package com.kubernetes.client.Models;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Template {
    private Metadata__1 metadata;
    private Spec__1 spec;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public Metadata__1 getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata__1 metadata) {
        this.metadata = metadata;
    }

    public Spec__1 getSpec() {
        return spec;
    }

    public void setSpec(Spec__1 spec) {
        this.spec = spec;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

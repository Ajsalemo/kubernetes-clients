package com.kubernetes.client.Models;

public class V1Resources {
    private String requests;
    private String limits;

    public V1Resources(String requests, String limits) {
        this.requests = requests;
        this.limits = limits;
    }

    public String getRequests() {
        return requests;
    }

    public void setRequests(String requests) {
        this.requests = requests;
    }

    public String getLimits() {
        return limits;
    }

    public void setLimits(String limits) {
        this.limits = limits;
    }
}

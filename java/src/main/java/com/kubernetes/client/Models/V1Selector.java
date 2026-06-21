package com.kubernetes.client.Models;

public class V1Selector {
    private String matchLabels;

    public V1Selector(String matchLabels) {
        this.matchLabels = matchLabels;
    }

    public String getMatchLabels() {
        return matchLabels;
    }

    public void setMatchLabels(String matchLabels) {
        this.matchLabels = matchLabels;
    }
}

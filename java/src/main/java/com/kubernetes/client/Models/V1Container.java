package com.kubernetes.client.Models;

public class V1Container {
    private String name;
    private String image;
    private V1Resources resources;

    public V1Container(String name, String image, V1Resources resources) {
        this.name = name;
        this.image = image;
        this.resources = resources;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public V1Resources getResources() {
        return resources;
    }

    public void setResources(V1Resources resources) {
        this.resources = resources;
    }
}

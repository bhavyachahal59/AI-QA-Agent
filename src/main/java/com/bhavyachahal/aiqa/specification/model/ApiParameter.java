package com.bhavyachahal.aiqa.specification.model;

public class ApiParameter {

    private String name;
    private String location;
    private boolean required;
    private String type;

    public ApiParameter() {
    }

    public ApiParameter(
            String name,
            String location,
            boolean required,
            String type) {
        this.name = name;
        this.location = location;
        this.required = required;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
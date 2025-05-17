package com.tablefour.sidequest.entities.enums;

public enum JobStatus {
    OPEN("Open"),
    FILLED("Filled"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String value;

    JobStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
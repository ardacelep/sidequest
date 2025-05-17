package com.tablefour.sidequest.entities.enums;

public enum Gender {

    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other"),;

    private String value;

    Gender(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }

}

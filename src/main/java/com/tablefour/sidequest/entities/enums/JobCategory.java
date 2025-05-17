package com.tablefour.sidequest.entities.enums;

public enum JobCategory {
    DRIVER_AND_DELIVERY("Şoförlük & Teslimat"),
    OFFICE_WORK("Ofis işleri"),
    EVENT_STAFF("Etkinlik Görevlisi"),
    SERVICE_STAFF("Servis Personeli");

    private final String value;

    JobCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
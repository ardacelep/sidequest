package com.tablefour.sidequest.entities.enums;

public enum JobCategory {
    WAITER("Garsonluk"),
    DELIVERY("Dağıtım"),
    CLEANING("Temizlik"),
    SECURITY("Güvenlik"),
    SALES("Satış"),
    CUSTOMER_SERVICE("Müşteri Hizmetleri"),
    OFFICE_WORK("Ofis İşleri"),
    EDUCATION("Eğitim"),
    TECHNICAL("Teknik"),
    OTHER("Diğer");

    private final String value;

    JobCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
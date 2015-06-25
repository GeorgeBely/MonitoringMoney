package ru.MonitoringMoney.types;


public enum ImportanceTypeDefault {
    EMPTY(""),
    VERY_IMPORTANT("Необходимо"),
    IMPORTANT("Нужно"),
    MEDIUM("Полезное"),
    LOW("Хочется"),
    VERY_LOW("Бесполезное");

    private String name;

    ImportanceTypeDefault(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
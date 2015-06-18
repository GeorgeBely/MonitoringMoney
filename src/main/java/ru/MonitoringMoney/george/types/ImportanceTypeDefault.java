package main.java.ru.MonitoringMoney.george.types;


public enum ImportanceTypeDefault {
    veryImportant("Необходимо"),
    important("Нужно"),
    medium("Полезное"),
    low("Хочется"),
    veryLow("Бесполезное");

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


    public String toString() {
        return name;
    }
}
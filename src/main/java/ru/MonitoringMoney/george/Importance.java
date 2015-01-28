package main.java.ru.MonitoringMoney.george;


public enum Importance {
    veryImportant("Необходимо"),
    important("Нужно"),
    medium("Полезное"),
    low("Хочется"),
    veryLow("Бесполезное");

    private String name;

    Importance(String name) {
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
package main.java.ru.MonitoringMoney.george;


public enum User {
    GEORGE("Жорик"),
    DASHA("Даша");

    private String name;

    User(String name) {
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
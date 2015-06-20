package main.java.ru.MonitoringMoney.george.types;


public enum UsersDefault {
    EMPTY(""),
    GEORGE("Жорик"),
    DASHA("Даша");

    private String name;

    UsersDefault(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
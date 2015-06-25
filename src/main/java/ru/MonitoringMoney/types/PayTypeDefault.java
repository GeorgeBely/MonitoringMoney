package ru.MonitoringMoney.types;


public enum PayTypeDefault {
    EMPTY(""),
    BUZ("Транспорт"),
    TAXI("Такси"),
    CINEMA("Кино"),
    THEATER("Театр"),
    CAFE("Кафе"),
    DINING_ROOM("Столовая"),
    FAST_FOOD("Фаст вуд"),
    MEDICINE("Лекарства"),
    ICE_WATER("Холодные напитки"),
    CHOCOLATE("Шоколад"),
    COFFEE("Кофе"),
    DELICACY("Вкусности"),
    FRENCH_GIFTS("Французкие подарочки"),
    DUTY("Пошлина");


    private String name;

    PayTypeDefault(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

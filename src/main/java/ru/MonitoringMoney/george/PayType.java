package main.java.ru.MonitoringMoney.george;


public enum PayType {
    buz("Транспорт"),
    taxi("Такси"),
    cinema("Кино"),
    theater("Театр"),
    cafe("Кафе"),
    diningRoom("Столовая"),
    iceWater("Холодные напитки"),
    chocolate("Шоколад"),
    coffee("Кофе"),
    frenchGifts("Французкие подарочки");


    private String name;

    PayType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}

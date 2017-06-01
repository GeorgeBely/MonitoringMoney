package ru.MonitoringMoney.types;


public class Bill {

    private Integer amountMoney;


    public Integer getAmountMoney() {
        return amountMoney;
    }

    public void addMoney(Integer count) {
        amountMoney += count;
    }
}

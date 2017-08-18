package ru.MonitoringMoney.income;


import java.io.Serializable;
import java.util.Date;

public class Income implements Serializable {

    private static final long serialVersionUID = -915846168985393719L;


    private Date date;
    private Integer amountMoney;


    public Income(Date date, Integer amountMoney) {
        this.date = date;
        this.amountMoney = amountMoney;
    }

    public Date getDate() {
        return date;
    }

    public Integer getAmountMoney() {
        return amountMoney;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAmountMoney(Integer amountMoney) {
        this.amountMoney = amountMoney;
    }
}

package ru.MonitoringMoney.types;

import java.io.Serializable;
import java.util.Date;


/**
 * Объект "доход"
 */
public class Income implements Serializable {

    private static final long serialVersionUID = -915846168985393719L;


    private Date date;
    private Integer amountMoney;
    private Users user;
    private IncomeType type;
    private String description;


    public Income(Date date, Integer amountMoney, Users user, IncomeType type, String description) {
        this.date = date;
        this.amountMoney = amountMoney;
        this.user = user;
        this.type = type;
        this.description = description;
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

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public IncomeType getType() {
        return type;
    }

    public void setType(IncomeType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

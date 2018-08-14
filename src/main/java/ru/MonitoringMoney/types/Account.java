package ru.MonitoringMoney.types;


import java.io.Serializable;
import java.util.List;


/**
 * Created by  George Beliy at 09-08-2018
 */
public class Account implements Comparable,Serializable {

    private static final long serialVersionUID = 2746590529235680129L;


    private Integer limit;
    private List<PayType> types;
    private String name;
    private boolean replenishable;
    private int calendarPeriod;


    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<PayType> getTypes() {
        return types;
    }

    public void setTypes(List<PayType> types) {
        this.types = types;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReplenishable() {
        return replenishable;
    }

    public void setReplenishable(boolean replenishable) {
        this.replenishable = replenishable;
    }

    public int getCalendarPeriod() {
        return calendarPeriod;
    }

    public void setCalendarPeriod(int calendarPeriod) {
        this.calendarPeriod = calendarPeriod;
    }

    @Override
    public int compareTo(Object o) {
        return name.compareTo(((Account) o).getName());
    }
}

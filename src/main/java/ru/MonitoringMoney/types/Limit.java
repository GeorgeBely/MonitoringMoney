package ru.MonitoringMoney.types;


import java.util.List;

public class Limit {

    private String name;
    private List<PayType> payTypes;
    private Integer limit;
    private Bill exportBill;
    private TimeRange timeRange;


    public Limit(String name, List<PayType> payTypes, Integer limit, Bill exportBill, TimeRange timeRange) {
        this.name = name;
        this.payTypes = payTypes;
        this.limit = limit;
        this.exportBill = exportBill;
        this.timeRange = timeRange;
    }


    public String getName() {
        return name;
    }

    public List<PayType> getPayTypes() {
        return payTypes;
    }

    public Integer getLimit() {
        return limit;
    }

    public Bill getExportBill() {
        return exportBill;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }
}

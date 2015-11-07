package ru.MonitoringMoney.types;

import java.io.Serializable;


/**
 * Объект желаемой покупки
 */
public class DesiredPurchase implements TypeValue, Serializable {

    private static final long serialVersionUID = 7416866525278684935L;


    private String code;
    private String name;

    public DesiredPurchase(String value, String code) {
        this.name = value;
        this.code = code;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

}

package main.java.ru.MonitoringMoney.george.types;

import java.io.Serializable;

/**
 *
 */
public class PayType implements Serializable {

    private static final long serialVersionUID = -9031825584982262846L;


    private String code;
    private String name;

    public PayType() {
    }

    public PayType(PayTypeDefault typeDefault) {
        code = typeDefault.toString().toLowerCase();
        name = typeDefault.getName();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

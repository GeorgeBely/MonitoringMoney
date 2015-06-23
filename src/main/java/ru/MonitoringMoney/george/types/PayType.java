package main.java.ru.MonitoringMoney.george.types;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 */
public class PayType implements Serializable {

    private static final long serialVersionUID = -9031825584982262846L;


    private String code;
    private String name;

    public PayType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public PayType(PayTypeDefault typeDefault) {
        code = typeDefault.toString().toLowerCase();
        name = typeDefault.getName().toLowerCase();
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
        return StringUtils.capitalize(name);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        PayType that = (PayType) o;
        return this == o || (Objects.equals(that.getCode(), code) && Objects.equals(that.getName(), name));
    }
}

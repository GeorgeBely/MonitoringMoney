package main.java.ru.MonitoringMoney.george.types;

import java.io.Serializable;

/**
 *
 */
public class ImportanceType implements Serializable {

    private static final long serialVersionUID = 3734720365475548880L;


    private String code;
    private String name;

    public ImportanceType() {

    }

    public ImportanceType(ImportanceTypeDefault typeDefault) {
        code = typeDefault.toString();
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

package ru.MonitoringMoney.types;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 */
public class ImportanceType implements Serializable {

    private static final long serialVersionUID = 3734720365475548880L;


    private String code;
    private String name;

    public ImportanceType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public ImportanceType(ImportanceTypeDefault typeDefault) {
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

        ImportanceType that = (ImportanceType) o;
        return this == o || (Objects.equals(that.getCode(), code) && Objects.equals(that.getName(), name));
    }
}
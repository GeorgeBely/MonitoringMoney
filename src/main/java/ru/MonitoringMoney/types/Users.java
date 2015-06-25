package ru.MonitoringMoney.types;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 */
public class Users implements Serializable {

    private static final long serialVersionUID = -5378473412329676164L;


    private String code;
    private String name;


    public Users(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Users(UsersDefault typeDefault) {
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

        Users that = (Users) o;
        return this == o || (Objects.equals(that.getCode(), code) && Objects.equals(that.getName(), name));
    }
}

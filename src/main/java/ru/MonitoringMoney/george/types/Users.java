package main.java.ru.MonitoringMoney.george.types;

import java.io.Serializable;

/**
 *
 */
public class Users implements Serializable {

    private static final long serialVersionUID = -5378473412329676164L;


    private String code;
    private String name;

    public Users() {

    }

    public Users(UsersDefault typeDefault) {
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

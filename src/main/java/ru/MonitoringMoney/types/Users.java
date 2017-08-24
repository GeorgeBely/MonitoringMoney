package ru.MonitoringMoney.types;


/**
 * Объект "пользователь"
 */
public class Users extends TypeValue {

    private static final long serialVersionUID = -5378473412329676164L;

    public Users() {
        super();
    }

    public Users(String code, String name) {
        super(code, name);
    }

}

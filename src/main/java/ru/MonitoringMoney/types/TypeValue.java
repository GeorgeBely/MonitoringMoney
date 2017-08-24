package ru.MonitoringMoney.types;

import org.apache.commons.lang.StringUtils;
import ru.MonitoringMoney.services.ApplicationService;

import java.io.Serializable;
import java.util.Objects;


/**
 * Абстрактный класс для работы с любым свойством
 */
public abstract class TypeValue implements Serializable {

    private static final long serialVersionUID = -3869215401859374134L;

    public static final String EMPTY = "empty";

    private String name;
    private String code;

    protected TypeValue() {
        this(ApplicationService.getInstance().getNewUniqueCode(), "");
    }

    protected TypeValue(String code, String name) {
        this.name = name;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return StringUtils.capitalize(getName());
    }

    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + code.hashCode();
        return result;
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        TypeValue that = (TypeValue) o;
        return this == o || (Objects.equals(that.getCode(), code) && Objects.equals(that.getName(), name));
    }
}

package ru.MonitoringMoney.types;


/**
 * объект "тип покупки"
 */
public class PayType extends TypeValue {

    private static final long serialVersionUID = -9031825584982262846L;

    public PayType() {
        super();
    }

    public PayType(String code, String name) {
        super(code, name);
    }

}

package ru.MonitoringMoney.types;


/**
 * Объект желаемой покупки
 */
public class DesiredPurchase extends TypeValue {

    private static final long serialVersionUID = 7416866525278684935L;

    public DesiredPurchase() {
        super();
    }

    public DesiredPurchase(String code, String name) {
        super(code, name);
    }

}

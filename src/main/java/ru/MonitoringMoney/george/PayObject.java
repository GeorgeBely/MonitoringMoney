package main.java.ru.MonitoringMoney.george;

import java.io.Serializable;
import java.util.Date;


public class PayObject implements Serializable {
    Integer price;
    Date date;
    PayType type;
    Importance importance;
    String description;
    boolean purchased;
    User user;

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (date != null)
            builder.append(date.toString()).append(" ");
        if (importance != null)
            builder.append(importance.getName()).append(" ");
        if (type != null)
            builder.append(type.getName()).append(" ");
        if (price != null)
            builder.append("ценой в").append(price).append("руб ");
        if (user != null)
            builder.append(user.getName()).append(" ");
        if (description != null)
            builder.append(description);
        return builder.toString();
    }
}
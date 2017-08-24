package ru.MonitoringMoney.types;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import ru.MonitoringMoney.main.ApplicationProperties;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;


/**
 * Объект покупки
 */
public class PayObject implements Serializable {

    private static final long serialVersionUID = -2068955115954529140L;


    private Integer price;
    private Date date;
    private PayType payType;
    private ImportanceType importance;
    private String description;
    private Users user;


    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (user != null)
            builder.append(user.getName()).append(" ");
        if (price != null)
            builder.append(price).append("руб ");
        if (payType != null)
            builder.append(payType.getName()).append(" ");
        if (importance != null)
            builder.append(importance.getName()).append(" ");
        if (date != null)
            builder.append(ApplicationProperties.FORMAT_DATE.format(date)).append(" ");
        if (StringUtils.isNotBlank(description))
            builder.append("\n").append(description);
        return builder.toString();
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = DateUtils.addHours(DateUtils.truncate(date, Calendar.DATE), 12);
    }

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public ImportanceType getImportance() {
        return importance;
    }

    public void setImportance(ImportanceType importance) {
        this.importance = importance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
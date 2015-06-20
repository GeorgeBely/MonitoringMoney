package main.java.ru.MonitoringMoney.george;

import main.java.ru.MonitoringMoney.george.helpers.ApplicationHelper;
import main.java.ru.MonitoringMoney.george.types.*;

import java.io.Serializable;
import java.util.Date;


public class PayObject implements Serializable {

    private static final long serialVersionUID = -2068955115954529140L;


    private Integer price;
    private Date date;
    private PayType payType;
    private ImportanceType importance;
    private String description;
    private boolean purchased;
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
            builder.append(ApplicationHelper.FORMAT_DATE.format(date)).append(" ");
        if (description != null)
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
        this.date = date;
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

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
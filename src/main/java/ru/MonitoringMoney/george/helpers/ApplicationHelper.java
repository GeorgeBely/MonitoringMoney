package main.java.ru.MonitoringMoney.george.helpers;

import main.java.ru.MonitoringMoney.george.PayObject;
import main.java.ru.MonitoringMoney.george.types.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Хелпер для работы с приложением
 */
public class ApplicationHelper implements Serializable {

    private static final long serialVersionUID = -2808789219515984025L;

    /** Файл с данными о покупках */
    public static final File buyFile = new File("MoneyData.mm");

    /** Список покупок */
    public List<PayObject> payObjects = new ArrayList<>();

    /** Список уровней важности */
    public List<ImportanceType> importanceTypes = new ArrayList<>();

    /** Список товаров и услуг */
    public List<PayType> payTypes = new ArrayList<>();

    /** Список пользователей */
    public List<Users> users = new ArrayList<>();


    private static ApplicationHelper instance;

    public static ApplicationHelper getInstance() {
        if (instance == null) {
            instance = new ApplicationHelper();
        }
        if (instance.importanceTypes.isEmpty() && instance.payTypes.isEmpty() && instance.users.isEmpty()) {
            instance.initDefaultProperties();
        }
        return instance;
    }

    public void initDefaultProperties() {
        importanceTypes = new ArrayList<>();
        payTypes = new ArrayList<>();
        users = new ArrayList<>();
        payObjects = new ArrayList<>();

        for (ImportanceTypeDefault defaultType : ImportanceTypeDefault.values()) {
            importanceTypes.add(new ImportanceType(defaultType));
        }
        for (PayTypeDefault defaultType : PayTypeDefault.values()) {
            payTypes.add(new PayType(defaultType));
        }
        for (UsersDefault defaultType : UsersDefault.values()) {
            users.add(new Users(defaultType));
        }
    }

    public static void createNewData() throws IOException {
        if (buyFile.createNewFile()) {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(buyFile));
            os.writeObject(new ApplicationHelper());
        }
    }

    public static void readData() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(buyFile));
        instance = (ApplicationHelper) ois.readObject();
    }

    public static void writeData() throws IOException {
        ObjectOutputStream bin = new ObjectOutputStream(new FileOutputStream(ApplicationHelper.buyFile));
        bin.writeObject(getInstance());
    }
}

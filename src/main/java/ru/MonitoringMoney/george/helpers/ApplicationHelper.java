package main.java.ru.MonitoringMoney.george.helpers;

import main.java.ru.MonitoringMoney.george.PayObject;
import main.java.ru.MonitoringMoney.george.types.*;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Хелпер для работы с приложением
 */
public class ApplicationHelper implements Serializable {

    private static final long serialVersionUID = -2808789219515984025L;

    /** Файл с данными о покупках */
    public static final File buyFile = new File("MoneyData.mm");

    /** Формат даты для поля ввода даты */
    public static final DateFormat FORMAT_DATE = DateFormat.getDateInstance(DateFormat.SHORT);

    private static final String EMPTY = "empty";

    /** Список покупок */
    public List<PayObject> payObjects = new ArrayList<>();

    /** Список уровней важности */
    public List<ImportanceType> importanceTypes = new ArrayList<>();

    /** Список товаров и услуг */
    public List<PayType> payTypes = new ArrayList<>();

    /** Список пользователей */
    public List<Users> users = new ArrayList<>();

    public Integer uniqueId;


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

    public String getTextPayObjects(String term, Date dateFrom, Date dateTo, Integer priseFrom, Integer priseTo,
                                    ImportanceType importanceType, PayType payType, Users user, boolean purchased) {
        Optional<String> optional = getPayObjectsWithFilters(term, dateFrom, dateTo, priseFrom, priseTo, importanceType, payType, user, purchased)
                .stream()
                .map(PayObject::toString)
                .reduce((s1, s2) -> s1 + "\n\n" + s2);

        if (optional.isPresent())
            return optional.get();
        return "";
    }

    public Integer getSumPrice(String term, Date dateFrom, Date dateTo, Integer priseFrom, Integer priseTo,
                               ImportanceType importanceType, PayType payType, Users user, boolean purchased) {
        Optional<Integer> optional = getPayObjectsWithFilters(term, dateFrom, dateTo, priseFrom, priseTo, importanceType, payType, user, purchased)
                .stream()
                .map(PayObject::getPrice)
                .reduce((s1, s2) -> s1 + s2);

        if (optional.isPresent())
            return optional.get();
        return 0;
    }

    public List<PayObject> getPayObjectsWithFilters(String term, Date dateFrom, Date dateTo, Integer priseFrom, Integer priseTo,
                                                    ImportanceType importanceType, PayType payType, Users user, boolean purchased) {
        return payObjects.stream()
                .filter(obj -> StringUtils.isBlank(term) || obj.getDescription().contains(term))
                .filter(obj -> dateFrom == null || obj.getDate().equals(dateFrom) || obj.getDate().after(dateFrom))
                .filter(obj -> dateTo == null || obj.getDate().before(dateTo))
                .filter(obj -> priseFrom == null || obj.getPrice() >= priseFrom)
                .filter(obj -> priseTo == null || obj.getPrice() <= priseTo)
                .filter(obj -> importanceType == null || EMPTY.equals(importanceType.getCode()) || obj.getImportance().equals(importanceType))
                .filter(obj -> payType == null || EMPTY.equals(payType.getCode()) || obj.getPayType().equals(payType))
                .filter(obj -> user == null || EMPTY.equals(user.getCode()) || obj.getUser().equals(user))
                .filter(obj -> Objects.equals(obj.isPurchased(), purchased))
                .collect(Collectors.toList());
    }

    public String getNewUniqueCode() {
        if (uniqueId == null)
            uniqueId = 0;
        uniqueId++;
        return "auto" + uniqueId;
    }

    public void addPropertyValue(String name, Class className) {
        if (PayType.class.equals(className)) {
            payTypes.add(new PayType(getNewUniqueCode(), name));
        } else if (ImportanceType.class.equals(className)) {
            importanceTypes.add(new ImportanceType(getNewUniqueCode(), name));
        } else if (Users.class.equals(className)) {
            users.add(new Users(getNewUniqueCode(), name));
        }

        try {
            writeData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

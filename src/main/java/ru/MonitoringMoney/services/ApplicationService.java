package ru.MonitoringMoney.services;

import ru.MonitoringMoney.ImageCanvas;
import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.types.*;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.text.DateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Хелпер для работы с приложением
 */
public class ApplicationService implements Serializable {

    private static final long serialVersionUID = -2808789219515984025L;

    /** Файл с данными о покупках */
    public static final File buyFile = new File("MoneyData.mm");

    /** Формат даты для поля ввода даты */
    public static final DateFormat FORMAT_DATE = DateFormat.getDateInstance(DateFormat.SHORT);

    /** Код пустого поля свойства покупки */
    public static final String EMPTY = "empty";

    /** Список покупок */
    public List<PayObject> payObjects = new ArrayList<>();

    /** Список уровней важности */
    public List<ImportanceType> importanceTypes = new ArrayList<>();

    /** Список товаров и услуг */
    public List<PayType> payTypes = new ArrayList<>();

    /** Список пользователей */
    public List<Users> users = new ArrayList<>();

    /** Иконки и картинки в приложение */
    public Map<String, ImageCanvas> images = new HashMap<>();

    public Integer uniqueId;


    private static ApplicationService instance;

    public static ApplicationService getInstance() {
        if (instance == null) {
            instance = new ApplicationService();
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
    }

    public static void createNewData() throws IOException {
        if (buyFile.createNewFile()) {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(buyFile));
            os.writeObject(new ApplicationService());
        }
    }

    public static void readData() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(buyFile));
        instance = (ApplicationService) ois.readObject();
        if (instance.images == null)
            instance.images = new HashMap<>();
    }

    public static void writeData() throws IOException {
        ObjectOutputStream bin = new ObjectOutputStream(new FileOutputStream(ApplicationService.buyFile));
        bin.writeObject(getInstance());
    }

    public String getTextPayObjects(List<PayObject> payObjects) {
        Optional<String> optional = payObjects
                .stream()
                .map(PayObject::toString)
                .reduce((s1, s2) -> s1 + "\n\n" + s2);

        if (optional.isPresent())
            return optional.get();
        return "";
    }

    public Integer getSumPrice(List<PayObject> payObjects) {
        Optional<Integer> optional = payObjects
                .stream()
                .map(PayObject::getPrice)
                .reduce((s1, s2) -> s1 + s2);

        if (optional.isPresent())
            return optional.get();
        return 0;
    }

    public List<PayObject> getPayObjectsWithFilters(String term, Date dateFrom, Date dateTo, Integer priseFrom, Integer priseTo,
                                                    ImportanceType importanceType, PayType payType, Users user) {
        return payObjects.stream()
                .filter(obj -> StringUtils.isBlank(term) || obj.toString().toLowerCase().contains(term.toLowerCase()))
                .filter(obj -> dateFrom == null || obj.getDate().equals(dateFrom) || obj.getDate().after(dateFrom))
                .filter(obj -> dateTo == null || obj.getDate().before(dateTo))
                .filter(obj -> priseFrom == null || obj.getPrice() >= priseFrom)
                .filter(obj -> priseTo == null || obj.getPrice() <= priseTo)
                .filter(obj -> importanceType == null || EMPTY.equals(importanceType.getCode()) || obj.getImportance().equals(importanceType))
                .filter(obj -> payType == null || EMPTY.equals(payType.getCode()) || obj.getPayType().equals(payType))
                .filter(obj -> user == null || EMPTY.equals(user.getCode()) || obj.getUser().equals(user))
                .collect(Collectors.toList());
    }

    public String getNewUniqueCode() {
        if (uniqueId == null)
            uniqueId = 0;
        uniqueId++;
        return "auto" + uniqueId;
    }

    public Object addPropertyValue(String name, Class className) {
        Object newValue = null;
        if (PayType.class.equals(className)) {
            newValue = new PayType(getNewUniqueCode(), name);
            payTypes.add((PayType) newValue);
        } else if (ImportanceType.class.equals(className)) {
            newValue = new ImportanceType(getNewUniqueCode(), name);
            importanceTypes.add((ImportanceType) newValue);
        } else if (Users.class.equals(className)) {
            newValue = new Users(getNewUniqueCode(), name);
            users.add((Users) newValue);
        }

        try {
            writeData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newValue;
    }
}

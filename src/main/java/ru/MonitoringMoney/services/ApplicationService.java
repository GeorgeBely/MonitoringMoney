package ru.MonitoringMoney.services;

import ru.MonitoringMoney.ImageCanvas;
import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.types.*;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    /** Формат даты отображающий только название месяца и год*/
    public static final DateFormat FORMAT_MONTH_AND_YEAR = new SimpleDateFormat("LLLL yyyy");

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

    /** Карта со значениями и колличеством использования уровней важности */
    public Map<ImportanceType, Integer> frequencyUseImportance = new HashMap<>();

    /** Карта со значениями и колличеством использования типов покупки */
    public Map<PayType, Integer> frequencyUsePayType = new HashMap<>();

    /** Карта со значениями и колличеством использования платильщика */
    public Map<Users, Integer> frequencyUseUser = new HashMap<>();

    /** Иконки и картинки в приложение */
    public Map<String, ImageCanvas> images = new HashMap<>();

    /** Уникальный для id нового типа объекта TypeValue. обавляется в поле code */
    public Integer uniqueId;


    /**
     * Экземпляр данного класса. Может быть только один на протяжение всего жизненного цикла приложения.
     * Синхронезируется. Данные для долговременного хранения должны быть в этом классе
     */
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

    /** Инициализирует стандартные настройки приложения. Используется при первом запуске приложения */
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

        users.add(new Users(EMPTY, ""));
    }

    /** Обновляет все данные о частоте использования типов */
    public void updateAllFrequencyUse() {
        frequencyUsePayType = new HashMap<>();
        frequencyUseUser = new HashMap<>();
        frequencyUseImportance = new HashMap<>();
        payObjects.forEach(this::updateFrequencyUse);
    }

    /** Добавляет данные новой покупки в частоту использования типов  */
    private void updateFrequencyUse(PayObject payObject) {
        if (frequencyUseImportance.containsKey(payObject.getImportance())) {
            frequencyUseImportance.put(payObject.getImportance(), frequencyUseImportance.get(payObject.getImportance()) + 1);
        } else {
            frequencyUseImportance.put(payObject.getImportance(), 1);
        }

        if (frequencyUseUser.containsKey(payObject.getUser())) {
            frequencyUseUser.put(payObject.getUser(), frequencyUseUser.get(payObject.getUser()) + 1);
        } else {
            frequencyUseUser.put(payObject.getUser(), 1);
        }

        if (frequencyUsePayType.containsKey(payObject.getPayType())) {
            frequencyUsePayType.put(payObject.getPayType(), frequencyUsePayType.get(payObject.getPayType()) + 1);
        } else {
            frequencyUsePayType.put(payObject.getPayType(), 1);
        }
    }

    /** Добавляет новую покупку. Обновляет данные о частоте использования типов. */
    public void addPayObject(PayObject payObject) {
        if (payObjects.size() != 0 && frequencyUsePayType.size() == 0)
            updateAllFrequencyUse();

        payObjects.add(payObject);
        updateFrequencyUse(payObject);

        ApplicationService.writeData();
        MonitoringMoney.mainFrame.refreshText();
    }

    /**
     * @return Отсортированный список уровней важности
     */
    public ImportanceType[] getSortedImportance() {
        List<ImportanceType> sortedList = new ArrayList<>();
        sortedList.addAll(importanceTypes);
        Collections.sort(sortedList, (o1, o2) -> {
            Integer o1Count = frequencyUseImportance.get(o1);
            Integer o2Count = frequencyUseImportance.get(o2);
            if (o1Count != null || o2Count != null) {
                if (o1Count == null || EMPTY.equals(o2.getCode()))
                    return 1;
                if (o2Count == null || EMPTY.equals(o1.getCode()))
                    return -1;
                return o2Count.compareTo(o1Count);
            }
            return o1.getName().compareTo(o2.getName());
        });

        ImportanceType[] items = new ImportanceType[sortedList.size()];
        return sortedList.toArray(items);
    }

    /**
     * @return Отсортированные список типов покупок
     */
    public PayType[] getSortedPayTypes() {
        List<PayType> sortedList = new ArrayList<>();
        sortedList.addAll(payTypes);
        Collections.sort(sortedList, (o1, o2) -> {
            Integer o1Count = frequencyUsePayType.get(o1);
            Integer o2Count = frequencyUsePayType.get(o2);
            if (o1Count != null || o2Count != null) {
                if (o1Count == null || EMPTY.equals(o2.getCode()))
                    return 1;
                if (o2Count == null || EMPTY.equals(o1.getCode()))
                    return -1;
                return o2Count.compareTo(o1Count);
            }
            return o1.getName().compareTo(o2.getName());
        });

        PayType[] items = new PayType[sortedList.size()];
        return sortedList.toArray(items);
    }

    /**
     * @return Отсортированный список платильщиков
     */
    public Users[] getSortedUsers() {
        List<Users> sortedList = new ArrayList<>();
        sortedList.addAll(users);
        Collections.sort(sortedList, (o1, o2) -> {
            Integer o1Count = frequencyUseUser.get(o1);
            Integer o2Count = frequencyUseUser.get(o2);
            if (o1Count != null || o2Count != null) {
                if (o1Count == null || EMPTY.equals(o2.getCode()))
                    return 1;
                if (o2Count == null || EMPTY.equals(o1.getCode()))
                    return -1;
                return o2Count.compareTo(o1Count);
            }
            return o1.getName().compareTo(o2.getName());
        });

        Users[] items = new Users[sortedList.size()];
        return sortedList.toArray(items);
    }

    /** Создаёт новый файл для хранения долговременной информации */
    public static void createNewData() throws IOException {
        if (buyFile.createNewFile()) {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(buyFile));
            os.writeObject(new ApplicationService());
        }
    }

    /** Прочитывает файл, который хранит долговременную информацию */
    public static void readData() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(buyFile));
        instance = (ApplicationService) ois.readObject();
        if (instance.images == null)
            instance.images = new HashMap<>();
    }

    /** Записыввает данные в файл долговременной информации */
    public static void writeData() {
        try {
            ObjectOutputStream bin = new ObjectOutputStream(new FileOutputStream(ApplicationService.buyFile));
            bin.writeObject(getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param payObjects покупки, текст о которых нужно составить
     * @return сгрупированный текст о всех переданных покупок
     */
    public String getTextPayObjects(List<PayObject> payObjects) {
        Optional<String> optional = payObjects
                .stream()
                .map(PayObject::toString)
                .reduce((s1, s2) -> s1 + "\n\n" + s2);

        if (optional.isPresent())
            return optional.get();
        return "";
    }

    /**
     * @param payObjects покупки, суммарную стоимость которых нужно посчитать
     * @return суммарное колличество стоимости покупок
     */
    public Integer getSumPrice(List<PayObject> payObjects) {
        Optional<Integer> optional = payObjects
                .stream()
                .map(PayObject::getPrice)
                .reduce((s1, s2) -> s1 + s2);

        if (optional.isPresent())
            return optional.get();
        return 0;
    }

    /**
     * Все параметры могут быть null
     *
     * @param term            подстрока, которая должна быть в описании покупки
     * @param dateFrom        фильтр по дате от
     * @param dateTo          фильтр по дате до
     * @param priseFrom       фильтр по цене от
     * @param priseTo         фильтр по цене до
     * @param importanceTypes уровень важности
     * @param payTypes        тип покупки
     * @param users           платильщик
     * @return список покупок по заданным фильтрам
     */
    public List<PayObject> getPayObjectsWithFilters(String term, Date dateFrom, Date dateTo, Integer priseFrom, Integer priseTo,
                                                    List<TypeValue> importanceTypes, List<TypeValue> payTypes, List<TypeValue> users) {
        return payObjects.stream()
                .filter(obj -> StringUtils.isBlank(term) || obj.toString().toLowerCase().contains(term.toLowerCase()))
                .filter(obj -> dateFrom == null || obj.getDate().equals(dateFrom) || obj.getDate().after(dateFrom))
                .filter(obj -> dateTo == null || obj.getDate().before(dateTo))
                .filter(obj -> priseFrom == null || obj.getPrice() >= priseFrom)
                .filter(obj -> priseTo == null || obj.getPrice() <= priseTo)
                .filter(obj -> checkValue(obj.getImportance(), importanceTypes))
                .filter(obj -> checkValue(obj.getPayType(), payTypes))
                .filter(obj -> checkValue(obj.getUser(), users))
                .collect(Collectors.toList());
    }

    /**
     * Проверяет наличие объекта в списке
     *
     * @param value  объект, который проверяется на вхождение
     * @param values список в котором проверяется наличие объекта
     * @return {true} - если объект <code>value</code> пуст или его код равен <code>EMPTY</code>
     *                  или если этот объект есть в списке
     */
    public boolean checkValue(TypeValue value, List<TypeValue> values) {
        return values == null
                || values.isEmpty()
                || (values.size() == 1 && EMPTY.equals((values.get(0)).getCode()))
                || values.contains(value);
    }

    /**
     * Генерирует новый уникальный код
     *
     * @return уникальный код
     */
    public String getNewUniqueCode() {
        if (uniqueId == null)
            uniqueId = 0;
        uniqueId++;
        return "auto" + uniqueId;
    }

    /**
     * Добавляет новый тип покупки или уровень важности или платильщика
     *
     * @param name      наименование нового типа
     * @param className объект класса, новый тип которого нужно создать
     * @return новый объект переданного класса <code>className</code>
     */
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

        writeData();
        return newValue;
    }

    /**
     * Возвращает покупки с учётом фильтров. Если с учётом фильтров покупок нет, возвращает все покупки.
     *
     * @return все покупки по выбранным фильтрам в основном окне <code>MainFrame</code>
     */
    public static List<PayObject> getPayObjects() {
        List<PayObject> payObjects = MonitoringMoney.mainFrame.getPayObjectWithCurrentFilters();
        if (payObjects.isEmpty()) {
            payObjects = ApplicationService.getInstance().getPayObjectsWithFilters(null, null, null, null, null, null, null,null);
        }
        return payObjects;
    }
}

package ru.MonitoringMoney.services;

import ru.MonitoringMoney.ApplicationProperties;
import ru.MonitoringMoney.ImageCanvas;
import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.frame.MainFrame;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.types.*;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Хелпер для работы с приложением
 */
public class ApplicationService implements Serializable {

    private static final long serialVersionUID = -2808789219515984025L;


    /** Список покупок */
    public List<PayObject> payObjects = new ArrayList<>();

    /** Список уровней важности */
    public List<ImportanceType> importanceTypes = new ArrayList<>();

    /** Список товаров и услуг */
    public List<PayType> payTypes = new ArrayList<>();

    /** Список пользователей */
    public List<Users> users = new ArrayList<>();

    /** Список желаемых покупок */
    public List<DesiredPurchase> desiredPurchases = new ArrayList<>();

    /** Карта со значениями и колличеством использования уровней важности */
    private Map<ImportanceType, Integer> frequencyUseImportance = new HashMap<>();

    /** Карта со значениями и колличеством использования типов покупки */
    private Map<PayType, Integer> frequencyUsePayType = new HashMap<>();

    /** Карта со значениями и колличеством использования платильщика */
    private Map<Users, Integer> frequencyUseUser = new HashMap<>();

    /** Иконки и картинки в приложение */
    Map<String, ImageCanvas> images = new HashMap<>();

    /** Уникальный для id нового типа объекта TypeValue. обавляется в поле code */
    private Integer uniqueId;

    /** Карта со значениями расположений окон. Ключ класс фрейма, значение координата */
    private Map<Class, Point> locationWindows = new HashMap<>();

    /** Карта со значениями размера окон. Ключ класс фрейма, значение размер окна */
    private Map<Class, Dimension> sizeWindows = new HashMap<>();

    /** Список покупок, которые отображются в данный момент */
    public static List<PayObject> viewPayObjects = new ArrayList<>();


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

    public List<ImportanceType> getImportanceTypes() {
        if (importanceTypes == null)
            importanceTypes = new ArrayList<>();
        return importanceTypes;
    }

    public List<PayType> getPayTypes() {
        if (payTypes == null)
            payTypes = new ArrayList<>();
        return payTypes;
    }

    public List<Users> getUsers() {
        if (users == null)
            users = new ArrayList<>();
        return users;
    }

    public List<DesiredPurchase> getDesiredPurchases() {
        if (desiredPurchases == null)
            desiredPurchases = new ArrayList<>();
        return desiredPurchases;
    }

    /** Инициализирует стандартные настройки приложения. Используется при первом запуске приложения */
    private void initDefaultProperties() {
        importanceTypes = new ArrayList<>();
        payTypes = new ArrayList<>();
        users = new ArrayList<>();
        payObjects = new ArrayList<>();

        importanceTypes.addAll(ApplicationProperties.DEFAULT_IMPORTANCE);
        payTypes.addAll(ApplicationProperties.DEFAULT_PAY_TYPES);
        users.add(new Users(ApplicationProperties.EMPTY, ""));
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
        List<ImportanceType> sortedList = sortTypes(importanceTypes, frequencyUseImportance);

        ImportanceType[] items = new ImportanceType[sortedList.size()];
        return sortedList.toArray(items);
    }

    /**
     * @return Отсортированные список типов покупок
     */
    public PayType[] getSortedPayTypes() {
        List<PayType> sortedList = sortTypes(payTypes, frequencyUsePayType);

        PayType[] items = new PayType[sortedList.size()];
        return sortedList.toArray(items);
    }

    /**
     * @return Отсортированный список платильщиков
     */
    public Users[] getSortedUsers() {
        List<Users> sortedList = sortTypes(users, frequencyUseUser);

        Users[] items = new Users[sortedList.size()];
        return sortedList.toArray(items);
    }

    private <T extends TypeValue> List<T> sortTypes(List<T> list, Map<T, Integer> map) {
        List<T> sortedList = new ArrayList<>();
        sortedList.addAll(list);

        sortedList.sort((o1, o2) -> {
            Integer o1Count = map.get(o1);
            Integer o2Count = map.get(o2);
            if (o1Count != null || o2Count != null) {
                if (o1Count == null || ApplicationProperties.EMPTY.equals(o1.getCode()))
                    return 1;
                if (o2Count == null || ApplicationProperties.EMPTY.equals(o2.getCode()))
                    return -1;
                return o2Count.compareTo(o1Count);
            }
            return o1.getName().compareTo(o2.getName());
        });

        return sortedList;
    }

    /** Создаёт новый файл для хранения долговременной информации */
    public static void createNewData() throws IOException {
        if (ApplicationProperties.BUY_FILE.createNewFile()) {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(ApplicationProperties.BUY_FILE));
            os.writeObject(new ApplicationService());
        }
    }

    /** Прочитывает файл, который хранит долговременную информацию */
    public static void readData() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ApplicationProperties.BUY_FILE));
        instance = (ApplicationService) ois.readObject();
        if (instance.images == null)
            instance.images = new HashMap<>();
    }

    /** Записыввает данные в файл долговременной информации */
    public static void writeData() {
        try {
            ObjectOutputStream bin = new ObjectOutputStream(new FileOutputStream(ApplicationProperties.BUY_FILE));
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

        return optional.orElse("");
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

        return optional.orElse(0);
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
    private boolean checkValue(TypeValue value, List<TypeValue> values) {
        return values == null
                || values.isEmpty()
                || (values.size() == 1 && ApplicationProperties.EMPTY.equals((values.get(0)).getCode()))
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
    static List<PayObject> getPayObjects() {
        List<PayObject> payObjects = MonitoringMoney.mainFrame.getPayObjectWithCurrentFilters();
        if (payObjects.isEmpty()) {
            payObjects = ApplicationService.getInstance().getPayObjectsWithFilters(null, null, null, null, null, null, null,null);
        }
        viewPayObjects = payObjects;
        return payObjects;
    }


    /**
     * @param className объект класса, позицию фрейма которого нужно получить
     * @return координату фрейма
     */
    public Point getWindowLocation(Class className) {
        if (locationWindows == null)
            locationWindows = new HashMap<>();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getWindowSize(className);
        if (!locationWindows.containsKey(className)) {
            Integer x = (int) (screenSize.width / 2 - frameSize.getWidth() / 2);
            Integer y = (int) (screenSize.height / 2 - frameSize.getHeight() / 2);
            locationWindows.put(className, new Point(x, y));
        }

        Point point = locationWindows.get(className);
        if (screenSize.getWidth() < point.getX() + frameSize.getWidth()
                || screenSize.getHeight() < point.getY() + frameSize.getHeight()) {
            Integer x = (int) (screenSize.width / 2 - frameSize.getWidth() / 2);
            Integer y = (int) (screenSize.height / 2 - frameSize.getHeight() / 2);
            point = new Point(x, y);
            locationWindows.put(className, point);
        }

        return point;
    }

    /**
     * @param className объект класса, размер фрейма которого нужно получить
     * @return размер фрейма
     */
    public Dimension getWindowSize(Class className) {
        if (sizeWindows == null)
            sizeWindows = new HashMap<>();

        if (!sizeWindows.containsKey(className)) {
            sizeWindows.put(className, ApplicationProperties.DEFAULT_FRAME_SIZE.get(className));
        }
        return sizeWindows.get(className);
    }

    /**
     * Сохраняет координату фоейма
     *
     * @param className объект класса, позицию фрейма которого нужно перезаписать
     * @param position  координата фрейма
     */
    public void updateLocationWindow(Class className, Point position) {
        locationWindows.put(className, position);

        //Запысываем положение основного фрейма, так как при его закрытии происходит остановка приложения
        locationWindows.put(MainFrame.class, MonitoringMoney.mainFrame.getLocation());

        writeData();
    }

    /**
     * Сохраняет размер фрейма
     *
     * @param className объект класса, размер фрейма которого нужно перезаписать
     * @param size      размер фрейма
     */
    public void updateSizeWindow(Class className, Dimension size) {
        sizeWindows.put(className, size);

        //Запысываем размер основного фрейма, так как при его закрытии происходит остановка приложения
        sizeWindows.put(MainFrame.class, MonitoringMoney.mainFrame.getSize());

        writeData();
    }
}

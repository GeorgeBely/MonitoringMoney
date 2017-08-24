package ru.MonitoringMoney.services;

import ru.MonitoringMoney.frame.AddIncomeFrame;
import ru.MonitoringMoney.frame.GraphicsFrame;
import ru.MonitoringMoney.main.ApplicationProperties;
import ru.MonitoringMoney.types.ImageCanvas;
import ru.MonitoringMoney.types.PayObject;
import ru.MonitoringMoney.frame.AddFrame;
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
    private List<PayObject> payObjects = new ArrayList<>();

    /** Список уровней важности */
    private List<ImportanceType> importanceTypes = new ArrayList<>();

    /** Список товаров и услуг */
    private List<PayType> payTypes = new ArrayList<>();

    /** Список пользователей */
    private List<Users> users = new ArrayList<>();

    /** Список типов дохода */
    private List<IncomeType> incomeTypes = new ArrayList<>();

    /** Список желаемых покупок */
    private List<DesiredPurchase> desiredPurchases = new ArrayList<>();

    /** Карта со значениями и колличеством использования уровней важности */
    private Map<ImportanceType, Integer> frequencyUseImportance = new HashMap<>();

    /** Карта со значениями и колличеством использования типов покупки */
    private Map<PayType, Integer> frequencyUsePayType = new HashMap<>();

    /** Карта со значениями и колличеством использования платильщика */
    private Map<Users, Integer> frequencyUseUser = new HashMap<>();

    /** Карта со значениями и колличеством использования типов дохода */
    private Map<IncomeType, Integer> frequencyUseIncomeType = new HashMap<>();

    /** Иконки и картинки в приложение */
    private Map<String, ImageCanvas> images = new HashMap<>();

    /** Уникальный для id нового типа объекта TypeValue. обавляется в поле code */
    private Integer uniqueId;

    /** Карта со значениями расположений окон. Ключ класс фрейма, значение координата */
    private Map<Class, Point> locationWindows = new HashMap<>();

    /** Карта со значениями размера окон. Ключ класс фрейма, значение размер окна */
    private Map<Class, Dimension> sizeWindows = new HashMap<>();

    /** Список покупок, которые отображются в данный момент */
    public static List<PayObject> viewPayObjects = new ArrayList<>();

    /** Список доходов */
    private List<Income> incomes = new ArrayList<>();


    /**
     * Экземпляр данного класса. Может быть только один на протяжение всего жизненного цикла приложения.
     * Синхронезируется. Данные для долговременного хранения должны быть в этом классе
     */
    private static ApplicationService instance;

    public synchronized static ApplicationService getInstance() {
        if (instance == null) {
            instance = new ApplicationService();
            instance.initDefaultProperties();
        }
        return instance;
    }

    /** Инициализирует стандартные настройки приложения. Используется при первом запуске приложения */
    private void initDefaultProperties() {
        importanceTypes = new ArrayList<>();
        payTypes = new ArrayList<>();
        users = new ArrayList<>();
        payObjects = new ArrayList<>();

        importanceTypes.addAll(ApplicationProperties.DEFAULT_IMPORTANCE);
        payTypes.addAll(ApplicationProperties.DEFAULT_PAY_TYPES);
        users.add(new Users(TypeValue.EMPTY, ""));
        incomeTypes.addAll(ApplicationProperties.DEFAULT_INCOME_TYPES);
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
                || (values.size() == 1 && TypeValue.EMPTY.equals((values.get(0)).getCode()))
                || values.contains(value);
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

    /** Добавляет данные нового дохода в частоту использования типов  */
    private void updateIncomeFrequencyUse(Income income) {
        if (frequencyUseUser.containsKey(income.getUser())) {
            frequencyUseUser.put(income.getUser(), frequencyUseUser.get(income.getUser()) + 1);
        } else {
            frequencyUseUser.put(income.getUser(), 1);
        }

        if (frequencyUseIncomeType.containsKey(income.getType())) {
            frequencyUseIncomeType.put(income.getType(), frequencyUseIncomeType.get(income.getType()) + 1);
        } else {
            frequencyUseIncomeType.put(income.getType(), 1);
        }
    }

    /** Обновляет все данные о частоте использования типов */
    public void updateAllFrequencyUse() {
        frequencyUsePayType = new HashMap<>();
        frequencyUseUser = new HashMap<>();
        frequencyUseImportance = new HashMap<>();
        payObjects.forEach(this::updateFrequencyUse);
        incomes.forEach(this::updateIncomeFrequencyUse);
    }

    /**
     * Сортирует типы по частоте использования
     *
     * @param list список типов
     * @param map  карта со значениями частоты использования. Ключ -> тип. Значение -> количество использований
     * @param <T>  класс унаследованный от TypeValues
     * @return тсортированный список типов
     */
    private <T extends TypeValue> List<T> sortTypes(List<T> list, Map<T, Integer> map) {
        List<T> sortedList = new ArrayList<>();
        sortedList.addAll(list);

        sortedList.sort((o1, o2) -> {
            Integer o1Count = map.get(o1);
            Integer o2Count = map.get(o2);
            if (o1Count != null || o2Count != null) {
                if (o1Count == null || TypeValue.EMPTY.equals(o1.getCode()))
                    return -1;
                if (o2Count == null || TypeValue.EMPTY.equals(o2.getCode()))
                    return 1;
                return o2Count.compareTo(o1Count);
            }
            return o1.getName().compareTo(o2.getName());
        });

        return sortedList;
    }

    /**
     * Сохраняет координату фоейма
     *
     * @param className объект класса, позицию фрейма которого нужно перезаписать
     * @param position  координата фрейма
     */
    void updateLocationWindow(Class className, Point position) {
        locationWindows.put(className, position);

        //Запысываем положение основного фрейма, так как при его закрытии происходит остановка приложения
        locationWindows.put(MainFrame.class, MonitoringMoney.getFrame(MainFrame.class).getLocation());

        writeData();
    }

    /**
     * Сохраняет размер фрейма
     *
     * @param className объект класса, размер фрейма которого нужно перезаписать
     * @param size      размер фрейма
     */
    void updateSizeWindow(Class className, Dimension size) {
        sizeWindows.put(className, size);

        writeData();
    }

    /** Добавляет новую покупку. Обновляет данные о частоте использования типов. */
    public void addPayObject(PayObject payObject) {
        if (payObjects.size() != 0 && frequencyUsePayType.size() == 0)
            updateAllFrequencyUse();

        payObjects.add(payObject);
        updateFrequencyUse(payObject);

        ApplicationService.writeData();
        MonitoringMoney.getFrame(MainFrame.class).updateData();
    }

    /** Добавляет новый доход */
    public void addIncome(Income income) {
        if (incomes == null) {
            incomes = new ArrayList<>();
        }
        incomes.add(income);
        updateIncomeFrequencyUse(income);
        ApplicationService.writeData();
        if (MonitoringMoney.getFrame(MainFrame.class).graphicsFrame != null)
            MonitoringMoney.getFrame(MainFrame.class).graphicsFrame.updateData();
    }

    /**
     * Добавляет новое значение определённого атрибута
     *
     * @param name      наименование нового значения атрибута
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
        } else if (IncomeType.class.equals(className)) {
            newValue = new IncomeType(getNewUniqueCode(), name);
            incomeTypes.add((IncomeType) newValue);
        }

        writeData();
        return newValue;
    }

    /**
     * Удаляет переданные значения атрибутов. Если данное значение есть хотябы у одной покупки, то оно не будет удалено
     *
     * @param removeList Список значений атрибутов, которые нужно удалить.
     */
    public void removeTypes(List<TypeValue> removeList) {
        if (removeList == null || removeList.isEmpty())
            return;

        Set<TypeValue> useRemoveTypeSet = new HashSet<>();
        if (!(removeList.get(0) instanceof DesiredPurchase))
        for (PayObject payObject : payObjects) {
            if (removeList.contains(payObject.getImportance()) && !useRemoveTypeSet.contains(payObject.getImportance()))
                useRemoveTypeSet.add(payObject.getImportance());
            if (removeList.contains(payObject.getPayType()) && !useRemoveTypeSet.contains(payObject.getPayType()))
                useRemoveTypeSet.add(payObject.getPayType());
            if (removeList.contains(payObject.getUser()) && !useRemoveTypeSet.contains(payObject.getUser()))
                useRemoveTypeSet.add(payObject.getUser());
        }

        removeList.stream()
                .filter(importanceType -> !useRemoveTypeSet.contains(importanceType))
                .forEach(value -> {
                    MonitoringMoney.getFrame(AddFrame.class).removeSelectElement(value);
                    MonitoringMoney.getFrame(MainFrame.class).removeSelectElement(value);
                    MonitoringMoney.getFrame(AddIncomeFrame.class).removeSelectElement(value);

                    if (value instanceof PayType) {
                        if (payTypes.contains(value))
                            payTypes.remove(value);
                    } else if (value instanceof ImportanceType) {
                        if (importanceTypes.contains(value))
                            importanceTypes.remove(value);
                    } else if (value instanceof Users) {
                        if (users.contains(value))
                            users.remove(value);
                    } else if (value instanceof DesiredPurchase) {
                        if (desiredPurchases.contains(value))
                            desiredPurchases.remove(value);
                    }
                });
        removeList.clear();
    }

    /**
     * @param frame фрейм для которого нужно получить расположение
     * @return координату фрейма
     */
    public Point getWindowLocation(Frame frame) {
        if (locationWindows == null)
            locationWindows = new HashMap<>();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getWindowSize(frame);
        if (!locationWindows.containsKey(frame.getClass())) {
            Integer x = (int) (screenSize.width / 2 - frameSize.getWidth() / 2);
            Integer y = (int) (screenSize.height / 2 - frameSize.getHeight() / 2);
            locationWindows.put(frame.getClass(), new Point(x, y));
        }

        Point point = locationWindows.get(frame.getClass());
        if (screenSize.getWidth() < point.getX() + frameSize.getWidth()
                || screenSize.getHeight() < point.getY() + frameSize.getHeight()) {
            Integer x = (int) (screenSize.width / 2 - frameSize.getWidth() / 2);
            Integer y = (int) (screenSize.height / 2 - frameSize.getHeight() / 2);
            point = new Point(x, y);
            locationWindows.put(frame.getClass(), point);
        }

        return point;
    }

    /**
     * @param frame фрейма для которого нужно получить размер
     * @return размер фрейма
     */
    public Dimension getWindowSize(Frame frame) {
        if (sizeWindows == null)
            sizeWindows = new HashMap<>();

        if (!frame.isResizable()) {
            return ApplicationProperties.DEFAULT_FRAME_SIZE.get(frame.getClass());
        }

        if (!sizeWindows.containsKey(frame.getClass())) {
            sizeWindows.put(frame.getClass(), ApplicationProperties.DEFAULT_FRAME_SIZE.get(frame.getClass()));
        }
        return sizeWindows.get(frame.getClass());
    }

    public void removePayObjects(List<PayObject> objects) {
        payObjects.removeAll(objects);
    }

    public void removeIncomes(List<Income> objects) {
        incomes.removeAll(objects);
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
     * @return все доходы в течении выбранного времени в основном окне <code>MainFrame</code>
     */
    List<Income> getIncomes() {
        if (instance.incomes == null) {
            instance.incomes = new ArrayList<>();
        }

        Date dateFrom = MonitoringMoney.getFrame(MainFrame.class).getDateFrom();
        Date dateTo = MonitoringMoney.getFrame(MainFrame.class).getDateTo();

        return instance.incomes.stream()
                .filter(obj -> dateFrom == null || obj.getDate().equals(dateFrom) || obj.getDate().after(dateFrom))
                .filter(obj -> dateTo == null || obj.getDate().before(dateTo))
                .collect(Collectors.toList());
    }

    /**
     * @return Отсортированный список уровней важности
     */
    public ImportanceType[] getSortedImportance() {
        List<ImportanceType> sortedList = sortTypes(getImportanceTypes(), frequencyUseImportance);
        return sortedList.toArray(new ImportanceType[sortedList.size()]);
    }

    /**
     * @return Отсортированные список типов покупок
     */
    public PayType[] getSortedPayTypes() {
        List<PayType> sortedList = sortTypes(getPayTypes(), frequencyUsePayType);
        return sortedList.toArray(new PayType[sortedList.size()]);
    }

    /**
     * @return Отсортированный список платильщиков
     */
    public Users[] getSortedUsers() {
        List<Users> sortedList = sortTypes(getUsers(), frequencyUseUser);
        return sortedList.toArray(new Users[sortedList.size()]);
    }

    /**
     * @return Отсортированный список платильщиков
     */
    public IncomeType[] getSortedIncomeTypes() {
        List<IncomeType> sortedList = sortTypes(getIncomeTypes(), frequencyUseIncomeType);
        return sortedList.toArray(new IncomeType[sortedList.size()]);
    }

    /**
     * Генерирует новый уникальный код
     *
     * @return уникальный код
     */
    public synchronized String getNewUniqueCode() {
        if (uniqueId == null)
            uniqueId = 0;
        uniqueId++;
        return "auto" + uniqueId;
    }

    /**
     * @param payObjects покупки, текст о которых нужно составить
     * @return сгрупированный текст о всех переданных покупок
     */
    public String getTextPayObjects(List<PayObject> payObjects) {
        return payObjects
                .stream()
                .map(PayObject::toString)
                .reduce((s1, s2) -> s1 + "\n\n" + s2)
                .orElse("");
    }

    /**
     * @param payObjects покупки, суммарную стоимость которых нужно посчитать
     * @return суммарное колличество стоимости покупок
     */
    public Integer getSumPrice(List<PayObject> payObjects) {
        return payObjects
                .stream()
                .map(PayObject::getPrice)
                .reduce((s1, s2) -> s1 + s2)
                .orElse(0);
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

    public List<IncomeType> getIncomeTypes() {
        if (incomeTypes == null)
            incomeTypes = new ArrayList<>();
        return incomeTypes;
    }

    public List<DesiredPurchase> getDesiredPurchases() {
        if (desiredPurchases == null)
            desiredPurchases = new ArrayList<>();
        return desiredPurchases;
    }

    Map<String, ImageCanvas> getImages() {
        if (images == null)
            images = new HashMap<>();
        return images;
    }

    /** Создаёт новый файл для хранения долговременной информации */
    public static void createNewData() throws IOException {
        if (ApplicationProperties.BUY_FILE.createNewFile()) {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(ApplicationProperties.BUY_FILE));
            os.writeObject(getInstance());
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
}

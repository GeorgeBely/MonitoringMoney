package ru.MonitoringMoney;

import ru.MonitoringMoney.frame.*;
import ru.MonitoringMoney.types.ImportanceType;
import ru.MonitoringMoney.types.PayType;

import java.awt.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ApplicationProperties {

    /** Код пустого поля свойства покупки */
    public static final String EMPTY = "empty";

    /** Файл с данными о покупках */
    public static final File BUY_FILE = new File("MoneyData.mm");

    /** Формат даты для поля ввода даты */
    public static final DateFormat FORMAT_DATE = DateFormat.getDateInstance(DateFormat.SHORT);

    /** Формат даты отображающий только название месяца и год*/
    public static final DateFormat FORMAT_MONTH_AND_YEAR = new SimpleDateFormat("LLLL yyyy");

    /** Карта со значениями размера фрейма по умолчанию. Ключ объект класса фрейма, значение размер фрейма */
    public static final Map<Class, Dimension> DEFAULT_FRAME_SIZE = new HashMap<Class, Dimension>() {{
        put(AddFrame.class, new Dimension(250, 320));
        put(MainFrame.class, new Dimension(545, 260));
        put(GraphicsFrame.class, new Dimension(515, 335));
        put(EditFrame.class, new Dimension(660, 390));
        put(DesiredPurchaseFrame.class, new Dimension(300, 400));
    }};


    /**
     * Список уровней вкажности по умолчанию
     */
    public static final List<ImportanceType> DEFAULT_IMPORTANCE = new ArrayList<ImportanceType>() {{
        add(new ImportanceType(EMPTY, ""));
        add(new ImportanceType("very_important", "Необходимо"));
        add(new ImportanceType("important", "Нужно"));
        add(new ImportanceType("medium", "Полезное"));
        add(new ImportanceType("low", "Хочется"));
        add(new ImportanceType("very_low", "Бесполезное"));
    }};

    /**
     * Список типов покупки по умолчанию
     */
    public static final List<PayType> DEFAULT_PAY_TYPES = new ArrayList<PayType>() {{
        add(new PayType(EMPTY, ""));
        add(new PayType("buz", "Транспорт"));
        add(new PayType("dining_room", "Столовая"));
        add(new PayType("delicacy", "Вкусности"));
        add(new PayType("duty", "Пошлина"));
        add(new PayType("fast_food", "Фаст фуд"));
        add(new PayType("medicine", "Лекарства/здоровье"));
    }};
}

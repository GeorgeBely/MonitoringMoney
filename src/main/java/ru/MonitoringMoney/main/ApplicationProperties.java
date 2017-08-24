package ru.MonitoringMoney.main;

import ru.MonitoringMoney.frame.*;
import ru.MonitoringMoney.types.ImportanceType;
import ru.MonitoringMoney.types.IncomeType;
import ru.MonitoringMoney.types.PayType;
import ru.MonitoringMoney.types.TypeValue;

import java.awt.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Характеристики приложения. Значения по умолчанию, и некоторые константы
 */
public class ApplicationProperties {

    /** Максимальное колличество символов во всплывающей информацие на графиках */
    public static final Integer MAX_INFORM_GRAPHICS_MESSAGE_CHAR = 100;

    /** Файл с данными о покупках */
    public static final File BUY_FILE = new File("MoneyData.mm");

    /** Формат даты для поля ввода даты */
    public static final DateFormat FORMAT_DATE = DateFormat.getDateInstance(DateFormat.SHORT);

    /** Формат даты отображающий только название месяца и год*/
    public static final DateFormat FORMAT_MONTH_AND_YEAR = new SimpleDateFormat("LLLL yyyy");
    public static final DateFormat FORMAT_MONTH_AND_YEAR_FOR_PARSE = new SimpleDateFormat("MMMM yyyy");

    /** Карта со значениями размера фрейма по умолчанию. Ключ объект класса фрейма, значение размер фрейма */
    public static final Map<Class, Dimension> DEFAULT_FRAME_SIZE = new HashMap<Class, Dimension>() {{
        put(AddFrame.class, new Dimension(250, 320));
        put(MainFrame.class, new Dimension(580, 260));
        put(GraphicsFrame.class, new Dimension(515, 335));
        put(EditFrame.class, new Dimension(660, 390));
        put(DesiredPurchaseFrame.class, new Dimension(300, 400));
        put(AddIncomeFrame.class, new Dimension(250, 285));
        put(FrameAddPropertyValues.class, new Dimension(250, 130));
    }};

    /** Список уровней важности по умолчанию */
    public static final List<ImportanceType> DEFAULT_IMPORTANCE = new ArrayList<ImportanceType>() {{
        add(new ImportanceType(TypeValue.EMPTY, ""));
        add(new ImportanceType("very_important", "Необходимо"));
        add(new ImportanceType("important", "Нужно"));
        add(new ImportanceType("medium", "Полезное"));
        add(new ImportanceType("low", "Хочется"));
        add(new ImportanceType("very_low", "Бесполезное"));
    }};

    /** Список типов покупки по умолчанию */
    public static final List<PayType> DEFAULT_PAY_TYPES = new ArrayList<PayType>() {{
        add(new PayType(TypeValue.EMPTY, ""));
        add(new PayType("buz", "Транспорт"));
        add(new PayType("dining_room", "Столовая"));
        add(new PayType("delicacy", "Вкусности"));
        add(new PayType("duty", "Пошлина"));
        add(new PayType("fast_food", "Фаст фуд"));
        add(new PayType("medicine", "Лекарства/здоровье"));
    }};

    /** Список типов покупки по умолчанию */
    public static final List<IncomeType> DEFAULT_INCOME_TYPES = new ArrayList<IncomeType>() {{
        add(new IncomeType(TypeValue.EMPTY, ""));
        add(new IncomeType("salary", "Зарплата"));
        add(new IncomeType("prepaid_expense", "Аванс"));
    }};
}

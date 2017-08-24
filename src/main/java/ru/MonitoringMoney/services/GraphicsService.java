package ru.MonitoringMoney.services;

import org.apache.commons.lang.time.DateUtils;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import ru.MonitoringMoney.main.ApplicationProperties;
import ru.MonitoringMoney.types.PayObject;
import ru.MonitoringMoney.types.Income;
import ru.mangeorge.swing.service.PieService;

import java.util.*;


/**
 * Сервис для работы с компонентами графиков.
 */
public class GraphicsService {

    public static final String ALL_COAST = "Всего затрат";

    public static final String NO_DATA_MESSAGE = Locale.getDefault().equals(new Locale("ru", "RU")) ? "Нет данных" : "No data available";

    /** Данные отображающиеся в выпадающем списке выбора графика */
    public static final String[] GRAPHICS_NAMES = new String[]{"Процентное соотношение покупок", "График затрат по времени",
            "Суммарные затраты по времени", "Доходы/Расходы"};

    /** Данные отображающиеся в выпадающем списке выбора данных по типу */
    public static final String[] VIEW_DATA_NAMES = new String[]{"", "Тип покупки", "Уровень важности", "Платильщик", "Тип дохода"};


    public static TimeSeriesCollection getIncomeChartData(String selectData) {
        Map<String, Map<Date, Integer>> valueMap = new HashMap<>();

        for (Income income : ApplicationService.getInstance().getIncomes()) {
            String name = "Доход " + getIncomeName(income, selectData);

            Integer coast = income.getAmountMoney();
            Date date = DateUtils.truncate(income.getDate(), Calendar.MONTH);

            addToValueMap(valueMap, name, coast, date);
        }

        for (PayObject payObject : ApplicationService.viewPayObjects) {
            String name = "Расход " + getPayObjectName(payObject, selectData);

            Integer coast = payObject.getPrice();
            Date date = DateUtils.truncate(payObject.getDate(), Calendar.MONTH);

            addToValueMap(valueMap, name, coast, date);
        }

        return createTimeSeriesCollection(valueMap);
    }

    /**
     * Подготавливает данные для графика "Категории", по заданному типу.
     *
     * @param selectData наименование данных, которые нужно отобразить. Берутся из массива {VIEW_DATA_NAMES}
     * @return данные для графика "Категории"
     */
    public static CategoryDataset getBarChartData(String selectData) {
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

        Map<Date, Map<Object, Integer>> sortDataMap = new TreeMap<>(Date::compareTo);
        for (PayObject payObject : ApplicationService.viewPayObjects) {
            Date month = DateUtils.truncate(payObject.getDate(), Calendar.MONTH);
            Map<Object, Integer> monthValues = sortDataMap.get(month);

            Object selectValue;
            if (VIEW_DATA_NAMES[1].equals(selectData)) {
                selectValue = payObject.getPayType();
            } else if (VIEW_DATA_NAMES[2].equals(selectData)) {
                selectValue = payObject.getImportance();
            } else if (VIEW_DATA_NAMES[3].equals(selectData)) {
                selectValue = payObject.getUser();
            } else {
                selectValue = ALL_COAST;
            }

            if (monthValues == null) {
                Map<Object, Integer> valueMap = new HashMap<>();
                valueMap.put(selectValue, payObject.getPrice());
                sortDataMap.put(month, valueMap);
            } else {
                if (monthValues.containsKey(selectValue)) {
                    Integer price = monthValues.get(selectValue);
                    monthValues.put(selectValue, payObject.getPrice() + price);
                } else {
                    monthValues.put(selectValue, payObject.getPrice());
                }
            }
        }

        for (Map.Entry<Date, Map<Object, Integer>> entry : sortDataMap.entrySet()) {
            for (Map.Entry<Object, Integer> value : entry.getValue().entrySet()) {
                String monthName = ApplicationProperties.FORMAT_MONTH_AND_YEAR.format(entry.getKey());
                dataSet.addValue(value.getValue(), value.getKey().toString(), monthName);
            }
        }
        return dataSet;
    }

    /**
     * Подготавливает данные для графика "Временные линии", по заданному типу.
     *
     * @param selectData наименование данных, которые нужно отобразить. Берутся из массива {VIEW_DATA_NAMES}
     * @return данные для графика "Временные линии"
     */
    public static TimeSeriesCollection getTimeSeriesData(String selectData) {
        Map<String, Map<Date, Integer>> valueMap = new HashMap<>();

        for (PayObject payObject : ApplicationService.viewPayObjects) {
            String name = getPayObjectName(payObject, selectData);
            Integer coast = payObject.getPrice();
            Date date = payObject.getDate();

            addToValueMap(valueMap, name, coast, date);
        }

        return createTimeSeriesCollection(valueMap);
    }

    /**
     * Подготавливает данные для графика пирожок, по заданному типу.
     *
     * @param selectData наименование данных, которые нужно отобразить. Берутся из массива {VIEW_DATA_NAMES}
     * @return данные для графика "пирожок"
     */
    public static PieDataset getCountMoneyPieData(String selectData) {
        if (VIEW_DATA_NAMES[0].equals(selectData))
            selectData = VIEW_DATA_NAMES[1];

        Map<String, Number> valueMap = new HashMap<>();
        for (PayObject payObject : ApplicationService.viewPayObjects) {
            String name = getPayObjectName(payObject, selectData);
            Integer coast = payObject.getPrice();

            if (valueMap.containsKey(name)) {
                valueMap.put(name, valueMap.get(name).intValue() + coast);
            } else {
                valueMap.put(name, coast);
            }
        }
        return PieService.getCountMoneyPieData(valueMap);
    }

    /**
     * Возвращает наименование значения заданного атрибута {selectData} у переданной покупки {payObject}
     *
     * @param payObject  покупка
     * @param selectData наименование данных, которые нужно отобразить. Берутся из массива {VIEW_DATA_NAMES}
     * @return Наименование значения атрибута
     */
    private static String getPayObjectName(PayObject payObject, String selectData) {
        if (VIEW_DATA_NAMES[3].equals(selectData)) {
            return payObject.getUser().toString();
        } else if (VIEW_DATA_NAMES[2].equals(selectData)) {
            return payObject.getImportance().toString();
        } else if (VIEW_DATA_NAMES[1].equals(selectData)) {
            return payObject.getPayType().toString();
        }
        return "";
    }

    /**
     * Возвращает наименование значения заданного атрибута {selectData} у переданного дохода {income}
     *
     * @param income     доход
     * @param selectData наименование данных, которые нужно отобразить. Берутся из массива {VIEW_DATA_NAMES}
     * @return Наименование значения атрибута
     */
    private static String getIncomeName(Income income, String selectData) {
        if (VIEW_DATA_NAMES[3].equals(selectData)) {
            return income.getUser().toString();
        } else if (VIEW_DATA_NAMES[4].equals(selectData)) {
            return income.getType().toString();
        }
        return "";
    }

    private static TimeSeriesCollection createTimeSeriesCollection(Map<String, Map<Date, Integer>> valueMap) {
        TimeSeriesCollection dataSet = new TimeSeriesCollection();
        for (Map.Entry<String, Map<Date, Integer>> value : valueMap.entrySet()) {
            Integer sum = value.getValue().values().stream().reduce((i1, i2) -> i1 + i2).orElse(0);
            TimeSeries series = new TimeSeries(value.getKey() + ": " + sum);
            for (Map.Entry<Date, Integer> dateCoast : value.getValue().entrySet()) {
                series.add(new Minute(dateCoast.getKey()), dateCoast.getValue());
            }
            dataSet.addSeries(series);
        }

        return dataSet;
    }

    private static void addToValueMap(Map<String, Map<Date, Integer>> valueMap, String name, Integer coast, Date date) {
        if (valueMap.containsKey(name)) {
            if (valueMap.get(name).containsKey(date)) {
                valueMap.get(name).put(date, valueMap.get(name).get(date) + coast);
            } else {
                valueMap.get(name).put(date, coast);
            }
        } else {
            Map<Date, Integer> coastMap = new TreeMap<>(Date::compareTo);
            coastMap.put(date, coast);
            valueMap.put(name, coastMap);
        }
    }
}

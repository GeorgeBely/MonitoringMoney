package ru.MonitoringMoney.services;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import ru.MonitoringMoney.ApplicationProperties;
import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.main.MonitoringMoney;

import java.awt.*;
import java.util.*;

/**
 * Сервис для работы с компонентами графиков.
 */
public class GraphicsService {

    private static final String ANOTHER_BLOCK_NAME = "Другие";

    public static final String ALL_COAST = "Всего затрат";

    public static final String[] GRAPHICS_NAMES = new String[]{"Процентное соотношение покупок", "График затрат по времени", "Суммарные затраты по времени"};

    public static final String[] VIEW_DATA_NAMES = new String[]{"", "Тип покупки", "Уровень важности", "Платильщик"};


    /**
     * Создание компонента графика "Пирог"
     *
     * @param name       наименование графика
     * @param background цвет панели графика
     * @return компонент графика "Пирог".
     */
    public static JFreeChart getPieComponent(String name, Color background) {
        JFreeChart chart = ChartFactory.createPieChart(name, null);
        chart.setBackgroundPaint(background);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}-{1}-({2})"));
        plot.setBackgroundPaint(background);
        plot.setLabelLinkStyle(PieLabelLinkStyle.STANDARD);

        String noDataMessage = "No data available";
        if (Locale.getDefault().equals(new Locale("ru", "RU")))
            noDataMessage = "Нет данных";
        plot.setNoDataMessage(noDataMessage);

        return chart;
    }

    /**
     * Создание компонента графика "График изминения значений по датам"
     *
     * @param name       наименование графика
     * @param nameX      наименование значения по абсцисе
     * @param nameY      наименование значения
     * @param background цвет панели графика
     * @return компонент графика "График изминения значений по датам".
     */
    public static JFreeChart getTimeSeriesComponent(String name, String nameX, String nameY, Color background) {
        JFreeChart chartCategory = ChartFactory.createTimeSeriesChart(name, nameX, nameY, getTimeSeriesData(""));
        chartCategory.setBackgroundPaint(background);

        return chartCategory;
    }

    public static JFreeChart getBatChartsComponent(String name, String nameX, String nameY, Color background) {
        JFreeChart chartBar = ChartFactory.createBarChart(name, nameX, nameY, getBarChartData(""));
        chartBar.setBackgroundPaint(background);

        return chartBar;
    }

    public static void updatePieData(JFreeChart pie, String selectData) {
        DefaultPieDataset data = getCountMoneyPieData(selectData);
        PiePlot plot = (PiePlot) pie.getPlot();

        plot.setExplodePercent((String) data.getKeys().get(0), 0.20);
        plot.setDataset(data);

        int greenCount = 255;
        int redCount = 0;
        int blueCount = 0;
        int step = 511/data.getKeys().size();
        for (Object keyObj : data.getKeys()) {
            plot.setSectionPaint(keyObj.toString(), new Color(redCount, greenCount, blueCount));
            if (greenCount > step) {
                greenCount -= step;
                redCount += step;
            } else if (blueCount < 255 - step) {
                blueCount += step;
                redCount -= step;
            }
        }
    }

    public static void updateTimeSeriesData(JFreeChart timeSeries, String selectData) {
        ((XYPlot) timeSeries.getPlot()).setDataset(getTimeSeriesData(selectData));
    }

    public static void updateBarData(JFreeChart timeSeries, String selectData) {
        ((CategoryPlot) timeSeries.getPlot()).setDataset(getBarChartData(selectData));
    }

    public static CategoryDataset getBarChartData(String selectData) {
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

        Map<Date, Map<Object, Integer>> sortDataMap = new TreeMap<>(Date::compareTo);
        for (PayObject payObject : ApplicationService.getPayObjects()) {
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

    public static TimeSeriesCollection getTimeSeriesData(String selectData) {
        Map<String, Map<Date, Integer>> valueMap = new HashMap<>();

        for (PayObject payObject : ApplicationService.getPayObjects()) {
            String name;
            Integer coast;
            Date date;
            if ((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUsePayType()) || VIEW_DATA_NAMES[1].equals(selectData)) {
                name = payObject.getPayType().toString();
                coast = payObject.getPrice();
                date = payObject.getDate();
            } else if ((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUseImportant()) || VIEW_DATA_NAMES[2].equals(selectData)) {
                name = payObject.getImportance().toString();
                coast = payObject.getPrice();
                date = payObject.getDate();
            } else if ((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUseUser()) || VIEW_DATA_NAMES[3].equals(selectData)) {
                name = payObject.getUser().toString();
                coast = payObject.getPrice();
                date = payObject.getDate();
            } else {
                name = payObject.getPayType().toString();
                coast = payObject.getPrice();
                date = payObject.getDate();
            }

            if (valueMap.containsKey(name)) {
                if (valueMap.get(name).containsKey(date)) {
                    valueMap.get(name).put(date, valueMap.get(name).get(date) + coast);
                } else {
                    valueMap.get(name).put(date, coast);
                }
            } else {
                Map<Date, Integer> coastMap = new HashMap<>();
                coastMap.put(date, coast);
                valueMap.put(name, coastMap);
            }
        }

        TimeSeriesCollection dataSet = new TimeSeriesCollection();
        for (Map.Entry<String, Map<Date, Integer>> value : valueMap.entrySet()) {
            TimeSeries series = new TimeSeries(value.getKey());
            for (Map.Entry<Date, Integer> dateCoast : value.getValue().entrySet()) {
                series.add(new Minute(dateCoast.getKey()), dateCoast.getValue());
            }
            dataSet.addSeries(series);
        }


        return dataSet;
    }

    public static DefaultPieDataset getCountMoneyPieData(String selectData) {
        Map<String, Integer> valueMap = new HashMap<>();
        for (PayObject payObject : ApplicationService.getPayObjects()) {
            String name;
            Integer coast;
            if ((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUsePayType()) || VIEW_DATA_NAMES[1].equals(selectData)) {
                name = payObject.getPayType().toString();
                coast = payObject.getPrice();
            } else if ((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUseImportant()) || VIEW_DATA_NAMES[2].equals(selectData)) {
                name = payObject.getImportance().toString();
                coast = payObject.getPrice();
            } else if ((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUseUser()) || VIEW_DATA_NAMES[3].equals(selectData)) {
                name = payObject.getUser().toString();
                coast = payObject.getPrice();
            } else {
                name = payObject.getPayType().toString();
                coast = payObject.getPrice();
            }

            if (valueMap.containsKey(name)) {
                valueMap.put(name, valueMap.get(name) + coast);
            } else {
                valueMap.put(name, coast);
            }
        }

        Map<String, Integer> sortedValueMap = new TreeMap<>((s1, s2) -> {
            if (ANOTHER_BLOCK_NAME.equals(s1))
                return 1;
            if (ANOTHER_BLOCK_NAME.equals(s2))
                return -1;
            return valueMap.get(s2).compareTo(valueMap.get(s1));
        });
        sortedValueMap.putAll(valueMap);

        if (valueMap.size() > 10) {
            Map<String, Integer> shortValueMap = new HashMap<>();
            int count = 0;
            for (Map.Entry<String, Integer> value : sortedValueMap.entrySet()) {
                if (count < 9) {
                    shortValueMap.put(value.getKey(), value.getValue());
                } else {
                    if (shortValueMap.get(ANOTHER_BLOCK_NAME) == null) {
                        shortValueMap.put(ANOTHER_BLOCK_NAME, value.getValue());
                    } else {
                        shortValueMap.put(ANOTHER_BLOCK_NAME, shortValueMap.get(ANOTHER_BLOCK_NAME) + value.getValue());
                    }
                }
                count++;
            }
            sortedValueMap.clear();
            sortedValueMap.putAll(shortValueMap);
        }

        DefaultPieDataset defaultPieDataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> value : sortedValueMap.entrySet()) {
            defaultPieDataset.setValue(value.getKey(), value.getValue());
        }
        return defaultPieDataset;
    }

}

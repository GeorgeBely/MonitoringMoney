package ru.MonitoringMoney.services;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.main.MonitoringMoney;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Сервис для работы с компонентами графиков.
 */
public class GraphicsService {

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
        JFreeChart chartCategory = ChartFactory.createTimeSeriesChart(name, nameX, nameY, getTimeSeriesData());
        chartCategory.setBackgroundPaint(background);

        return chartCategory;
    }

    public static void updatePieData(JFreeChart pie) {
        DefaultPieDataset data = getCountMoneyPieData();
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

    public static void updateTimeSeriesData(JFreeChart timeSeries) {
        ((XYPlot) timeSeries.getPlot()).setDataset(getTimeSeriesData());
    }

    public static TimeSeriesCollection getTimeSeriesData() {
        Map<String, Map<Date, Integer>> valueMap = new HashMap<>();

        for (PayObject payObject : getPayObjects()) {
            String name;
            Integer coast;
            Date date;
            if (!MonitoringMoney.frame.isUsePayType()) {
                name = payObject.getPayType().toString();
                coast = payObject.getPrice();
                date = payObject.getDate();
            } else if (!MonitoringMoney.frame.isUseImportant()) {
                name = payObject.getImportance().toString();
                coast = payObject.getPrice();
                date = payObject.getDate();
            } else if (!MonitoringMoney.frame.isUseUser()) {
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

    public static DefaultPieDataset getCountMoneyPieData() {
        Map<String, Integer> valueMap = new HashMap<>();
        for (PayObject payObject : getPayObjects()) {
            String name;
            Integer coast;
            if (!MonitoringMoney.frame.isUsePayType()) {
                name = payObject.getPayType().toString();
                coast = payObject.getPrice();
            } else if (!MonitoringMoney.frame.isUseImportant()) {
                name = payObject.getImportance().toString();
                coast = payObject.getPrice();
            } else if (!MonitoringMoney.frame.isUseUser()) {
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

        DefaultPieDataset defaultPieDataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> value : valueMap.entrySet()) {
            defaultPieDataset.setValue(value.getKey(), value.getValue());
        }
        return defaultPieDataset;
    }

    private static List<PayObject> getPayObjects() {
        List<PayObject> payObjects = MonitoringMoney.frame.getPayObjectWithCurrentFilters();
        if (payObjects.isEmpty()) {
            payObjects = ApplicationService.getInstance().getPayObjectsWithFilters(null, null, null, null, null, null, null,null, true);
        }
        return payObjects;
    }

}

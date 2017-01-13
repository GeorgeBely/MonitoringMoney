package ru.MonitoringMoney.frame;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jfree.chart.*;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import ru.MonitoringMoney.ApplicationProperties;
import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.GraphicsService;
import ru.MonitoringMoney.services.ImageService;
import ru.mangeorge.swing.service.PieService;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;

/**
 * Фрейм с графической информацией
 */
public class GraphicsFrame extends JFrame {

    private static final long serialVersionUID = 2295386769890035598L;

    private static final String FRAME_NAME = "Графики затрат";


    private ChartPanel piePanel;
    private ChartPanel categoryPanel;
    private ChartPanel barPanel;
    private JFreeChart pieChart;
    private JFreeChart timeSerialChart;
    private JFreeChart barChart;
    private JComboBox selectGraphic;
    private JComboBox selectViewData;

    GraphicsFrame() {
        setLocation(ApplicationService.getInstance().getWindowLocation(GraphicsFrame.class));
        setSize(ApplicationService.getInstance().getWindowSize(GraphicsFrame.class));
        setVisible(true);
        setTitle(FRAME_NAME);
        setIconImage(ImageService.getGraphicsImage());
        toFront();
        addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) { resizeFrame(); }
            public void componentMoved(ComponentEvent e) { }
            public void componentShown(ComponentEvent e) {}
            public void componentHidden(ComponentEvent e) {
                ApplicationService.getInstance().updateSizeWindow(GraphicsFrame.class, getSize());
                ApplicationService.getInstance().updateLocationWindow(GraphicsFrame.class, getLocation());
            }
        });


        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);

        selectGraphic = new JComboBox<Object>(GraphicsService.GRAPHICS_NAMES) {{
            setBounds(40, 5, 240, 30);
            addActionListener(e -> useSelectGraphic());
        }};
        panel.add(selectGraphic);

        selectViewData = new JComboBox<Object>(GraphicsService.VIEW_DATA_NAMES) {{
            setBounds(290, 5, 150, 30);
            addActionListener(e -> updateData());
        }};
        panel.add(selectViewData);

        pieChart = PieService.createChart("Процентное соотношение покупок", GraphicsService.getCountMoneyPieData(""));
        pieChart.setBackgroundPaint(this.getBackground());
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(this.getBackground());
        plot.setToolTipGenerator((pieDataset, comparable) -> {
            List<String> texts = new ArrayList<>();
            String selectData = (String) selectViewData.getSelectedItem();
            for (PayObject payObject : ApplicationService.viewPayObjects) {
                if (((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUseImportant()) || GraphicsService.VIEW_DATA_NAMES[2].equals(selectData))
                        && payObject.getImportance().getName().equals(comparable)) {
                    if (!texts.contains(payObject.getDescription())) {
                        texts.add(payObject.getDescription());
                    }
                } else if (((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUseUser()) || GraphicsService.VIEW_DATA_NAMES[3].equals(selectData))
                        && payObject.getUser().getName().equals(comparable)) {
                    if (!texts.contains(payObject.getDescription())) {
                        texts.add(payObject.getDescription());
                    }
                } else if (payObject.getPayType().getName().equals(comparable)) {
                    texts.add(payObject.getDescription());
                }
            }
            String result = "";
            for (String text : texts) {
                if (result.length() < ApplicationProperties.MAX_INFORM_GRAPHICS_MESSAGE_CHAR) {
                    if (!result.isEmpty()) {
                        result += " ";
                    }
                    if (result.length() + text.length() > ApplicationProperties.MAX_INFORM_GRAPHICS_MESSAGE_CHAR) {
                        if (ApplicationProperties.MAX_INFORM_GRAPHICS_MESSAGE_CHAR - result.length() > 3) {
                            result += text.substring(0, ApplicationProperties.MAX_INFORM_GRAPHICS_MESSAGE_CHAR - result.length() - 3);
                        }
                        result += "...";
                    } else {
                        result += text;
                    }
                }
            }
            return result;
        });

        piePanel = new ChartPanel(pieChart) {{
            setLocation(5, 50);
            setSize(super.getWidth() - 30, super.getHeight()  - 55);
            setVisible(false);
        }};
        piePanel.addChartMouseListener(new ChartMouseListener() {
            public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
                if (chartMouseEvent.getEntity() instanceof PieSectionEntity) {
                    String name = ((PieSectionEntity) chartMouseEvent.getEntity()).getSectionKey().toString();
                    updatePieData(name);
                }
            }
            public void chartMouseMoved(ChartMouseEvent chartMouseEvent) { }
        });
        panel.add(piePanel);

        timeSerialChart = ChartFactory.createTimeSeriesChart("График затрат по времени", "Дата покупок", "колличество", GraphicsService.getTimeSeriesData(""));
        timeSerialChart.setBackgroundPaint(this.getBackground());
        XYPlot timeSerialPlot = (XYPlot) timeSerialChart.getPlot();
        XYItemRenderer renderer = timeSerialPlot.getRenderer();
        renderer.setBaseToolTipGenerator((xyDataset, i, j) -> {
            Date date = new Date();
            date.setTime(xyDataset.getX(i, j).longValue());
            Number value = xyDataset.getY(i, j);
            String categoryName = xyDataset.getSeriesKey(i).toString();

            String selectData = (String) selectViewData.getSelectedItem();
            List<PayObject> categoryPayObjects = new ArrayList<>();
            for (PayObject payObject : ApplicationService.viewPayObjects) {
                if (((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUseImportant()) || GraphicsService.VIEW_DATA_NAMES[2].equals(selectData))
                        && payObject.getImportance().getName().equals(categoryName)) {
                    categoryPayObjects.add(payObject);
                } else if (((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUseUser()) || GraphicsService.VIEW_DATA_NAMES[3].equals(selectData))
                        && payObject.getUser().getName().equals(categoryName)) {
                    categoryPayObjects.add(payObject);
                } else if (payObject.getPayType().getName().equals(categoryName)) {
                    categoryPayObjects.add(payObject);
                }
            }
            categoryPayObjects.sort(Comparator.comparing(PayObject::getDate));
            PayObject selectPayObject = categoryPayObjects.get(j);

            String result = categoryName + ": (" + ApplicationProperties.FORMAT_DATE.format(date) + ", " + value + ")";
            if (StringUtils.isNotBlank(selectPayObject.getDescription())) {
                result += " - " + selectPayObject.getDescription();
            }
            return result;
        });
        categoryPanel = new ChartPanel(timeSerialChart) {{
            setLocation(5, 50);
            setSize(super.getWidth() - 30, super.getHeight()  - 55);
            setVisible(false);
        }};
        panel.add(categoryPanel);

        barChart = ChartFactory.createBarChart("Суммарные затраты по времени", "Месяц", "колличество", GraphicsService.getBarChartData(""));
        barChart.setBackgroundPaint(this.getBackground());
        barPanel = new ChartPanel(barChart) {{
            setLocation(5, 50);
            setSize(super.getWidth() - 30, super.getHeight()  - 55);
            setVisible(false);
        }};
        barPanel.addChartMouseListener(new ChartMouseListener() {
            public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
                try {
                    CategoryItemEntity entity = (CategoryItemEntity) chartMouseEvent.getEntity();
                    String rowName = entity.getRowKey().toString();
                    Date month = ApplicationProperties.FORMAT_MONTH_AND_YEAR_FOR_PARSE.parse(entity.getColumnKey().toString());
                    Date endMonth = DateUtils.addDays(DateUtils.addMonths(DateUtils.truncate(month, Calendar.MONTH), 1), -1);
                    MonitoringMoney.mainFrame.dateFromText.setValue(month);
                    MonitoringMoney.mainFrame.dateToText.setValue(endMonth);
                    selectGraphic.setSelectedItem(GraphicsService.GRAPHICS_NAMES[0]);

                    if (!GraphicsService.ALL_COAST.equals(rowName)) {
                        updatePieData(rowName);
                    }

                    MonitoringMoney.mainFrame.refreshText();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            public void chartMouseMoved(ChartMouseEvent chartMouseEvent) { }
        });
        panel.add(barPanel);

        updatePieData("");
        useSelectGraphic();
    }

    private void updatePieData(String name) {
        if (StringUtils.isNotBlank(name)) {
            String selectData = (String) selectViewData.getSelectedItem();
            List graphicValues = ((PiePlot) piePanel.getChart().getPlot()).getDataset().getKeys();

            if ((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUsePayType()) || GraphicsService.VIEW_DATA_NAMES[1].equals(selectData)) {
                MonitoringMoney.mainFrame.selectPayTypeValue(name, graphicValues);
            } else if ((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUseImportant()) || GraphicsService.VIEW_DATA_NAMES[2].equals(selectData)) {
                MonitoringMoney.mainFrame.selectImportanceValue(name, graphicValues);
            } else {
                MonitoringMoney.mainFrame.selectUserValue(name, graphicValues);
            }
        }
        if (!PieService.ANOTHER_BLOCK_NAME.equals(name)) {
            setSelectViewData();
        }

        MonitoringMoney.mainFrame.refreshText();
    }

    private void setSelectViewData() {
        String selectData = (String) selectViewData.getSelectedItem();
        if (MonitoringMoney.mainFrame.isUsePayType() && !GraphicsService.VIEW_DATA_NAMES[1].equals(selectData)) {
            selectViewData.setSelectedItem(GraphicsService.VIEW_DATA_NAMES[1]);
        } else if (MonitoringMoney.mainFrame.isUseImportant() && !GraphicsService.VIEW_DATA_NAMES[2].equals(selectData)) {
            selectViewData.setSelectedItem(GraphicsService.VIEW_DATA_NAMES[2]);
        } else if (MonitoringMoney.mainFrame.isUseUser() && !GraphicsService.VIEW_DATA_NAMES[3].equals(selectData)) {
            selectViewData.setSelectedItem(GraphicsService.VIEW_DATA_NAMES[3]);
        } else {
            selectGraphic.setSelectedItem(GraphicsService.GRAPHICS_NAMES[1]);
        }
    }

    private void resizeFrame() {
        Container frame = piePanel.getParent();
        piePanel.setSize(frame.getWidth() - 15, frame.getHeight() - 55);
        categoryPanel.setSize(frame.getWidth() - 15, frame.getHeight() - 55);
        barPanel.setSize(frame.getWidth() - 15, frame.getHeight() - 55);
    }

    void updateData() {
        String selectDataValue = (String) selectViewData.getSelectedItem();
        PieService.updatePieData(pieChart, GraphicsService.getCountMoneyPieData(selectDataValue));
        ((CategoryPlot) barChart.getPlot()).setDataset(GraphicsService.getBarChartData(selectDataValue));
        ((XYPlot) timeSerialChart.getPlot()).setDataset(GraphicsService.getTimeSeriesData(selectDataValue));
    }

    private void useSelectGraphic() {
        setNotVisibleAllGraphics();
        String value = (String) selectGraphic.getSelectedItem();
        if (GraphicsService.GRAPHICS_NAMES[0].equals(value))
            piePanel.setVisible(true);
        else if (GraphicsService.GRAPHICS_NAMES[1].equals(value))
            categoryPanel.setVisible(true);
        else if (GraphicsService.GRAPHICS_NAMES[2].equals(value))
            barPanel.setVisible(true);
    }

    private void setNotVisibleAllGraphics() {
        piePanel.setVisible(false);
        categoryPanel.setVisible(false);
        barPanel.setVisible(false);
    }
}


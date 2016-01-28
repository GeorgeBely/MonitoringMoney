package ru.MonitoringMoney.frame;


import org.apache.commons.lang.StringUtils;
import org.jfree.chart.*;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import ru.MonitoringMoney.ApplicationProperties;
import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.GraphicsService;
import ru.MonitoringMoney.services.ImageService;
import ru.mangeorge.swing.service.PieService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

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

    public GraphicsFrame() {
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
                    if (!texts.contains(payObject.getPayType().getName())) {
                        texts.add(payObject.getPayType().getName());
                    }
                } else if (((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUseUser()) || GraphicsService.VIEW_DATA_NAMES[3].equals(selectData))
                        && payObject.getUser().getName().equals(comparable)) {
                    if (!texts.contains(payObject.getPayType().getName())) {
                        texts.add(payObject.getPayType().getName());
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

                    String selectData = (String) selectViewData.getSelectedItem();
                    if (((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUseImportant()) || GraphicsService.VIEW_DATA_NAMES[2].equals(selectData))) {
                        MonitoringMoney.mainFrame.selectImportanceValue(name);
                        if (!MonitoringMoney.mainFrame.isUseImportant()) {
                            selectViewData.setSelectedItem(GraphicsService.VIEW_DATA_NAMES[1]);
                        } else {
                            selectViewData.setSelectedItem(GraphicsService.VIEW_DATA_NAMES[3]);
                        }
                    } else if (((StringUtils.isBlank(selectData) && !MonitoringMoney.mainFrame.isUseUser()) || GraphicsService.VIEW_DATA_NAMES[3].equals(selectData))) {
                        MonitoringMoney.mainFrame.selectUserValue(name);
                        if (!MonitoringMoney.mainFrame.isUsePayType()) {
                            selectViewData.setSelectedItem(GraphicsService.VIEW_DATA_NAMES[1]);
                        } else {
                            selectViewData.setSelectedItem(GraphicsService.VIEW_DATA_NAMES[2]);
                        }
                    } else {
                        MonitoringMoney.mainFrame.selectPayTypeValue(name);
                        if (!MonitoringMoney.mainFrame.isUseUser()) {
                            selectViewData.setSelectedItem(GraphicsService.VIEW_DATA_NAMES[3]);
                        } else {
                            selectViewData.setSelectedItem(GraphicsService.VIEW_DATA_NAMES[2]);
                        }
                    }
                    MonitoringMoney.mainFrame.refreshText();
                }
            }
            public void chartMouseMoved(ChartMouseEvent chartMouseEvent) { }
        });
        panel.add(piePanel);

        timeSerialChart = ChartFactory.createTimeSeriesChart("График затрат по времени", "Дата покупок", "колличество", GraphicsService.getTimeSeriesData(""));
        timeSerialChart.setBackgroundPaint(this.getBackground());
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
        panel.add(barPanel);


        useSelectGraphic();
    }

    private void resizeFrame() {
        Container frame = piePanel.getParent();
        piePanel.setSize(frame.getWidth() - 15, frame.getHeight() - 55);
        categoryPanel.setSize(frame.getWidth() - 15, frame.getHeight() - 55);
        barPanel.setSize(frame.getWidth() - 15, frame.getHeight() - 55);
    }

    public void updateData() {
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


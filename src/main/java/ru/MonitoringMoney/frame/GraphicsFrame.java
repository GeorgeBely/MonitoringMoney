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
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.RectangleInsets;
import ru.MonitoringMoney.main.ApplicationProperties;
import ru.MonitoringMoney.types.ImportanceType;
import ru.MonitoringMoney.types.PayObject;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.FrameService;
import ru.MonitoringMoney.services.GraphicsService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.types.PayType;
import ru.MonitoringMoney.types.Users;
import ru.mangeorge.swing.service.PieService;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.*;
import java.util.List;

/**
 * Фрейм с графической информацией
 */
public class GraphicsFrame extends JFrame {

    private static final long serialVersionUID = 2295386769890035598L;

    private static final String FRAME_NAME = "Графики затрат";


    /** график "пирожок" */
    private ChartPanel piePanel;
    private JFreeChart pieChart;

    /** график "временнные линии" */
    private ChartPanel categoryPanel;
    private JFreeChart timeSerialChart;

    /** график "Категории" */
    private ChartPanel barPanel;
    private JFreeChart barChart;

    /** график "доходов и расходов" */
    private ChartPanel incomePanel;
    private JFreeChart incomeChart;

    /** общие компоненты*/
    private JComboBox<String> selectGraphic;
    private JComboBox<String> selectViewData;


    GraphicsFrame() {
        toFront();
        setResizable(true);
        setVisible(true);
        setTitle(FRAME_NAME);
        setIconImage(ImageService.GRAPHICS_IMAGE);
        setLocation(ApplicationService.getInstance().getWindowLocation(this));
        setSize(ApplicationService.getInstance().getWindowSize(this));
        addComponentListener(FrameService.addComponentListener(GraphicsFrame.class, getSize(), getLocation(), () -> {}, this::resizeFrame));

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);

        selectGraphic = new JComboBox<String>(GraphicsService.GRAPHICS_NAMES) {{
            setBounds(40, 5, 240, 30);
            addActionListener(e -> selectGraphic());
        }};
        panel.add(selectGraphic);

        selectViewData = new JComboBox<String>() {{
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
            for (PayObject payObject : ApplicationService.viewPayObjects) {
                if (StringUtils.isNotBlank(payObject.getDescription()) && !texts.contains(payObject.getDescription())) {
                    if (payObject.getImportance().getName().equals(comparable)
                        || payObject.getUser().getName().equals(comparable)
                        || payObject.getPayType().getName().equals(comparable)) {
                            texts.add(payObject.getDescription());
                    }
                }
            }
            StringBuilder result = new StringBuilder();
            for (String text : texts) {
                if (result.length() < ApplicationProperties.MAX_INFORM_GRAPHICS_MESSAGE_CHAR) {
                    if (result.length() > 0) {
                        result.append(" ");
                    }
                    if (result.length() + text.length() > ApplicationProperties.MAX_INFORM_GRAPHICS_MESSAGE_CHAR) {
                        if (ApplicationProperties.MAX_INFORM_GRAPHICS_MESSAGE_CHAR - result.length() > 3) {
                            result.append(text.substring(0, ApplicationProperties.MAX_INFORM_GRAPHICS_MESSAGE_CHAR - result.length() - 3));
                        }
                        result.append("...");
                    } else {
                        result.append(text);
                    }
                }
            }
            return result.toString();
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

        timeSerialChart = ChartFactory.createTimeSeriesChart("График затрат по времени", "Дата покупок", "колличество", null);
        timeSerialChart.setBackgroundPaint(this.getBackground());
        XYPlot timeSerialPlot = (XYPlot) timeSerialChart.getPlot();
        timeSerialPlot.setNoDataMessage(GraphicsService.NO_DATA_MESSAGE);
        XYItemRenderer renderer = timeSerialPlot.getRenderer();
        ((XYLineAndShapeRenderer) renderer).setBaseShapesVisible(true);
        renderer.setBaseToolTipGenerator((xyDataset, i, j) -> {
            Date date = new Date();
            date.setTime(xyDataset.getX(i, j).longValue());
            Number value = xyDataset.getY(i, j);
            String categoryName = xyDataset.getSeriesKey(i).toString();

            List<String> texts = new ArrayList<>();
            for (PayObject payObject : ApplicationService.viewPayObjects) {
                if (StringUtils.isNotBlank(payObject.getDescription()) && !texts.contains(payObject.getDescription())) {
                    if (ApplicationProperties.FORMAT_DATE.format(payObject.getDate()).equals(ApplicationProperties.FORMAT_DATE.format(date))) {
                        if (payObject.getImportance().getName().equals(categoryName)
                                || payObject.getUser().getName().equals(categoryName)
                                || payObject.getPayType().getName().equals(categoryName)) {
                            texts.add(payObject.getDescription());
                        }
                    }
                }
            }
            StringBuilder result = new StringBuilder(categoryName + ": (" + ApplicationProperties.FORMAT_DATE.format(date) + ", " + value + ")");
            for (String text : texts) {
                if (result.length() < ApplicationProperties.MAX_INFORM_GRAPHICS_MESSAGE_CHAR) {
                    if (result.length() > 0) {
                        result.append(" ");
                    }
                    if (result.length() + text.length() > ApplicationProperties.MAX_INFORM_GRAPHICS_MESSAGE_CHAR) {
                        if (ApplicationProperties.MAX_INFORM_GRAPHICS_MESSAGE_CHAR - result.length() > 3) {
                            result.append(text.substring(0, ApplicationProperties.MAX_INFORM_GRAPHICS_MESSAGE_CHAR - result.length() - 3));
                        }
                        result.append("...");
                    } else {
                        result.append(text);
                    }
                }
            }
            return result.toString();
        });
        categoryPanel = new ChartPanel(timeSerialChart) {{
            setLocation(5, 50);
            setSize(super.getWidth() - 30, super.getHeight()  - 55);
            setVisible(false);
        }};
        panel.add(categoryPanel);

        barChart = ChartFactory.createBarChart("Суммарные затраты по времени", "Месяц", "колличество", GraphicsService.getBarChartData(""));
        barChart.setBackgroundPaint(this.getBackground());
        barChart.getPlot().setNoDataMessage(GraphicsService.NO_DATA_MESSAGE);
        barPanel = new ChartPanel(barChart) {{
            setLocation(5, 50);
            setSize(super.getWidth() - 30, super.getHeight()  - 55);
            setVisible(false);
        }};
        barPanel.addChartMouseListener(new ChartMouseListener() {
            public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
                if (chartMouseEvent.getEntity() instanceof CategoryItemEntity) {
                    try {
                        CategoryItemEntity entity = (CategoryItemEntity) chartMouseEvent.getEntity();
                        String rowName = entity.getRowKey().toString();
                        Date month = ApplicationProperties.FORMAT_MONTH_AND_YEAR_FOR_PARSE.parse(entity.getColumnKey().toString());
                        Date endMonth = DateUtils.addDays(DateUtils.addMonths(DateUtils.truncate(month, Calendar.MONTH), 1), -1);
                        MonitoringMoney.getFrame(MainFrame.class).setDateFromText(month);
                        MonitoringMoney.getFrame(MainFrame.class).setDateToText(endMonth);
                        selectGraphic.setSelectedItem(GraphicsService.GRAPHICS_NAMES[0]);

                        if (!GraphicsService.ALL_COAST.equals(rowName)) {
                            updatePieData(rowName);
                        }

                        MonitoringMoney.getFrame(MainFrame.class).updateData();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void chartMouseMoved(ChartMouseEvent chartMouseEvent) { }
        });
        panel.add(barPanel);

        incomeChart = ChartFactory.createTimeSeriesChart("График доходов и расходов", "Месяц", "сумма", GraphicsService.getIncomeChartData(""));
        incomeChart.setBackgroundPaint(this.getBackground());
        XYPlot incomePlot = (XYPlot) incomeChart.getPlot();
        incomePlot.setNoDataMessage(GraphicsService.NO_DATA_MESSAGE);
        XYItemRenderer incomeRenderer = incomePlot.getRenderer();
        incomeRenderer.setBaseToolTipGenerator((xyDataset, i, j) -> {
            Date date = new Date();
            date.setTime(xyDataset.getX(i, j).longValue());
            Number value = xyDataset.getY(i, j);
            String categoryName = xyDataset.getSeriesKey(i).toString();

            return categoryName + ": (" + ApplicationProperties.FORMAT_DATE.format(date) + ", " + value + ")";
        });
        incomePanel = new ChartPanel(incomeChart) {{
            setLocation(5, 50);
            setSize(super.getWidth() - 30, super.getHeight()  - 55);
            setVisible(false);
        }};
        panel.add(incomePanel);

        updatePieData("");
        selectGraphic();
    }

    /**
     * Обновляет данные графика "Пирожок"
     *
     * @param name наименование выбранного атрибута.
     */
    private void updatePieData(String name) {
        if (StringUtils.isNotBlank(name)) {
            String selectData = (String) selectViewData.getSelectedItem();
            List graphicValues = ((PiePlot) piePanel.getChart().getPlot()).getDataset().getKeys();

            if (GraphicsService.VIEW_DATA_NAMES[3].equals(selectData)) {
                MonitoringMoney.getFrame(MainFrame.class).selectUserValue(name, graphicValues);
            } else if (GraphicsService.VIEW_DATA_NAMES[2].equals(selectData)) {
                MonitoringMoney.getFrame(MainFrame.class).selectImportanceValue(name, graphicValues);
            } else {
                MonitoringMoney.getFrame(MainFrame.class).selectPayTypeValue(name, graphicValues);
            }
        }
        if (!PieService.ANOTHER_BLOCK_NAME.equals(name)) {
            setSelectViewData();
        }

        MonitoringMoney.getFrame(MainFrame.class).updateData();
    }

    /**
     * Выбырает атрибут, по которому будет отображена информации на графиках.
     */
    private void setSelectViewData() {
        String selectData = (String) selectViewData.getSelectedItem();
        if (MonitoringMoney.getFrame(MainFrame.class).isNotUse(PayType.class) && !GraphicsService.VIEW_DATA_NAMES[1].equals(selectData)) {
            selectViewData.setSelectedItem(GraphicsService.VIEW_DATA_NAMES[1]);
        } else if (MonitoringMoney.getFrame(MainFrame.class).isNotUse(ImportanceType.class) && !GraphicsService.VIEW_DATA_NAMES[2].equals(selectData)) {
            selectViewData.setSelectedItem(GraphicsService.VIEW_DATA_NAMES[2]);
        } else if (MonitoringMoney.getFrame(MainFrame.class).isNotUse(Users.class) && !GraphicsService.VIEW_DATA_NAMES[3].equals(selectData)) {
            selectViewData.setSelectedItem(GraphicsService.VIEW_DATA_NAMES[3]);
        } else {
            selectGraphic.setSelectedItem(GraphicsService.GRAPHICS_NAMES[1]);
        }
    }

    /**
     * При изменении размеров окна сжимаем или разворачиваем графики пропорционально окну.
     */
    private void resizeFrame() {
        Container frame = piePanel.getParent();
        piePanel.setSize(frame.getWidth() - 15, frame.getHeight() - 55);
        categoryPanel.setSize(frame.getWidth() - 15, frame.getHeight() - 55);
        barPanel.setSize(frame.getWidth() - 15, frame.getHeight() - 55);
        incomePanel.setSize(frame.getWidth() - 15, frame.getHeight() - 55);
    }

    /**
     * Обновляет данные на графике
     */
    public void updateData() {
        String selectDataValue = (String) selectViewData.getSelectedItem();
        PieService.updatePieData(pieChart, GraphicsService.getCountMoneyPieData(selectDataValue));
        ((CategoryPlot) barChart.getPlot()).setDataset(GraphicsService.getBarChartData(selectDataValue));
        ((XYPlot) timeSerialChart.getPlot()).setDataset(GraphicsService.getTimeSeriesData(selectDataValue));
        ((XYPlot) incomeChart.getPlot()).setDataset(GraphicsService.getIncomeChartData(selectDataValue));
    }

    /**
     * Выбор отображаемого графика
     */
    @SuppressWarnings("unchecked")
    private void selectGraphic() {
        setNotVisibleAllGraphics();
        String value = (String) selectGraphic.getSelectedItem();
        if (GraphicsService.GRAPHICS_NAMES[0].equals(value))
            piePanel.setVisible(true);
        else if (GraphicsService.GRAPHICS_NAMES[1].equals(value))
            categoryPanel.setVisible(true);
        else if (GraphicsService.GRAPHICS_NAMES[2].equals(value))
            barPanel.setVisible(true);
        else if (GraphicsService.GRAPHICS_NAMES[3].equals(value))
            incomePanel.setVisible(true);

        DefaultComboBoxModel<String> selectViewDataModel = (DefaultComboBoxModel<String>) selectViewData.getModel();
        selectViewDataModel.removeAllElements();
        if (GraphicsService.GRAPHICS_NAMES[3].equals(value)) {
            selectViewDataModel.addElement(GraphicsService.VIEW_DATA_NAMES[0]);
            selectViewDataModel.addElement(GraphicsService.VIEW_DATA_NAMES[3]);
            selectViewDataModel.addElement(GraphicsService.VIEW_DATA_NAMES[4]);
        } else {
            selectViewDataModel.addElement(GraphicsService.VIEW_DATA_NAMES[0]);
            selectViewDataModel.addElement(GraphicsService.VIEW_DATA_NAMES[1]);
            selectViewDataModel.addElement(GraphicsService.VIEW_DATA_NAMES[2]);
            selectViewDataModel.addElement(GraphicsService.VIEW_DATA_NAMES[3]);
        }
    }

    /**
     * Скрывает все графики
     */
    private void setNotVisibleAllGraphics() {
        piePanel.setVisible(false);
        categoryPanel.setVisible(false);
        barPanel.setVisible(false);
        incomePanel.setVisible(false);
    }
}


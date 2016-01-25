package ru.MonitoringMoney.frame;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.GraphicsService;
import ru.MonitoringMoney.services.ImageService;
import ru.mangeorge.swing.service.PieService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Фрейм с графической информацией
 */
public class GraphicsFrame extends JFrame {

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

        piePanel = new ChartPanel(pieChart) {{
            setLocation(5, 50);
            setSize(super.getWidth() - 30, super.getHeight()  - 55);
            setVisible(false);
        }};
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


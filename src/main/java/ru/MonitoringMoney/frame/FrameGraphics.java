package ru.MonitoringMoney.frame;


import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import ru.MonitoringMoney.services.GraphicsService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class FrameGraphics extends JFrame {

    /** Ширина фрейма */
    private static final int FRAME_WIDTH = 515;

    /** Высота фрейма */
    private static final int FRAME_HEIGHT = 635;

    private static final String FRAME_NAME = "Графики затрат";


    private ChartPanel piePanel;
    private ChartPanel categoryPanel;
    private JFreeChart pieChart;
    private JFreeChart timeSerialChart;

    public FrameGraphics() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - FRAME_WIDTH / 2, screenSize.height / 2 - FRAME_HEIGHT / 2);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setVisible(true);
        setTitle(FRAME_NAME);
        toFront();

        addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
                resizeFrame();
            }
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {}
            public void componentHidden(ComponentEvent e) {}
        });


        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);

        pieChart = GraphicsService.getPieComponent("Процентное соотношение покупок", this.getBackground());
        piePanel = new ChartPanel(pieChart) {{
            setLocation(5, 5);
            setSize(FRAME_WIDTH - 30, (FRAME_HEIGHT - 35)/2);
        }};
        GraphicsService.updatePieData(pieChart);
        panel.add(piePanel);

        timeSerialChart = (GraphicsService.getTimeSeriesComponent("График затрат по времени",
                "Дата покупок", "колличество", this.getBackground()));
        categoryPanel = new ChartPanel(timeSerialChart) {{
            setLocation(5, (FRAME_HEIGHT - 35)/2 + 10);
            setSize(FRAME_WIDTH - 30, (FRAME_HEIGHT - 35)/2);
        }};
        GraphicsService.updateTimeSeriesData(timeSerialChart);
        panel.add(categoryPanel);

    }

    private void resizeFrame() {
        Container frame = piePanel.getParent();
        piePanel.setSize(frame.getWidth() - 15, (frame.getHeight() - 35)/2);
        categoryPanel.setSize(frame.getWidth() - 15, (frame.getHeight() - 35) / 2);
        categoryPanel.setLocation(5, piePanel.getY() + piePanel.getHeight() + 15);
    }

    public void update() {
        GraphicsService.updatePieData(pieChart);
        GraphicsService.updateTimeSeriesData(timeSerialChart);
    }
}


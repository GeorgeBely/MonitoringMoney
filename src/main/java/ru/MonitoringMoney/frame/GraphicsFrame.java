package ru.MonitoringMoney.frame;


import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import ru.MonitoringMoney.services.GraphicsService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class GraphicsFrame extends JFrame {

    /** Ширина фрейма */
    private static final int FRAME_WIDTH = 515;

    /** Высота фрейма */
    private static final int FRAME_HEIGHT = 335;

    private static final String FRAME_NAME = "Графики затрат";


    private ChartPanel piePanel;
    private ChartPanel categoryPanel;
    private JFreeChart pieChart;
    private JFreeChart timeSerialChart;
    private JComboBox selectGraphic;
    private JComboBox selectViewData;

    public GraphicsFrame() {
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

        pieChart = GraphicsService.getPieComponent("Процентное соотношение покупок", this.getBackground());
        piePanel = new ChartPanel(pieChart) {{
            setLocation(5, 50);
            setSize(FRAME_WIDTH - 30, FRAME_HEIGHT - 55);
            setVisible(false);
        }};
        GraphicsService.updatePieData(pieChart, "");
        panel.add(piePanel);

        timeSerialChart = (GraphicsService.getTimeSeriesComponent("График затрат по времени",
                "Дата покупок", "колличество", this.getBackground()));
        categoryPanel = new ChartPanel(timeSerialChart) {{
            setLocation(5, 50);
            setSize(FRAME_WIDTH - 30, FRAME_HEIGHT - 55);
            setVisible(false);
        }};
        GraphicsService.updateTimeSeriesData(timeSerialChart, "");
        panel.add(categoryPanel);


        useSelectGraphic();
    }

    private void resizeFrame() {
        Container frame = piePanel.getParent();
        piePanel.setSize(frame.getWidth() - 15, frame.getHeight() - 55);
        categoryPanel.setSize(frame.getWidth() - 15, frame.getHeight() - 55);
    }

    public void updateData() {
        String selectDataValue = (String) selectViewData.getSelectedItem();
        GraphicsService.updatePieData(pieChart, selectDataValue);
        GraphicsService.updateTimeSeriesData(timeSerialChart, selectDataValue);
    }

    private void useSelectGraphic() {
        setNotVisibleAllGraphics();
        String value = (String) selectGraphic.getSelectedItem();
        if (GraphicsService.GRAPHICS_NAMES[0].equals(value))
            piePanel.setVisible(true);
        else if (GraphicsService.GRAPHICS_NAMES[1].equals(value))
            categoryPanel.setVisible(true);
    }

    private void setNotVisibleAllGraphics() {
        piePanel.setVisible(false);
        categoryPanel.setVisible(false);
    }
}


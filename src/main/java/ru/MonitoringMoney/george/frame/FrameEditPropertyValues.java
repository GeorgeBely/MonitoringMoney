package main.java.ru.MonitoringMoney.george.frame;


import main.java.ru.MonitoringMoney.george.helpers.ApplicationHelper;

import javax.swing.*;
import java.awt.*;

public class FrameEditPropertyValues extends JFrame {

    /** Ширина фрейма */
    private static final int FRAME_WIDTH = 250;

    /** Высота фрейма */
    private static final int FRAME_HEIGHT = 100;

    public FrameEditPropertyValues(Class className) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - FRAME_WIDTH / 2, screenSize.height / 2 - FRAME_HEIGHT / 2);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        setVisible(true);

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);


        JTextField valueNameText = new JTextField() {{
            setBounds(5, 5, 240, 30);
        }};
        panel.add(valueNameText);

        JButton cancelButton = new JButton("Добавить") {{
            setBounds(5, 40, 240, 30);
            addActionListener(e -> {
                ApplicationHelper.getInstance().addPropertyValue(valueNameText.getText(), className);
                dispose();
            });
        }};
        panel.add(cancelButton);
    }
}

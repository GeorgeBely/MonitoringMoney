package main.java.ru.MonitoringMoney.george.frame;


import main.java.ru.MonitoringMoney.george.helpers.ApplicationHelper;
import main.java.ru.MonitoringMoney.george.types.ImportanceType;
import main.java.ru.MonitoringMoney.george.types.PayType;
import main.java.ru.MonitoringMoney.george.types.Users;

import javax.swing.*;
import java.awt.*;

public class FrameEditPropertyValues extends JFrame {

    /** Ширина фрейма */
    private static final int FRAME_WIDTH = 250;

    /** Высота фрейма */
    private static final int FRAME_HEIGHT = 130;

    private static final String FRAME_NAME = "Добавление нового значения";


    public FrameEditPropertyValues(Class className) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - FRAME_WIDTH / 2, screenSize.height / 2 - FRAME_HEIGHT / 2);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        setVisible(true);
        setTitle(FRAME_NAME);

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);


        JLabel newValueLabel = new JLabel() {{
            if (PayType.class.equals(className))
                setText("Добавить тип покупки");
            else if (ImportanceType.class.equals(className))
                setText("Добавить уровень важности");
            else if (Users.class.equals(className))
                setText("Добавить пользователя");
            setBounds(5, 0, 235, 30);
        }};
        panel.add(newValueLabel);

        JTextField valueNameText = new JTextField() {{
            setBounds(5, 30, 235, 30);
        }};
        panel.add(valueNameText);

        JButton cancelButton = new JButton("Добавить") {{
            setBounds(5, 65, 235, 30);
            addActionListener(e -> {
                ApplicationHelper.getInstance().addPropertyValue(valueNameText.getText(), className);
                dispose();
            });
        }};
        panel.add(cancelButton);
    }
}

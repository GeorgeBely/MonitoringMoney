package ru.MonitoringMoney.frame;


import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.types.ImportanceType;
import ru.MonitoringMoney.types.IncomeType;
import ru.MonitoringMoney.types.PayType;
import ru.MonitoringMoney.types.Users;

import javax.swing.*;
import java.io.Serializable;

/**
 * Фрейм для добавления нового атрибута
 */
public class FrameAddPropertyValues extends JFrame implements Serializable {

    private static final long serialVersionUID = -3452342341234123428L;


    private static final String FRAME_NAME = "Добавление нового значения";


    FrameAddPropertyValues(Class className) {
        setResizable(false);
        setVisible(true);
        setTitle(FRAME_NAME);
        setIconImage(ImageService.PLUS_IMAGE);
        setLocation(ApplicationService.getInstance().getWindowLocation(this));
        setSize(ApplicationService.getInstance().getWindowSize(this));

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
            else if (IncomeType.class.equals(className))
                setText("Добавить тип дохода");
            setBounds(5, 0, 235, 30);
        }};
        panel.add(newValueLabel);

        JTextField valueNameText = new JTextField() {{
            setBounds(5, 30, 235, 30);
        }};
        panel.add(valueNameText);

        JButton addButton = new JButton("Добавить") {{
            setBounds(5, 65, 235, 30);
            addActionListener(e -> {
                Object newValue = ApplicationService.getInstance().addPropertyValue(valueNameText.getText(), className);
                if (newValue != null) {
                    MonitoringMoney.getFrame(AddFrame.class).addSelectElement(newValue);
                    MonitoringMoney.getFrame(MainFrame.class).addSelectElement(newValue);
                    MonitoringMoney.getFrame(AddIncomeFrame.class).addSelectElement(newValue);
                }
                dispose();
            });
        }};
        panel.add(addButton);
    }
}

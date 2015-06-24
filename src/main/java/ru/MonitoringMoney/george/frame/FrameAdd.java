package main.java.ru.MonitoringMoney.george.frame;


import main.java.ru.MonitoringMoney.george.*;
import main.java.ru.MonitoringMoney.george.helpers.ApplicationHelper;
import main.java.ru.MonitoringMoney.george.types.*;
import org.apache.commons.lang.time.DateUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class FrameAdd extends JFrame{

    /** Ширина фрейма */
    private static final int FRAME_WIDTH = 250;

    /** Высота фрейма */
    private static final int FRAME_HEIGHT = 320;


    public FrameAdd() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - FRAME_WIDTH / 2, screenSize.height / 2 - FRAME_HEIGHT / 2);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);

        JComboBox importanceSelect = new JComboBox<Object>(ApplicationHelper.getInstance().importanceTypes.toArray()) {{
            setBounds(5, 5, 200, 30);
        }};
        panel.add(importanceSelect);

        JButton importanceButton = new JButton(".") {{
            setBounds(210, 5, 30, 30);
            addActionListener(e -> new FrameEditPropertyValues(ImportanceType.class));
        }};
        panel.add(importanceButton);

        JComboBox payTypeSelect = new JComboBox<Object>(ApplicationHelper.getInstance().payTypes.toArray()) {{
            setBounds(5, 40, 200, 30);
        }};
        panel.add(payTypeSelect);

        JButton payTypeButton = new JButton(".") {{
            setBounds(210, 40, 30, 30);
            addActionListener(e -> new FrameEditPropertyValues(PayType.class));
        }};
        panel.add(payTypeButton);

        JLabel priceLabel = new JLabel("Стоимость покупки") {{
            setBounds(5, 75, 140, 20);
        }};
        panel.add(priceLabel);

        JTextField price = new JTextField() {{
            setBounds(145, 75, 90, 20);
        }};
        panel.add(price);

        JLabel labelFromDate = new JLabel("Дата покупки") {{
            setBounds(5, 100, 140, 20);
        }};
        panel.add(labelFromDate);

//        FORMAT_DATE.setLenient(false);
        JFormattedTextField date = new JFormattedTextField(ApplicationHelper.FORMAT_DATE) {{
            setBounds(145, 100, 90, 20);
            setValue(new Date());
        }};
        panel.add(date);

        JTextArea textDescription = new JTextArea() {{
            setLineWrap(true);
            setWrapStyleWord(true);
        }};

        JScrollPane textScrollPane = new JScrollPane() {{
            setViewportView(textDescription);
            setBounds(5, 130, 240, 60);
        }};
        panel.add(textScrollPane);

//        JLabel purchasedLabel = new JLabel("Покупка осуществленна") {{
//            setBounds(5, 195, 180, 20);
//        }};
//        panel.add(purchasedLabel);

//        JCheckBox purchased = new JCheckBox() {{
//            setBounds(200, 195, 20, 20);
//        }};
//        panel.add(purchased);

        JComboBox userSelect = new JComboBox<Object>(ApplicationHelper.getInstance().users.toArray()) {{
            setBounds(5, 220, 200, 30);
            setSelectedItem(UsersDefault.GEORGE);
        }};
        panel.add(userSelect);

        JButton userButton = new JButton(".") {{
            setBounds(210, 220, 30, 30);
            addActionListener(e -> new FrameEditPropertyValues(Users.class));
        }};
        panel.add(userButton);

        JButton okButton = new JButton("Добавить") {{
            setBounds(5, 255, 115, 30);
            addActionListener(e -> {
                PayObject pay = new PayObject();
                pay.setDate(DateUtils.truncate((Date) date.getValue(), Calendar.DATE));
                pay.setDescription(textDescription.getText());
                pay.setImportance((ImportanceType) importanceSelect.getSelectedItem());
                pay.setPayType((PayType) payTypeSelect.getSelectedItem());
                pay.setPrice(Integer.parseInt(price.getText().replaceAll("[^0-9]]", "")));
//                pay.setPurchased(purchased.isSelected());
                pay.setPurchased(true);
                pay.setUser((Users) userSelect.getSelectedItem());
                ApplicationHelper.getInstance().payObjects.add(pay);

                try {
                    ApplicationHelper.writeData();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                MainFrameThread.frame.refreshText();
                dispose();
            });
        }};
        panel.add(okButton);

        JButton cancelButton = new JButton("Отмена") {{
            setBounds(125, 255, 115, 30);
            addActionListener(e -> dispose());
        }};
        panel.add(cancelButton);
    }
}

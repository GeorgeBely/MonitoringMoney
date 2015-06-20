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
    private static final int WIDTH = 250;
    private static final int HEIGHT = 320;

    public FrameAdd() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - WIDTH / 2, screenSize.height / 2 - HEIGHT / 2);
        setSize(WIDTH, HEIGHT);
        setResizable(false);

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);

        final JComboBox importanceSelect = new JComboBox(ApplicationHelper.getInstance().importanceTypes.toArray());
        importanceSelect.setBounds(5, 5, 200, 30);
        panel.add(importanceSelect);

        JButton importanceButton = new JButton(".");
        importanceButton.setBounds(210, 5, 30, 30);
        panel.add(importanceButton);

        final JComboBox payTypeSelect = new JComboBox(ApplicationHelper.getInstance().payTypes.toArray());
        payTypeSelect.setBounds(5, 40, 200, 30);
        panel.add(payTypeSelect);

        JButton payTypeButton = new JButton(".");
        payTypeButton.setBounds(210, 40, 30, 30);
        panel.add(payTypeButton);

        JLabel priceLabel = new JLabel("Стоимость покупки");
        priceLabel.setBounds(5, 75, 140, 20);
        panel.add(priceLabel);

        final JTextField price = new JTextField();
        price.setBounds(145, 75, 90, 20);
        panel.add(price);

        JLabel labelFromDate = new JLabel("Дата покупки");
        labelFromDate.setBounds(5, 100, 140, 20);
        panel.add(labelFromDate);

//        FORMAT_DATE.setLenient(false);
        final JFormattedTextField date = new JFormattedTextField(ApplicationHelper.FORMAT_DATE) {{
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

        JLabel purchasedLabel = new JLabel("Покупка осуществленна");
        purchasedLabel.setBounds(5, 195, 180, 20);
        panel.add(purchasedLabel);

        final JCheckBox purchased = new JCheckBox();
        purchased.setBounds(200, 195, 20, 20);
        panel.add(purchased);

        final JComboBox userSelect = new JComboBox(ApplicationHelper.getInstance().users.toArray());
        userSelect.setBounds(5, 220, 200, 30);
        userSelect.setSelectedItem(UsersDefault.GEORGE);
        panel.add(userSelect);

        JButton okButton = new JButton("Добавить");
        okButton.setBounds(5, 255, 115, 30);
        panel.add(okButton);

        JButton cancelButton = new JButton("Отмена");
        cancelButton.setBounds(125, 255, 115, 30);
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);


        okButton.addActionListener(e -> {
            PayObject pay = new PayObject();
            pay.setDate(DateUtils.truncate((Date) date.getValue(), Calendar.DATE));
            pay.setDescription(textDescription.getText());
            pay.setImportance((ImportanceType) importanceSelect.getSelectedItem());
            pay.setPayType((PayType) payTypeSelect.getSelectedItem());
            pay.setPrice(Integer.parseInt(price.getText().replaceAll("[^0-9]]", "")));
            pay.setPurchased(purchased.isSelected());
            pay.setUser((Users) userSelect.getSelectedItem());
            ApplicationHelper.getInstance().payObjects.add(pay);

            try {
                ApplicationHelper.writeData();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }
}

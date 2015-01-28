package main.java.ru.MonitoringMoney.george;


import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

public class FrameAdd extends JFrame{
    private static final int WIDTH = 250;
    private static final int HEIGHT = 320;

    public FrameAdd() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        setLocation(screenSize.width / 2 - WIDTH / 2, screenSize.height / 2 - HEIGHT / 2);
        setSize(WIDTH, HEIGHT);
        setResizable(false);

        JPanel panel = new JPanel(){{
            setFocusable(true);
            setLayout(null);
            setBackground(Color.GREEN);
        }};
        add(panel);

        final JComboBox importanceSelect = new JComboBox(Importance.values());
        importanceSelect.setBounds(5, 5, 200, 30);
        panel.add(importanceSelect);

        JButton importanceButton = new JButton(".");
        importanceButton.setBounds(210, 5, 30, 30);
        panel.add(importanceButton);

        final JComboBox payTypeSelect = new JComboBox(PayType.values());
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

//        formatDate.setLenient(false);
        final JFormattedTextField date = new JFormattedTextField(MainFrame.formatDate) {{
            setBounds(145, 100, 90, 20);
            setValue(new Date());
        }};
        panel.add(date);

        final JTextArea description = new JTextArea();
        description.setBounds(5, 130, 240, 60);
        panel.add(description);

        JLabel purchasedLabel = new JLabel("Покупка осуществленна");
        purchasedLabel.setBounds(5, 195, 180, 20);
        panel.add(purchasedLabel);

        final JCheckBox purchased = new JCheckBox();
        purchased.setBounds(200, 195, 20, 20);
        panel.add(purchased);

        final JComboBox userSelect = new JComboBox(User.values());
        userSelect.setBounds(5, 220, 200, 30);
        userSelect.setSelectedItem(User.GEORGE);
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
            pay.date = (Date) date.getValue();
            pay.description = description.getText();
            pay.importance = (Importance) importanceSelect.getSelectedItem();
            pay.type = (PayType) userSelect.getSelectedItem();
            pay.price = Integer.parseInt(price.getText().replaceAll("[^0-9]]", ""));
            pay.purchased = purchased.isBorderPaintedFlat();
            pay.user = (User) userSelect.getSelectedItem();
            MonitoringMoney.payObjects.add(pay);

            try {
                ObjectOutputStream bin = new ObjectOutputStream(new FileOutputStream(MonitoringMoney.buyFile));
                bin.writeObject(MonitoringMoney.payObjects);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }
}

package main.java.ru.MonitoringMoney.george;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

class MainFrame extends JFrame implements Serializable {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 225;
    private static final String TERM_INPUT_DEFAULT_TEXT = "Поиск по подстроке";
    public static final DateFormat formatDate = DateFormat.getDateInstance(DateFormat.SHORT);
    JComboBox importanceSelect;
    public MainFrame() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        setLocation(screenSize.width / 2 - WIDTH / 2, screenSize.height / 2 - HEIGHT / 2);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
//        setIconImage(kit.getImage("C :\\Workspace\\MassageAgent\\src\\ru\\MassageAgent.png"));

        JPanel panel = new JPanel(){{
            setFocusable(true);
            setLayout(null);
            setBackground(Color.GREEN);
        }};
        add(panel);

        JTextArea text = new JTextArea();
        for (PayObject pay : MonitoringMoney.payObjects) {
            text.append(pay.toString());
            text.append("\n");
        }
        text.setBounds(250, 5, 245, 190);
        text.setLineWrap(true);
        panel.add(text);

        JTextField termInput = new JTextField();
        termInput.setBounds(5, 5, 240, 30);
        termInput.setText(TERM_INPUT_DEFAULT_TEXT);
        termInput.setDisabledTextColor(Color.LIGHT_GRAY);
        termInput.setSelectedTextColor(Color.LIGHT_GRAY);
        termInput.setSelectionColor(Color.LIGHT_GRAY);
//        termInput.setFont(new Font(TERM_INPUT_DEFAULT_TEXT,Font.ITALIC,12));
        panel.add(termInput);

        importanceSelect = new JComboBox(Importance.values());
        importanceSelect.setBounds(5, 40, 240, 30);
        panel.add(importanceSelect);

        JComboBox payTypeSelect = new JComboBox(PayType.values());
        payTypeSelect.setBounds(5, 75, 240, 30);
        panel.add(payTypeSelect);

        JLabel priceFromLabel = new JLabel("Цена от");
        priceFromLabel.setBounds(5, 110, 60, 20);
        panel.add(priceFromLabel);

        JTextField priceFrom = new JTextField();
        priceFrom.setBounds(65, 110, 75, 20);
        panel.add(priceFrom);

        JLabel priceToLabel = new JLabel("до");
        priceToLabel.setBounds(145, 110, 20, 20);
        panel.add(priceToLabel);

        JTextField priceTo = new JTextField();
        priceTo.setBounds(170, 110, 75, 20);
        panel.add(priceTo);

        JLabel labelFromDate = new JLabel("В период c");
        labelFromDate.setBounds(5, 135, 80, 20);
        panel.add(labelFromDate);

//        formatDate.setLenient(false);

        JFormattedTextField dateFrom = new JFormattedTextField(formatDate) {{
            setBounds(85, 135, 65, 20);
            setValue(new Date());
        }};
        panel.add(dateFrom);

        JLabel labelDateTo = new JLabel("по");
        labelDateTo.setBounds(155, 135, 20, 20);
        panel.add(labelDateTo);

        JFormattedTextField dateTo = new JFormattedTextField(formatDate) {{
            setBounds(180, 135, 65, 20);
            setValue(new Date());
        }};
        panel.add(dateTo);

        JButton buttonAdd = new JButton("Добавить покупку");
        buttonAdd.setBounds(5, 160, 240, 30);
        panel.add(buttonAdd);

        buttonAdd.addActionListener(e -> EventQueue.invokeLater(() -> {
            FrameAdd frame = new FrameAdd();
            frame.toFront();
            frame.setVisible(true);
        }));

    }
}
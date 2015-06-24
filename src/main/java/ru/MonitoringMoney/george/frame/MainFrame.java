package main.java.ru.MonitoringMoney.george.frame;

import main.java.ru.MonitoringMoney.george.helpers.ApplicationHelper;
import main.java.ru.MonitoringMoney.george.types.ImportanceType;
import main.java.ru.MonitoringMoney.george.types.PayType;
import main.java.ru.MonitoringMoney.george.types.Users;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

class MainFrame extends JFrame implements Serializable {

    /** Ширина фрейма */
    private static final int FRAME_WIDTH = 500;

    /** Высота фрейма */
    private static final int FRAME_HEIGHT = 260;

    /** Фраза, которая отображается в строчке поиска по подстроке, до начала ввода поискового выражения */
    private static final String TERM_INPUT_DEFAULT_TEXT = "Поиск по подстроке";

    /** Фраза, которая отображается до суммы всех покупок отображаемым по заданным фильтрам */
    private static final String PREFIX_LABEL_SUM_PRICE = "Потрачено на сумму: ";


    private JTextArea text;
    private JTextField termInput;
    private JComboBox importanceSelect;
    private JComboBox payTypeSelect;
    private JTextField priceFromText;
    private JTextField priceToText;
    private JFormattedTextField dateFromText;
    private JFormattedTextField dateToText;
    private JComboBox userSelect;
    private JLabel labelSumPrice;


    public MainFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - FRAME_WIDTH / 2, screenSize.height / 2 - FRAME_HEIGHT / 2);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        JPanel panel = new JPanel(){{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);

        text = new JTextArea() {{
            setLineWrap(true);
            setWrapStyleWord(true);
        }};
        JScrollPane textScrollPane = new JScrollPane() {{
            setViewportView(text);
            setBounds(250, 5, 245, 190);
        }};
        panel.add(textScrollPane);

        termInput = new JTextField() {{
            setBounds(5, 5, 240, 30);
            setText(TERM_INPUT_DEFAULT_TEXT);
            setDisabledTextColor(Color.LIGHT_GRAY);
            setSelectedTextColor(Color.LIGHT_GRAY);
            setSelectionColor(Color.LIGHT_GRAY);
            addMouseListener(new MouseListener() {
                public void mouseReleased(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseClicked(MouseEvent e) {}
                public void mousePressed(MouseEvent e) {
                    if (TERM_INPUT_DEFAULT_TEXT.equals(getText())) {
                        setText("");
                    }
                }
            });
            addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {}
                public void keyReleased(KeyEvent e) {
                    refreshText();
                }
                public void keyTyped(KeyEvent e) {}
            });
        }};
        panel.add(termInput);

        importanceSelect = new JComboBox<Object>(ApplicationHelper.getInstance().importanceTypes.toArray()) {{
            setBounds(5, 40, 240, 30);
            addActionListener(e -> refreshText());
        }};
        panel.add(importanceSelect);

        payTypeSelect = new JComboBox<Object>(ApplicationHelper.getInstance().payTypes.toArray()) {{
            setBounds(5, 75, 240, 30);
            addActionListener(e -> refreshText());
        }};
        panel.add(payTypeSelect);

        JLabel priceFromLabel = new JLabel("Цена от") {{
            setBounds(5, 110, 60, 20);
        }};
        panel.add(priceFromLabel);

        priceFromText = new JTextField() {{
            setBounds(65, 110, 75, 20);
            addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {}
                public void keyReleased(KeyEvent e) {
                    refreshText();
                }
                public void keyTyped(KeyEvent e) {}
            });
        }};
        panel.add(priceFromText);

        JLabel priceToLabel = new JLabel("до") {{
            setBounds(145, 110, 20, 20);
        }};
        panel.add(priceToLabel);

        priceToText = new JTextField() {{
            setBounds(170, 110, 75, 20);

            addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {}
                public void keyReleased(KeyEvent e) {
                    refreshText();
                }
                public void keyTyped(KeyEvent e) {}
            });
        }};
        panel.add(priceToText);

        JLabel labelFromDate = new JLabel("В период c") {{
            setBounds(5, 135, 80, 20);
        }};
        panel.add(labelFromDate);

        dateFromText = new JFormattedTextField(ApplicationHelper.FORMAT_DATE) {{
            setBounds(85, 135, 65, 20);
            setValue(new Date());
            addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {}
                public void keyReleased(KeyEvent e) {
                    refreshText();
                }
                public void keyTyped(KeyEvent e) {}
            });
        }};
        panel.add(dateFromText);

        JLabel labelDateTo = new JLabel("по") {{
            setBounds(155, 135, 20, 20);
        }};
        panel.add(labelDateTo);

        dateToText = new JFormattedTextField(ApplicationHelper.FORMAT_DATE) {{
            setBounds(180, 135, 65, 20);
            setValue(new Date());
            addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {}
                public void keyReleased(KeyEvent e) {
                    refreshText();
                }
                public void keyTyped(KeyEvent e) {}
            });
        }};
        panel.add(dateToText);

        userSelect = new JComboBox<Object>(ApplicationHelper.getInstance().users.toArray()) {{
            setBounds(5, 160, 240, 30);
            addActionListener(e -> refreshText());
        }};
        panel.add(userSelect);

        labelSumPrice = new JLabel() {{
            setBounds(250, 195, 240, 20);
        }};
        panel.add(labelSumPrice);

        JButton buttonAdd = new JButton("Добавить покупку") {{
            setBounds(5, 195, 240, 30);
            addActionListener(e -> EventQueue.invokeLater(() -> {
                FrameAdd frame = new FrameAdd();
                frame.toFront();
                frame.setVisible(true);
            }));
        }};
        panel.add(buttonAdd);

        refreshText();
    }


    public void refreshText() {
        Integer priceFrom = null;
        Integer priceTo = null;
        Date dateFrom;
        Date dateTo;
        String term = null;
        if (!TERM_INPUT_DEFAULT_TEXT.equals(termInput.getText()) && StringUtils.isNotBlank(termInput.getText()))
            term = termInput.getText();
        dateFrom = DateUtils.truncate((Date) dateFromText.getValue(), Calendar.DATE);
        dateTo = DateUtils.truncate((Date) dateToText.getValue(), Calendar.DATE);
        dateTo.setDate(dateTo.getDate() + 1);
        try {
            if (StringUtils.isNotBlank(priceFromText.getText()))
                priceFrom = Integer.parseInt(priceFromText.getText());
            if (StringUtils.isNotBlank(priceToText.getText()))
                priceTo = Integer.parseInt(priceToText.getText());
        } catch (Exception ignore) {}
        text.setText(ApplicationHelper.getInstance().getTextPayObjects(term, dateFrom, dateTo, priceFrom,
                priceTo, (ImportanceType) importanceSelect.getSelectedItem(), (PayType) payTypeSelect.getSelectedItem(),
                (Users) userSelect.getSelectedItem(), true));

        labelSumPrice.setText(PREFIX_LABEL_SUM_PRICE + " "  + ApplicationHelper.getInstance().getSumPrice(term, dateFrom, dateTo, priceFrom,
                priceTo, (ImportanceType) importanceSelect.getSelectedItem(), (PayType) payTypeSelect.getSelectedItem(),
                (Users) userSelect.getSelectedItem(), true));
    }

}
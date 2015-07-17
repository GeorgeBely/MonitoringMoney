package ru.MonitoringMoney.frame;

import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.types.ImportanceType;
import ru.MonitoringMoney.types.PayType;
import ru.MonitoringMoney.types.Users;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainFrame extends JFrame {

    /** Ширина фрейма */
    private static final int FRAME_WIDTH = 500;

    /** Высота фрейма */
    private static final int FRAME_HEIGHT = 260;

    /** Фраза, которая отображается в строчке поиска по подстроке, до начала ввода поискового выражения */
    private static final String TERM_INPUT_DEFAULT_TEXT = "Поиск по подстроке";

    /** Фраза, которая отображается до суммы всех покупок отображаемым по заданным фильтрам */
    private static final String PREFIX_LABEL_SUM_PRICE = "Потрачено на сумму: ";

    private static final String FRAME_NAME = "MonitoringMoney";


    private JTextArea text;
    public JTextField termInput;
    public JComboBox importanceSelect;
    public JComboBox payTypeSelect;
    public JTextField priceFromText;
    public JTextField priceToText;
    public JFormattedTextField dateFromText;
    public JFormattedTextField dateToText;
    public JComboBox userSelect;
    private JLabel labelSumPrice;
    private FrameGraphics frameGraphics;


    public MainFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - FRAME_WIDTH / 2, screenSize.height / 2 - FRAME_HEIGHT / 2);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setTitle(FRAME_NAME + "_" + MonitoringMoney.VERSION);

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
            setBounds(250, 5, 240, 186);
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

        importanceSelect = new JComboBox<Object>(ApplicationService.getInstance().importanceTypes.toArray()) {{
            setBounds(5, 40, 240, 30);
            addActionListener(e -> refreshText());
        }};
        panel.add(importanceSelect);

        payTypeSelect = new JComboBox<Object>(ApplicationService.getInstance().payTypes.toArray()) {{
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

        dateFromText = new JFormattedTextField(ApplicationService.FORMAT_DATE) {{
            setBounds(85, 135, 65, 20);
            setValue(new Date());
            addMouseListener(new MouseListener() {
                public void mouseReleased(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseClicked(MouseEvent e) {}
                public void mousePressed(MouseEvent e) {
                    new FrameCalendar(dateFromText);
                }
            });
        }};
        panel.add(dateFromText);

        JLabel labelDateTo = new JLabel("по") {{
            setBounds(155, 135, 20, 20);
        }};
        panel.add(labelDateTo);

        dateToText = new JFormattedTextField(ApplicationService.FORMAT_DATE) {{
            setBounds(180, 135, 65, 20);
            setValue(new Date());
            addMouseListener(new MouseListener() {
                public void mouseReleased(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseClicked(MouseEvent e) {}
                public void mousePressed(MouseEvent e) {
                    new FrameCalendar(dateToText);
                }
            });
        }};
        panel.add(dateToText);

        userSelect = new JComboBox<Object>(ApplicationService.getInstance().users.toArray()) {{
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
            addActionListener(e -> MonitoringMoney.frameAdd.showFrame());
        }};
        panel.add(buttonAdd);

        JButton buttonGraphics = new JButton() {{
            setBounds(460, 195, 30, 30);
            addActionListener(e -> EventQueue.invokeLater(() -> frameGraphics = new FrameGraphics()));
            setIcon(ImageService.getGraphicsButtonIcon());
        }};
        panel.add(buttonGraphics);

        refreshText();
    }


    public void refreshText() {
        text.setText(ApplicationService.getInstance().getTextPayObjects(getPayObjectWithCurrentFilters()));
        labelSumPrice.setText(PREFIX_LABEL_SUM_PRICE + " "  + ApplicationService.getInstance().getSumPrice(getPayObjectWithCurrentFilters()));
        if (frameGraphics != null)
            frameGraphics.update();
    }

    public List<PayObject> getPayObjectWithCurrentFilters() {
        Integer priceFrom = null;
        Integer priceTo = null;
        Date dateFrom;
        Date dateTo;
        String term = null;
        if (!TERM_INPUT_DEFAULT_TEXT.equals(termInput.getText()) && StringUtils.isNotBlank(termInput.getText()))
            term = termInput.getText();
        dateFrom = DateUtils.truncate((Date) dateFromText.getValue(), Calendar.DATE);
        Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTime(DateUtils.truncate((Date) dateToText.getValue(), Calendar.DATE));
        calendarTo.add(Calendar.DATE, 1);
        dateTo = calendarTo.getTime();
        try {
            if (StringUtils.isNotBlank(priceFromText.getText()))
                priceFrom = Integer.parseInt(priceFromText.getText());
            if (StringUtils.isNotBlank(priceToText.getText()))
                priceTo = Integer.parseInt(priceToText.getText());
        } catch (Exception ignore) {}

        return ApplicationService.getInstance().getPayObjectsWithFilters(term, dateFrom, dateTo, priceFrom,
                priceTo, (ImportanceType) importanceSelect.getSelectedItem(), (PayType) payTypeSelect.getSelectedItem(),
                (Users) userSelect.getSelectedItem());
    }

    public boolean isUsePayType() {
        return !ApplicationService.EMPTY.equals(((PayType) payTypeSelect.getSelectedItem()).getCode());
    }

    public boolean isUseImportant() {
        return !ApplicationService.EMPTY.equals(((ImportanceType) importanceSelect.getSelectedItem()).getCode());
    }
    public boolean isUseUser() {
        return !ApplicationService.EMPTY.equals(((Users) userSelect.getSelectedItem()).getCode());
    }
}
package ru.MonitoringMoney.frame;

import ru.MonitoringMoney.ApplicationProperties;
import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.CheckBoxListService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.types.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import ru.mangeorge.awt.service.CalendarService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Основной фрейм приложения
 */
public class MainFrame extends JFrame implements Serializable {

    private static final long serialVersionUID = -7550278965422223450L;


    /** Фраза, которая отображается в строчке поиска по подстроке, до начала ввода поискового выражения */
    private static final String TERM_INPUT_DEFAULT_TEXT = "Поиск по подстроке";

    /** Фраза, которая отображается до суммы всех покупок отображаемым по заданным фильтрам */
    private static final String PREFIX_LABEL_SUM_PRICE = "Потрачено на сумму: ";

    private static final String FRAME_NAME = "MonitoringMoney";


    private JTextArea text;
    public JTextField termInput;
    private JComboBox<CheckBoxListService.CheckComboValue>  importanceSelect;
    public JComboBox<CheckBoxListService.CheckComboValue> payTypeSelect;
    public JTextField priceFromText;
    public JTextField priceToText;
    public JFormattedTextField dateFromText;
    public JFormattedTextField dateToText;
    private JComboBox<CheckBoxListService.CheckComboValue> userSelect;
    private JLabel labelSumPrice;
    private GraphicsFrame graphicsFrame;
    public EditFrame editFrame;
    public DesiredPurchaseFrame desiredPurchaseFrame;


    public MainFrame() {
        setLocation(ApplicationService.getInstance().getWindowLocation(MainFrame.class));
        setSize(ApplicationService.getInstance().getWindowSize(MainFrame.class));
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setTitle(FRAME_NAME + "_" + MonitoringMoney.VERSION);
        setIconImage(ImageService.getMoneyImage());

        addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) { }
            public void componentMoved(ComponentEvent e) { }
            public void componentShown(ComponentEvent e) { }
            public void componentHidden(ComponentEvent e) {
                ApplicationService.getInstance().updateSizeWindow(MainFrame.class, getSize());
                ApplicationService.getInstance().updateLocationWindow(MainFrame.class, getLocation());
            }
        });

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
            setBounds(250, 5, 285, 186);
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
            addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) { }
                public void focusLost(FocusEvent e) {
                    if ("".equals(getText()))
                        setText(TERM_INPUT_DEFAULT_TEXT);
                }
            });
        }};
        panel.add(termInput);

        importanceSelect = new JComboBox<CheckBoxListService.CheckComboValue>() {
            public void setPopupVisible(boolean v) { }
            {
                setModel(CheckBoxListService.getModel(ApplicationService.getInstance().getSortedImportance()));
                setBounds(5, 40, 240, 30);
                setRenderer(new CheckBoxListService.CheckComboRenderer());
                addActionListener(new CheckBoxListService.CheckBoxList());
            }
        };
        panel.add(importanceSelect);

        payTypeSelect = new JComboBox<CheckBoxListService.CheckComboValue>() {
            public void setPopupVisible(boolean v) { }
            {
                setModel(CheckBoxListService.getModel(ApplicationService.getInstance().getSortedPayTypes()));
                setBounds(5, 75, 240, 30);
                setRenderer(new CheckBoxListService.CheckComboRenderer());
                addActionListener(new CheckBoxListService.CheckBoxList());
            }
        };
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

        dateFromText = new JFormattedTextField(ApplicationProperties.FORMAT_DATE) {{
            setBounds(85, 135, 65, 20);
            setValue(DateUtils.truncate(new Date(), Calendar.MONTH));
            addMouseListener(new MouseListener() {
                public void mouseReleased(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseClicked(MouseEvent e) {}
                public void mousePressed(MouseEvent e) {
                    try { CalendarService.addPopupCalendarDialog(dateFromText, ApplicationProperties.FORMAT_DATE, val -> refreshText()); } catch (ParseException ignore) { }
                }
            });
        }};
        panel.add(dateFromText);

        JLabel labelDateTo = new JLabel("по") {{
            setBounds(155, 135, 20, 20);
        }};
        panel.add(labelDateTo);

        dateToText = new JFormattedTextField(ApplicationProperties.FORMAT_DATE) {{
            setBounds(180, 135, 65, 20);
            setValue(DateUtils.addDays(DateUtils.addMonths(DateUtils.truncate(new Date(), Calendar.MONTH), 1), -1));
            addMouseListener(new MouseListener() {
                public void mouseReleased(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseClicked(MouseEvent e) {}
                public void mousePressed(MouseEvent e) {
                    try { CalendarService.addPopupCalendarDialog(dateToText, ApplicationProperties.FORMAT_DATE, val -> refreshText()); } catch (ParseException ignore) { }
                }
            });
        }};
        panel.add(dateToText);

        userSelect = new JComboBox<CheckBoxListService.CheckComboValue>() {
            public void setPopupVisible(boolean v) { }
            {
                setModel(CheckBoxListService.getModel(ApplicationService.getInstance().getSortedUsers()));
                setBounds(5, 160, 240, 30);
                setRenderer(new CheckBoxListService.CheckComboRenderer());
                addActionListener(new CheckBoxListService.CheckBoxList());
            }
        };
        panel.add(userSelect);

        labelSumPrice = new JLabel() {{
            setBounds(250, 195, 240, 20);
        }};
        panel.add(labelSumPrice);

        JButton buttonAdd = new JButton("Добавить покупку") {{
            setBounds(5, 195, 240, 30);
            addActionListener(e -> MonitoringMoney.addFrame.showFrame());
        }};
        panel.add(buttonAdd);

        JButton buttonGraphics = new JButton() {{
            setBorder(null);
            setBounds(470, 195, 30, 30);
            addActionListener(e -> EventQueue.invokeLater(() -> graphicsFrame = new GraphicsFrame()));
            setIcon(ImageService.getGraphicsButtonIcon());
        }};
        panel.add(buttonGraphics);

        JButton buttonEditPays = new JButton() {{
            setBorder(null);
            setBounds(435, 195, 30, 30);
            addActionListener(e -> EventQueue.invokeLater(() -> editFrame = new EditFrame()));
            setIcon(ImageService.getEditButtonIcon());
        }};
        panel.add(buttonEditPays);

        JButton buttonEditDesiredPurchase = new JButton() {{
            setBorder(null);
            setBounds(505, 195, 30, 30);
            addActionListener(e -> EventQueue.invokeLater(() -> desiredPurchaseFrame = new DesiredPurchaseFrame()));
            setIcon(ImageService.getDesiredPurchase());
        }};
        panel.add(buttonEditDesiredPurchase);

        refreshText();
    }


    public void refreshText() {
        text.setText(ApplicationService.getInstance().getTextPayObjects(getPayObjectWithCurrentFilters()));
        labelSumPrice.setText(PREFIX_LABEL_SUM_PRICE + " "  + ApplicationService.getInstance().getSumPrice(getPayObjectWithCurrentFilters()));
        if (graphicsFrame != null)
            graphicsFrame.updateData();
        if (editFrame != null)
            editFrame.updatePayObjectTable();
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

        List<TypeValue> selectedPayTypes = getSelectedValues((DefaultComboBoxModel) payTypeSelect.getModel());
        List<TypeValue> selectedImportanceTypes = getSelectedValues((DefaultComboBoxModel) importanceSelect.getModel());
        List<TypeValue> selectedUsers = getSelectedValues((DefaultComboBoxModel) userSelect.getModel());

        return ApplicationService.getInstance().getPayObjectsWithFilters(term, dateFrom, dateTo, priceFrom,
                priceTo, selectedImportanceTypes, selectedPayTypes, selectedUsers);
    }

    private List<TypeValue> getSelectedValues(DefaultComboBoxModel defaultModel) {
        List<TypeValue> selected = new ArrayList<>();
        for (int i = 0; i < defaultModel.getSize(); i++) {
            CheckBoxListService.CheckComboValue value = (CheckBoxListService.CheckComboValue) defaultModel.getElementAt(i);
            if (value.isSelected()) {
                selected.add(value.getType());
            }
        }
        return selected;
    }

    public void selectPayTypeValue(String payTypeName) {
        PayType type = null;
        for (PayType payType : ApplicationService.getInstance().getPayTypes()) {
            if (payType.toString().equals(payTypeName)) {
                type = payType;
            }
        }

        selectValue((DefaultComboBoxModel) payTypeSelect.getModel(), type);
    }

    public void selectImportanceValue(String importanceName) {
        ImportanceType type = null;
        for (ImportanceType importance : ApplicationService.getInstance().getImportanceTypes()) {
            if (importance.getName().equals(importanceName)) {
                type = importance;
            }
        }

        selectValue((DefaultComboBoxModel) importanceSelect.getModel(), type);
    }

    public void selectUserValue(String userName) {
        Users type = null;
        for (Users payType : ApplicationService.getInstance().getUsers()) {
            if (payType.getName().equals(userName)) {
                type = payType;
            }
        }

        selectValue((DefaultComboBoxModel) userSelect.getModel(), type);
    }

    private void selectValue(DefaultComboBoxModel defaultModel, TypeValue typeValue) {
        for (int i = 0; i < defaultModel.getSize(); i++) {
            CheckBoxListService.CheckComboValue value = (CheckBoxListService.CheckComboValue) defaultModel.getElementAt(i);
            if (value.getType().equals(typeValue)) {
                defaultModel.setSelectedItem(value);
                value.setState(true);
            }
        }
    }

    public boolean isUsePayType() {
        List<TypeValue> selectedPayTypes = getSelectedValues((DefaultComboBoxModel) payTypeSelect.getModel());
        return selectedPayTypes.size() == 0 ||
                (selectedPayTypes.size() == 1 && ApplicationProperties.EMPTY.equals(selectedPayTypes.get(0).getCode()));
    }

    public boolean isUseImportant() {
        List<TypeValue> selectedPayTypes = getSelectedValues((DefaultComboBoxModel) importanceSelect.getModel());
        return selectedPayTypes.size() == 0 ||
                (selectedPayTypes.size() == 1 && ApplicationProperties.EMPTY.equals(selectedPayTypes.get(0).getCode()));
    }
    public boolean isUseUser() {
        List<TypeValue> selectedPayTypes = getSelectedValues((DefaultComboBoxModel) userSelect.getModel());
        return selectedPayTypes.size() == 0 ||
                (selectedPayTypes.size() == 1 && ApplicationProperties.EMPTY.equals(selectedPayTypes.get(0).getCode()));
    }

    /**
     * Добавляет переданное значение нового типа покупки в список типов.
     * По классу определяет в какой список добавить
     *
     * @param item      новое значение
     */
    public void addSelectElement(Object item) {
        if (item instanceof PayType)
            payTypeSelect.addItem(new CheckBoxListService.CheckComboValue((PayType) item, false));
        else if (item instanceof ImportanceType)
            importanceSelect.addItem(new CheckBoxListService.CheckComboValue((ImportanceType) item, false));
        else if (item instanceof Users)
            userSelect.addItem(new CheckBoxListService.CheckComboValue((Users) item, false));
    }

    public void removeSelectElement(Object item) {
        if (item instanceof PayType) {
            CheckBoxListService.CheckComboValue value = getSelectValue((TypeValue) item, payTypeSelect);
            if (value != null) {
                payTypeSelect.removeItem(value);
            }
        } else if (item instanceof ImportanceType) {
            CheckBoxListService.CheckComboValue value = getSelectValue((TypeValue) item, importanceSelect);
            if (value != null) {
                importanceSelect.removeItem(value);
            }
        } else if (item instanceof Users) {
            CheckBoxListService.CheckComboValue value = getSelectValue((TypeValue) item, userSelect);
            if (value != null) {
                userSelect.removeItem(value);
            }
        }
    }

    public CheckBoxListService.CheckComboValue getSelectValue(TypeValue value, JComboBox comboBox) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            CheckBoxListService.CheckComboValue comboValue = (CheckBoxListService.CheckComboValue) comboBox.getItemAt(i);
            if (comboValue.getType().equals(value))
                return comboValue;
        }
        return null;
    }
}
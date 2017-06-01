package ru.MonitoringMoney.frame;

import ru.MonitoringMoney.ApplicationProperties;
import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.CheckBoxListService;
import ru.MonitoringMoney.services.FrameService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.types.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import ru.mangeorge.swing.service.PieService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
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

    /** Наименование окна */
    private static final String FRAME_NAME = "MonitoringMoney";


    /** Компоненты фрейма */
    private JTextArea text;
    private JTextField termInput;
    private JComboBox<CheckBoxListService.CheckComboValue>  importanceSelect;
    private JComboBox<CheckBoxListService.CheckComboValue> payTypeSelect;
    private JComboBox<CheckBoxListService.CheckComboValue> userSelect;
    private JTextField priceFromText;
    private JTextField priceToText;
    private JFormattedTextField dateFromText;
    private JFormattedTextField dateToText;
    private JLabel labelSumPrice;

    /** Ссылка на другие окна приложения */
    private GraphicsFrame graphicsFrame;
    public EditFrame editFrame;
    public DesiredPurchaseFrame desiredPurchaseFrame;
    private LimitFrame limitFrame;


    public MainFrame() {
        setLocation(ApplicationService.getInstance().getWindowLocation(MainFrame.class));
        setSize(ApplicationService.getInstance().getWindowSize(MainFrame.class));
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setTitle(FRAME_NAME + "_" + MonitoringMoney.VERSION);
        setIconImage(ImageService.getMoneyImage());
        addComponentListener(FrameService.addComponentListener(MainFrame.class, getSize(), getLocation(), () -> {}));

        JPanel panel = new JPanel(){{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);

        text = FrameService.createJTextArea(panel, new Rectangle(250, 5, 320, 186), () -> {});

        termInput = new JTextField() {{
            setBounds(5, 5, 240, 30);
            setText(TERM_INPUT_DEFAULT_TEXT);
            setDisabledTextColor(Color.LIGHT_GRAY);
            setSelectedTextColor(Color.LIGHT_GRAY);
            setSelectionColor(Color.LIGHT_GRAY);
            addMouseListener(FrameService.createMouseListener(() -> {
                if (TERM_INPUT_DEFAULT_TEXT.equals(getText())) {
                    setText("");
                }
            }));
            addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {}
                public void keyReleased(KeyEvent e) {
                    updateData();
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

        importanceSelect = FrameService.createMultiSelectType(panel, ApplicationService.getInstance().getSortedImportance(), new Rectangle(5, 40, 240, 30));
        payTypeSelect = FrameService.createMultiSelectType(panel, ApplicationService.getInstance().getSortedPayTypes(), new Rectangle(5, 75, 240, 30));
        userSelect = FrameService.createMultiSelectType(panel, ApplicationService.getInstance().getSortedUsers(), new Rectangle(5, 160, 240, 30));

        JLabel priceFromLabel = new JLabel("Цена от") {{
            setBounds(5, 110, 60, 20);
        }};
        panel.add(priceFromLabel);

        priceFromText = new JTextField() {{
            setBounds(65, 110, 75, 20);
            addKeyListener(FrameService.createPriceKeyListener(this, MainFrame.this::updateData));
        }};
        panel.add(priceFromText);

        JLabel priceToLabel = new JLabel("до") {{
            setBounds(145, 110, 20, 20);
        }};
        panel.add(priceToLabel);

        priceToText = new JTextField() {{
            setBounds(170, 110, 75, 20);
            addKeyListener(FrameService.createPriceKeyListener(this, MainFrame.this::updateData));
        }};
        panel.add(priceToText);

        JLabel labelFromDate = new JLabel("В период c") {{
            setBounds(5, 135, 80, 20);
        }};
        panel.add(labelFromDate);

        dateFromText = new JFormattedTextField(ApplicationProperties.FORMAT_DATE) {{
            setBounds(85, 135, 65, 20);
            setValue(DateUtils.truncate(new Date(), Calendar.MONTH));
            addMouseListener(FrameService.getMouseListenerPopupCalendarDialog(this, val -> updateData(), () -> {}));
        }};
        panel.add(dateFromText);

        JLabel labelDateTo = new JLabel("по") {{
            setBounds(155, 135, 20, 20);
        }};
        panel.add(labelDateTo);

        dateToText = new JFormattedTextField(ApplicationProperties.FORMAT_DATE) {{
            setBounds(180, 135, 65, 20);
            setValue(DateUtils.addDays(DateUtils.addMonths(DateUtils.truncate(new Date(), Calendar.MONTH), 1), -1));
            addMouseListener(FrameService.getMouseListenerPopupCalendarDialog(this, val -> updateData(), () -> {}));
        }};
        panel.add(dateToText);

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
            setIcon(ImageService.getDesiredPurchaseIcon());
        }};
        panel.add(buttonEditDesiredPurchase);

        JButton buttonLimit = new JButton() {{
            setBorder(null);
            setBounds(540, 195, 30, 30);
            addActionListener(e -> EventQueue.invokeLater(() -> limitFrame = new LimitFrame()));
            setIcon(ImageService.getLimitIcon());
        }};
        panel.add(buttonLimit);

        updateData();
    }

    /**
     * Обновляем данные
     */
    public void updateData() {
        text.setText(ApplicationService.getInstance().getTextPayObjects(getPayObjectWithCurrentFilters()));
        labelSumPrice.setText(PREFIX_LABEL_SUM_PRICE + " "  + ApplicationService.getInstance().getSumPrice(getPayObjectWithCurrentFilters()));
        if (graphicsFrame != null)
            graphicsFrame.updateData();
        if (editFrame != null)
            editFrame.updatePayObjectTable();
    }

    /**
     * По выбранным фильтрам отбирает покупки
     *
     * @return список покупок по заданным фильтрам
     */
    public List<PayObject> getPayObjectWithCurrentFilters() {
        String term = null;
        if (!TERM_INPUT_DEFAULT_TEXT.equals(termInput.getText()) && StringUtils.isNotBlank(termInput.getText()))
            term = termInput.getText();

        Date dateFrom = DateUtils.truncate((Date) dateFromText.getValue(), Calendar.DATE);
        Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTime(DateUtils.truncate((Date) dateToText.getValue(), Calendar.DATE));
        calendarTo.add(Calendar.DATE, 1);
        Date dateTo = calendarTo.getTime();

        Integer priceFrom = null;
        Integer priceTo = null;
        if (StringUtils.isNotBlank(priceFromText.getText().replaceAll("[^0-9]", "")))
            priceFrom = Integer.parseInt(priceFromText.getText().replaceAll("[^0-9]", ""));
        if (StringUtils.isNotBlank(priceToText.getText().replaceAll("[^0-9]", "")))
            priceTo = Integer.parseInt(priceToText.getText().replaceAll("[^0-9]", ""));

        List<TypeValue> selectedPayTypes = getSelectedValues((DefaultComboBoxModel) payTypeSelect.getModel());
        List<TypeValue> selectedImportanceTypes = getSelectedValues((DefaultComboBoxModel) importanceSelect.getModel());
        List<TypeValue> selectedUsers = getSelectedValues((DefaultComboBoxModel) userSelect.getModel());

        return ApplicationService.getInstance().getPayObjectsWithFilters(term, dateFrom, dateTo, priceFrom,
                priceTo, selectedImportanceTypes, selectedPayTypes, selectedUsers);
    }

    /**
     * @param defaultModel модель списка из которого нужно отобрать выбранные значения
     * @return список выбранных значений
     */
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

    /**
     * Выбирает указанное значение в списке типов покупки
     *
     * @param name          наименование значения
     * @param graphicValues список наименований значений отображаемых на графике пирожок (необходимо при выборе значения {PieService.ANOTHER_BLOCK_NAME}).
     */
    void selectPayTypeValue(String name, List graphicValues) {
        selectType(name, ApplicationService.getInstance().getPayTypes(), (DefaultComboBoxModel) payTypeSelect.getModel(), graphicValues);
    }

    /**
     * Выбирает указанное значение в списке уровней важности
     *
     * @param name          наименование значения
     * @param graphicValues список наименований значений отображаемых на графике пирожок (необходимо при выборе значения {PieService.ANOTHER_BLOCK_NAME}).
     */
    void selectImportanceValue(String name, List graphicValues) {
        selectType(name, ApplicationService.getInstance().getImportanceTypes(), (DefaultComboBoxModel) importanceSelect.getModel(), graphicValues);
    }

    /**
     * Выбирает указанное значение в списке пользователей
     *
     * @param name          наименование значения
     * @param graphicValues список наименований значений отображаемых на графике пирожок (необходимо при выборе значения {PieService.ANOTHER_BLOCK_NAME}).
     */
    void selectUserValue(String name, List graphicValues) {
        selectType(name, ApplicationService.getInstance().getUsers(), (DefaultComboBoxModel) userSelect.getModel(), graphicValues);
    }

    /**
     * Выбирает указанное значение в списке. Если значение {name} равно {PieService.ANOTHER_BLOCK_NAME}, то будут выбраны
     * те значения, которые не отображаются на графике пирожок
     *
     * @param name          Наименование значения
     * @param types         Список значений типов
     * @param model         Модель списка из которого нужно отобрать выбранные значения
     * @param graphicValues Список наименований значений отображаемых на графике пирожок (необходимо при выборе значения {PieService.ANOTHER_BLOCK_NAME}).
     * @param <T>           Тип списка
     */
    private <T> void selectType(String name, List<T> types, DefaultComboBoxModel model, List graphicValues) {
        if (PieService.ANOTHER_BLOCK_NAME.equals(name)) {
            if (getSelectedValues(model).isEmpty()) {
                selectAllValues(model, true);
            }

            for (int i = 0; i < model.getSize(); i++) {
                CheckBoxListService.CheckComboValue value = (CheckBoxListService.CheckComboValue) model.getElementAt(i);
                if (value.isSelected() && graphicValues.contains(value.getType().getName())) {
                    model.setSelectedItem(value);
                    value.setState(false);
                }
            }
            return;
        }
        selectAllValues(model, false);

        T type = null;
        for (T payType : types) {
            if (((TypeValue) payType).getName().equals(name)) {
                type = payType;
            }
        }

        selectValue(model, (TypeValue) type);
    }

    /**
     * Выбирает указанное значение в списке
     *
     * @param model      Модель списка из которого нужно отобрать выбранные значения
     * @param typeValue  Значение определённого атрибута
     */
    private void selectValue(DefaultComboBoxModel model, TypeValue typeValue) {
        for (int i = 0; i < model.getSize(); i++) {
            CheckBoxListService.CheckComboValue value = (CheckBoxListService.CheckComboValue) model.getElementAt(i);
            if (value.getType().equals(typeValue)) {
                model.setSelectedItem(value);
                value.setState(true);
            }
        }
    }

    /**
     * Устанавливает все значения из списка в положение {state}
     *
     * @param model Модель списка из которого нужно отобрать выбранные значения
     * @param state При {true} все значения из списка будут выбраны
     */
    private void selectAllValues(DefaultComboBoxModel model, boolean state) {
        for (int i = 0; i < model.getSize(); i++) {
            CheckBoxListService.CheckComboValue value = (CheckBoxListService.CheckComboValue) model.getElementAt(i);
            value.setState(state);
        }
    }

    /**
     * @return {true}, если не выбрано ни одно значение из списка типов покупок
     */
    boolean isNotUsePayType() {
        List<TypeValue> selectedPayTypes = getSelectedValues((DefaultComboBoxModel) payTypeSelect.getModel());
        return selectedPayTypes.isEmpty() ||
                (selectedPayTypes.size() == 1 && ApplicationProperties.EMPTY.equals(selectedPayTypes.get(0).getCode()));
    }

    /**
     * @return {true}, если не выбрано ни одно значение из списка уровней важности
     */
    boolean isNotUseImportant() {
        List<TypeValue> selectedPayTypes = getSelectedValues((DefaultComboBoxModel) importanceSelect.getModel());
        return selectedPayTypes.isEmpty() ||
                (selectedPayTypes.size() == 1 && ApplicationProperties.EMPTY.equals(selectedPayTypes.get(0).getCode()));
    }

    /**
     * @return {true}, если не выбрано ни одно значение из списка пользователей
     */
    boolean isNotUseUser() {
        List<TypeValue> selectedPayTypes = getSelectedValues((DefaultComboBoxModel) userSelect.getModel());
        return selectedPayTypes.isEmpty() ||
                (selectedPayTypes.size() == 1 && ApplicationProperties.EMPTY.equals(selectedPayTypes.get(0).getCode()));
    }

    /**
     * Добавляет переданное значение нового типа покупки в список типов.
     * По классу определяет в какой список добавить
     *
     * @param item  новое значение
     */
    void addSelectElement(Object item) {
        if (item instanceof PayType)
            payTypeSelect.addItem(new CheckBoxListService.CheckComboValue((PayType) item, false));
        else if (item instanceof ImportanceType)
            importanceSelect.addItem(new CheckBoxListService.CheckComboValue((ImportanceType) item, false));
        else if (item instanceof Users)
            userSelect.addItem(new CheckBoxListService.CheckComboValue((Users) item, false));
    }

    /**
     * Удаляет значение {item} из списка
     */
    public void removeSelectElement(Object item) {
        JComboBox select = null;
        if (item instanceof PayType) {
            select = payTypeSelect;
        } else if (item instanceof ImportanceType) {
            select = importanceSelect;
        } else if (item instanceof Users) {
            select = userSelect;
        }
        if (select != null) {
            CheckBoxListService.CheckComboValue value = getSelectValue((TypeValue) item, select);
            if (value != null) {
                select.removeItem(value);
            }
        }
    }

    /**
     * Находит значение во множественном списке
     *
     * @param value    значение
     * @param comboBox список в котором нужно искать значение {value}
     * @return объект {CheckBoxListService.CheckComboValue} соответствующий переданному значению
     */
    private CheckBoxListService.CheckComboValue getSelectValue(TypeValue value, JComboBox comboBox) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            CheckBoxListService.CheckComboValue comboValue = (CheckBoxListService.CheckComboValue) comboBox.getItemAt(i);
            if (comboValue.getType().equals(value))
                return comboValue;
        }
        return null;
    }

    void setDateFromText(Date date) {
        this.dateFromText.setValue(date);
    }

    void setDateToText(Date date) {
        this.dateToText.setValue(date);
    }
}
package ru.MonitoringMoney.frame;


import org.apache.commons.lang.StringUtils;
import ru.MonitoringMoney.main.ApplicationProperties;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.FrameService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.types.*;
import ru.mangeorge.swing.graphics.PopupDialog;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.Date;


/**
 * Фрейм для добавления покупок
 */
public class AddFrame extends JFrame implements Serializable {

    private static final long serialVersionUID = -5105213622223281168L;

    /** Заголовок фрейма */
    private static final String FRAME_NAME = "Добавление покупки";

    private JFormattedTextField dateText;
//    private JComboBox<ImportanceType> importanceSelect;
    private JComboBox<PayType> payTypeSelect;
    private JComboBox<Users> userSelect;
    private JTextArea textDescription;
    private JTextField priceText;

    /** Всплывающие окна ошибок валидиции */
    private PopupDialog importanceErrorPopup;
    private PopupDialog payTypeErrorPopup;
    private PopupDialog userErrorPopup;
    private PopupDialog priceErrorPopup;

    private JCheckBox checkBox;


    public AddFrame() {
        setResizable(false);
        setVisible(false);
        setTitle(FRAME_NAME);
        toFront();
        setIconImage(ImageService.PLUS_IMAGE);
        setLocation(ApplicationService.getInstance().getWindowLocation(this));
        setSize(ApplicationService.getInstance().getWindowSize(this));
        addComponentListener(FrameService.addComponentListener(AddFrame.class, getSize(), getLocation(), () -> {}));

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
            addMouseListener(FrameService.createMouseListener(AddFrame.this::disposePopup));
        }};
        add(panel);

//        importanceSelect = FrameService.createSelectTypeValue(panel, new Rectangle(5, 5, 200, 30),
//                ApplicationService.getInstance().getSortedImportance(), () -> { disposePopup(); new FrameAddPropertyValues(ImportanceType.class); },
//                AddFrame.this::disposePopup);
        payTypeSelect = FrameService.createSelectTypeValue(panel, new Rectangle(5, 40, 200, 30),
                ApplicationService.getInstance().getSortedPayTypes(), () -> { disposePopup(); new FrameAddPropertyValues(PayType.class); },
                AddFrame.this::disposePopup);
        userSelect = FrameService.createSelectTypeValue(panel, new Rectangle(5, 220, 200, 30),
                ApplicationService.getInstance().getSortedUsers(), () -> { disposePopup(); new FrameAddPropertyValues(Users.class); },
                AddFrame.this::disposePopup);

        panel.add(new JLabel("Стоимость покупки") {{
            setBounds(5, 75, 140, 20);
        }});

        priceText = new JTextField() {{
            setBounds(145, 75, 90, 20);
            addKeyListener(FrameService.createPriceKeyListener(this, () -> {}));
            addMouseListener(FrameService.createMouseListener(AddFrame.this::disposePopup));
        }};
        panel.add(priceText);

        panel.add(new JLabel("Дата покупки") {{
            setBounds(5, 100, 140, 20);
        }});

        dateText = new JFormattedTextField(ApplicationProperties.FORMAT_DATE) {{
            setBounds(145, 100, 90, 20);
            setValue(new Date());
            addMouseListener(FrameService.getMouseListenerPopupCalendarDialog(this, null, AddFrame.this::disposePopup));
        }};
        panel.add(dateText);

        textDescription = FrameService.createJTextArea(panel, new Rectangle(5, 130, 235, 80), this::disposePopup);

        panel.add(new JButton("Добавить") {{
            setBounds(5, 255, 115, 30);
            addActionListener(e -> addPayObject());
        }});

        panel.add(new JButton("Отмена") {{
            setBounds(125, 255, 115, 30);
            addActionListener(e -> hideFrame());
        }});

        checkBox = new JCheckBox() {{
            setBounds(5, 290, 20, 20);
            setSelected(true);
        }};
        panel.add(checkBox);
        panel.add(new JLabel("Скрывать фрейм") {{
            setBounds(25, 285, 100, 30);
        }});

    }

    private void addPayObject() {
        boolean checkParams = true;
        disposePopup();
//        if (importanceSelect.getSelectedItem() == null || TypeValue.EMPTY.equals(((TypeValue) importanceSelect.getSelectedItem()).getCode())) {
//            importanceErrorPopup = FrameService.createErrorDialog("Необходимо выбрать уровень важности", importanceSelect);
//            checkParams = false;
//        }
        if (payTypeSelect.getSelectedItem() == null || TypeValue.EMPTY.equals(((TypeValue) payTypeSelect.getSelectedItem()).getCode())) {
            payTypeErrorPopup = FrameService.createErrorDialog("Необходимо выбрать тип покупки", payTypeSelect);
            checkParams = false;
        }
        if (userSelect.getSelectedItem() == null || TypeValue.EMPTY.equals(((TypeValue) userSelect.getSelectedItem()).getCode())) {
            userErrorPopup = FrameService.createErrorDialog("Необходимо выбрать пользователя", userSelect);
            checkParams = false;
        }
        if (StringUtils.isBlank(priceText.getText())) {
            priceErrorPopup = FrameService.createErrorDialog("Необходимо указать цену", priceText);
            checkParams = false;
        }

        if (checkParams) {
            PayObject pay = new PayObject();
            pay.setDate((Date) dateText.getValue());
            pay.setDescription(textDescription.getText());
//            pay.setImportance((ImportanceType) importanceSelect.getSelectedItem());
            pay.setImportance(ApplicationService.getInstance().getImportanceTypes().get(1));
            pay.setPayType((PayType) payTypeSelect.getSelectedItem());
            pay.setPrice(Integer.parseInt(priceText.getText()));
            pay.setUser((Users) userSelect.getSelectedItem());
            ApplicationService.getInstance().addPayObject(pay);

            if (checkBox.isSelected()) {
                hideFrame();
            }
        }
    }

    /**
     * Добавляет переданное значение нового типа покупки в список типов.
     * По классу определяет в какой список добавить.
     * Устанавлмвает значение <code>item</code> выбраным в записанном списке.
     *
     * @param item      новое значение
     */
    void addSelectElement(Object item) {
        if (item instanceof PayType) {
            payTypeSelect.addItem((PayType) item);
            payTypeSelect.setSelectedItem(item);
        } else if (item instanceof ImportanceType) {
//            importanceSelect.addItem((ImportanceType) item);
//            importanceSelect.setSelectedItem(item);
        } else if (item instanceof Users) {
            userSelect.addItem((Users) item);
            userSelect.setSelectedItem(item);
        }
    }

    public void removeSelectElement(Object item) {
        if (item instanceof PayType) {
            payTypeSelect.removeItem(item);
        } else if (item instanceof ImportanceType) {
//            importanceSelect.removeItem(item);
        } else if (item instanceof Users) {
            userSelect.removeItem(item);
        }
    }

    void showFrame() {
        textDescription.setText("");
        setVisible(true);
    }

    private void hideFrame() {
        disposePopup();
        setVisible(false);
    }

    private void disposePopup() {
        if (importanceErrorPopup != null) {
            importanceErrorPopup.closeDialog();
        }
        if (payTypeErrorPopup != null) {
            payTypeErrorPopup.closeDialog();
        }
        if (userErrorPopup != null) {
            userErrorPopup.closeDialog();
        }
        if (priceErrorPopup != null) {
            priceErrorPopup.closeDialog();
        }
    }
}

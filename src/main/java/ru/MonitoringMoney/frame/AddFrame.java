package ru.MonitoringMoney.frame;


import org.apache.commons.lang.StringUtils;
import ru.MonitoringMoney.*;
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
    private JComboBox<ImportanceType> importanceSelect;
    private JComboBox<PayType> payTypeSelect;
    private JComboBox<Users> userSelect;
    private JTextArea textDescription;
    private JTextField priceText;

    /** Всплывающие окна ошибок валидиции */
    private PopupDialog importanceErrorPopup;
    private PopupDialog payTypeErrorPopup;
    private PopupDialog userErrorPopup;
    private PopupDialog priceErrorPopup;


    public AddFrame() {
        setLocation(ApplicationService.getInstance().getWindowLocation(AddFrame.class));
        setSize(ApplicationService.getInstance().getWindowSize(AddFrame.class));
        setResizable(false);
        setTitle(FRAME_NAME);
        setIconImage(ImageService.getPlusImage());
        toFront();
        addComponentListener(FrameService.addComponentListener(AddFrame.class, getSize(), getLocation(), () -> {}));

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
            addMouseListener(FrameService.createMouseListener(AddFrame.this::disposePopup));
        }};
        add(panel);

        importanceSelect = FrameService.createSelectTypeValue(panel, new Rectangle(5, 5, 200, 30),
                ApplicationService.getInstance().getSortedImportance(), () -> { disposePopup(); new FrameAddPropertyValues(ImportanceType.class); },
                AddFrame.this::disposePopup);
        payTypeSelect = FrameService.createSelectTypeValue(panel, new Rectangle(5, 40, 200, 30),
                ApplicationService.getInstance().getSortedPayTypes(), () -> { disposePopup(); new FrameAddPropertyValues(PayType.class); },
                AddFrame.this::disposePopup);
        userSelect = FrameService.createSelectTypeValue(panel, new Rectangle(5, 220, 200, 30),
                ApplicationService.getInstance().getSortedUsers(), () -> { disposePopup(); new FrameAddPropertyValues(Users.class); },
                AddFrame.this::disposePopup);

        JLabel priceLabel = new JLabel("Стоимость покупки") {{
            setBounds(5, 75, 140, 20);
        }};
        panel.add(priceLabel);

        priceText = new JTextField() {{
            setBounds(145, 75, 90, 20);
            addKeyListener(FrameService.createPriceKeyListener(this, () -> {}));
            addMouseListener(FrameService.createMouseListener(AddFrame.this::disposePopup));
        }};
        panel.add(priceText);

        JLabel labelFromDate = new JLabel("Дата покупки") {{
            setBounds(5, 100, 140, 20);
        }};
        panel.add(labelFromDate);

        dateText = new JFormattedTextField(ApplicationProperties.FORMAT_DATE) {{
            setBounds(145, 100, 90, 20);
            setValue(new Date());
            addMouseListener(FrameService.getMouseListenerPopupCalendarDialog(this, null, AddFrame.this::disposePopup));
        }};
        panel.add(dateText);

        textDescription = FrameService.createJTextArea(panel, new Rectangle(5, 130, 235, 80), this::disposePopup);

        JButton okButton = new JButton("Добавить") {{
            setBounds(5, 255, 115, 30);
            addActionListener(e -> addPayObject());
        }};
        panel.add(okButton);

        JButton cancelButton = new JButton("Отмена") {{
            setBounds(125, 255, 115, 30);
            addActionListener(e -> hideFrame());
        }};
        panel.add(cancelButton);
    }


    private PopupDialog createErrorDialog(String title, JComponent select) {
        JLabel label = new JLabel("<html><font color=\"red\">" + title + "</font></html>") {{
            setBounds(10, 0, title.length() * 8, 30);
        }};
        return new PopupDialog(select, new Dimension(title.length() * 8, 40), new Component[]{label}, true, false);
    }

    private void addPayObject() {
        boolean checkParams = true;
        disposePopup();
        if (ApplicationProperties.EMPTY.equals(((ImportanceType) importanceSelect.getSelectedItem()).getCode())) {
            importanceErrorPopup = createErrorDialog("Необходимо выбрать уровень важности", importanceSelect);
            checkParams = false;
        }
        if (ApplicationProperties.EMPTY.equals(((PayType) payTypeSelect.getSelectedItem()).getCode())) {
            payTypeErrorPopup = createErrorDialog("Необходимо выбрать тип покупки", payTypeSelect);
            checkParams = false;
        }
        if (ApplicationProperties.EMPTY.equals(((Users) userSelect.getSelectedItem()).getCode())) {
            userErrorPopup = createErrorDialog("Необходимо выбрать пользователя", userSelect);
            checkParams = false;
        }
        if (StringUtils.isBlank(priceText.getText())) {
            priceErrorPopup = createErrorDialog("Необходимо указать цену", priceText);
            checkParams = false;
        }

        if (checkParams) {
            PayObject pay = new PayObject();
            pay.setDate((Date) dateText.getValue());
            pay.setDescription(textDescription.getText());
            pay.setImportance((ImportanceType) importanceSelect.getSelectedItem());
            pay.setPayType((PayType) payTypeSelect.getSelectedItem());
            pay.setPrice(Integer.parseInt(priceText.getText()));
            pay.setUser((Users) userSelect.getSelectedItem());
            ApplicationService.getInstance().addPayObject(pay);

            hideFrame();
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
            importanceSelect.addItem((ImportanceType) item);
            importanceSelect.setSelectedItem(item);
        } else if (item instanceof Users) {
            userSelect.addItem((Users) item);
            userSelect.setSelectedItem(item);
        }
    }

    public void removeSelectElement(Object item) {
        if (item instanceof PayType) {
            payTypeSelect.removeItem(item);
        } else if (item instanceof ImportanceType) {
            importanceSelect.removeItem(item);
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

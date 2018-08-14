package ru.MonitoringMoney.frame;


import org.apache.commons.lang.StringUtils;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.CheckBoxListService;
import ru.MonitoringMoney.services.FrameService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.types.*;
import ru.mangeorge.swing.graphics.PopupDialog;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Фрейм для добавления покупок
 */
public class AddAccountFrame extends JFrame implements Serializable {

    private static final long serialVersionUID = -5105213622223281168L;

    /** Заголовок фрейма */
    private static final String FRAME_NAME = "Добавление счёта";

    private JTextField limitText;
    private JTextField nameText;
    private JComboBox<CheckBoxListService.CheckComboValue> payTypeSelect;
    private JCheckBox replenishableCheckBox;


    /** Всплывающие окна ошибок валидиции */
    private PopupDialog payTypeErrorPopup;
    private PopupDialog limitErrorPopup;
    private PopupDialog nameErrorPopup;


    public AddAccountFrame() {
        setResizable(false);
        setVisible(false);
        setTitle(FRAME_NAME);
        toFront();
        setIconImage(ImageService.PLUS_IMAGE);
        setLocation(ApplicationService.getInstance().getWindowLocation(this));
        setSize(ApplicationService.getInstance().getWindowSize(this));
        addComponentListener(FrameService.addComponentListener(AddAccountFrame.class, getSize(), getLocation(), () -> {}));

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
            addMouseListener(FrameService.createMouseListener(AddAccountFrame.this::disposePopup));
        }};
        add(panel);

        panel.add(new JLabel("Наименование") {{
            setBounds(5, 5, 100, 20);
        }});

        nameText = new JTextField() {{
            setBounds(105, 5, 130, 20);
            addMouseListener(FrameService.createMouseListener(AddAccountFrame.this::disposePopup));
        }};
        panel.add(nameText);

        panel.add(new JLabel("Предел счёта") {{
            setBounds(5, 40, 100, 20);
        }});

        limitText = new JTextField() {{
            setBounds(105, 40, 130, 20);
            addKeyListener(FrameService.createPriceKeyListener(this, () -> {}));
            addMouseListener(FrameService.createMouseListener(AddAccountFrame.this::disposePopup));
        }};
        panel.add(limitText);

        payTypeSelect = FrameService.createMultiSelectType(panel, ApplicationService.getInstance().getSortedPayTypes(),
                new Rectangle(5, 75, 240, 30));

        replenishableCheckBox = new JCheckBox() {{
            setBounds(5, 110, 20, 20);
        }};
        panel.add(replenishableCheckBox);
        panel.add(new JLabel("Счёт пополняемый") {{
            setBounds(25, 110, 100, 30);
        }});


        panel.add(new JButton("Добавить") {{
            setBounds(5, 140, 115, 30);
            addActionListener(e -> addPayObject());
        }});

        panel.add(new JButton("Отмена") {{
            setBounds(125, 140, 115, 30);
            addActionListener(e -> hideFrame());
        }});

    }

    private void addPayObject() {
        boolean checkParams = true;
        disposePopup();

        if (StringUtils.isBlank(nameText.getText())) {
            nameErrorPopup = FrameService.createErrorDialog("Необходимо указать наименование", nameText);
            checkParams = false;
        }
        if (StringUtils.isBlank(limitText.getText()) && !replenishableCheckBox.isSelected()) {
            limitErrorPopup = FrameService.createErrorDialog("Необходимо указать лимит", limitText);
            checkParams = false;
        }

        List<TypeValue> selectedPayTypes = MainFrame.getSelectedValues((DefaultComboBoxModel) payTypeSelect.getModel());
        if (checkParams) {
            Account account = new Account();
            account.setName(nameText.getText());
            account.setLimit(Integer.parseInt(limitText.getText()));
            account.setReplenishable(replenishableCheckBox.isSelected());
            account.setTypes(selectedPayTypes.stream().map(t -> (PayType) t).collect(Collectors.toList()));
            ApplicationService.getInstance().addAccount(account);
        }
    }

    public void removeSelectElement(Object item) {
        CheckBoxListService.CheckComboValue value = MainFrame.getSelectValue((TypeValue) item, payTypeSelect);
        if (value != null) {
            payTypeSelect.removeItem(value);
        }
    }

    void addSelectElement(Object item) {
        if (item instanceof PayType)
            payTypeSelect.addItem(new CheckBoxListService.CheckComboValue((PayType) item, false));
    }


    void showFrame() {
        setVisible(true);
    }

    private void hideFrame() {
        disposePopup();
        setVisible(false);
    }

    private void disposePopup() {
        if (payTypeErrorPopup != null) {
            payTypeErrorPopup.closeDialog();
        }
        if (limitErrorPopup != null) {
            limitErrorPopup.closeDialog();
        }
        if (nameErrorPopup != null) {
            nameErrorPopup.closeDialog();
        }
    }
}

package ru.MonitoringMoney.frame;


import org.apache.commons.lang.StringUtils;
import ru.MonitoringMoney.ApplicationProperties;
import ru.MonitoringMoney.income.Income;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.FrameService;
import ru.MonitoringMoney.services.ImageService;
import ru.mangeorge.swing.graphics.PopupDialog;

import javax.swing.*;
import java.io.Serializable;
import java.util.Date;


/**
 * Фрейм для добавления доходов
 */
public class AddIncomeFrame extends JFrame implements Serializable {

    private static final long serialVersionUID = 2306831544787529125L;

    private static final String FRAME_NAME = "Добавление дохода";

    private JTextField amountCountText;
    private JFormattedTextField dateText;

    /** Всплывающие окно ошибок валидиции ввода суммы */
    private PopupDialog amountCountErrorPopup;


    public AddIncomeFrame() {
        setResizable(false);
        setVisible(false);
        setTitle(FRAME_NAME);
        setIconImage(ImageService.ADD_INCOME_IMAGE);
        setLocation(ApplicationService.getInstance().getWindowLocation(this));
        setSize(ApplicationService.getInstance().getWindowSize(this));
        addComponentListener(FrameService.addComponentListener(AddFrame.class, getSize(), getLocation(), () -> {}));

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
            addMouseListener(FrameService.createMouseListener(AddIncomeFrame.this::disposePopup));
        }};
        add(panel);


        JLabel newValueLabel = new JLabel() {{
            setText("Сумма дохода");
            setBounds(5, 0, 140, 30);
        }};
        panel.add(newValueLabel);

        amountCountText = new JTextField() {{
            setBounds(130, 10, 90, 20);
            addKeyListener(FrameService.createPriceKeyListener(this, () -> {}));
            addMouseListener(FrameService.createMouseListener(AddIncomeFrame.this::disposePopup));
        }};
        panel.add(amountCountText);

        JLabel labelFromDate = new JLabel("Дата дохода") {{
            setBounds(5, 20, 140, 30);
        }};
        panel.add(labelFromDate);

        dateText = new JFormattedTextField(ApplicationProperties.FORMAT_DATE) {{
            setBounds(130, 30, 90, 20);
            setValue(new Date());
            addMouseListener(FrameService.getMouseListenerPopupCalendarDialog(this, null, AddIncomeFrame.this::disposePopup));
        }};
        panel.add(dateText);

        JButton addButton = new JButton("Добавить") {{
            setBounds(5, 55, 235, 30);
            addActionListener(e -> addIncome());
        }};
        panel.add(addButton);
    }

    private void addIncome() {
        disposePopup();
        if (StringUtils.isBlank(amountCountText.getText())) {
            amountCountErrorPopup = FrameService.createErrorDialog("Необходимо указать сумму", amountCountText);
        } else {
            ApplicationService.getInstance().addIncome(new Income((Date) dateText.getValue(), Integer.parseInt(amountCountText.getText())));
            hideFrame();
        }
    }

    private void disposePopup() {
        if (amountCountErrorPopup != null)
            amountCountErrorPopup.closeDialog();
    }

    void showFrame() {
        setVisible(true);
    }

    private void hideFrame() {
        disposePopup();
        setVisible(false);
    }
}

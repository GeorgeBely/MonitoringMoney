package ru.MonitoringMoney.frame;


import org.apache.commons.lang.StringUtils;
import ru.MonitoringMoney.main.ApplicationProperties;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.FrameService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.types.Income;
import ru.MonitoringMoney.types.IncomeType;
import ru.MonitoringMoney.types.Users;
import ru.mangeorge.swing.graphics.PopupDialog;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.Date;


/**
 * Фрейм для добавления доходов
 */
public class AddIncomeFrame extends JFrame implements Serializable {

    private static final long serialVersionUID = 2306831544787529125L;

    private static final String FRAME_NAME = "Добавление дохода";


    private JComboBox<IncomeType> incomeTypeSelect;
    private JComboBox<Users> userSelect;
    private JTextField amountCountText;
    private JFormattedTextField dateText;
    private JTextArea textDescription;

    /** Всплывающие окно ошибок валидиции ввода суммы */
    private PopupDialog amountCountErrorPopup;


    public AddIncomeFrame() {
        setResizable(false);
        setVisible(false);
        setTitle(FRAME_NAME);
        toFront();
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

        userSelect = FrameService.createSelectTypeValue(panel, new Rectangle(5, 185, 200, 30),
                ApplicationService.getInstance().getSortedUsers(), () -> { disposePopup(); new FrameAddPropertyValues(Users.class); },
                AddIncomeFrame.this::disposePopup);

        incomeTypeSelect = FrameService.createSelectTypeValue(panel, new Rectangle(5, 5, 200, 30),
                ApplicationService.getInstance().getSortedIncomeTypes(), () -> { disposePopup(); new FrameAddPropertyValues(IncomeType.class); },
                AddIncomeFrame.this::disposePopup);

        panel.add(new JLabel() {{
            setText("Сумма дохода");
            setBounds(5, 40, 140, 20);
        }});

        amountCountText = new JTextField() {{
            setBounds(145, 40, 90, 20);
            addKeyListener(FrameService.createPriceKeyListener(this, () -> {}));
            addMouseListener(FrameService.createMouseListener(AddIncomeFrame.this::disposePopup));
        }};
        panel.add(amountCountText);

        panel.add(new JLabel("Дата дохода") {{
            setBounds(5, 65, 140, 20);
        }});

        dateText = new JFormattedTextField(ApplicationProperties.FORMAT_DATE) {{
            setBounds(145, 65, 90, 20);
            setValue(new Date());
            addMouseListener(FrameService.getMouseListenerPopupCalendarDialog(this, null, AddIncomeFrame.this::disposePopup));
        }};
        panel.add(dateText);

        textDescription = FrameService.createJTextArea(panel, new Rectangle(5, 95, 235, 80), this::disposePopup);

        panel.add(new JButton("Добавить") {{
            setBounds(5, 220, 115, 30);
            addActionListener(e -> addIncome());
        }});

        panel.add(new JButton("Отмена") {{
            setBounds(125, 220, 115, 30);
            addActionListener(e -> hideFrame());
        }});
    }

    private void addIncome() {
        disposePopup();
        if (StringUtils.isBlank(amountCountText.getText())) {
            amountCountErrorPopup = FrameService.createErrorDialog("Необходимо указать сумму", amountCountText);
        } else {
            ApplicationService.getInstance().addIncome(new Income(
                    (Date) dateText.getValue(),
                    Integer.parseInt(amountCountText.getText()),
                    (Users) userSelect.getSelectedItem(),
                    (IncomeType) incomeTypeSelect.getSelectedItem(),
                    textDescription.getText()));
            hideFrame();
        }
    }

    private void disposePopup() {
        if (amountCountErrorPopup != null)
            amountCountErrorPopup.closeDialog();
    }

    void showFrame() {
        textDescription.setText("");
        setVisible(true);
    }

    private void hideFrame() {
        disposePopup();
        setVisible(false);
    }

    /**
     * Добавляет переданное значение нового типа в список типов.
     * По классу определяет в какой список добавить.
     * Устанавлмвает значение <code>item</code> выбраным в записанном списке.
     *
     * @param item      новое значение
     */
    void addSelectElement(Object item) {
        if (item instanceof Users) {
            userSelect.addItem((Users) item);
            userSelect.setSelectedItem(item);
        } else if (item instanceof IncomeType) {
            incomeTypeSelect.addItem((IncomeType) item);
            incomeTypeSelect.setSelectedItem(item);
        }
    }

    public void removeSelectElement(Object item) {
        if (item instanceof IncomeType) {
            incomeTypeSelect.removeItem(item);
        } else if (item instanceof Users) {
            userSelect.removeItem(item);
        }
    }
}

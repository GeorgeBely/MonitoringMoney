package ru.MonitoringMoney.frame;


import org.apache.commons.lang.StringUtils;
import ru.MonitoringMoney.*;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.types.*;
import ru.mangeorge.swing.graphics.PopupDialog;
import ru.mangeorge.awt.service.CalendarService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.text.ParseException;
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
    private PopupDialog importancePopup;
    private PopupDialog payTypePopup;
    private PopupDialog userPopup;
    private PopupDialog pricePopup;


    public AddFrame() {
        setLocation(ApplicationService.getInstance().getWindowLocation(AddFrame.class));
        setSize(ApplicationService.getInstance().getWindowSize(AddFrame.class));
        setResizable(false);
        setTitle(FRAME_NAME);
        setIconImage(ImageService.getPlusImage());
        toFront();
        addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) { }
            public void componentMoved(ComponentEvent e) { disposePopup(); }
            public void componentShown(ComponentEvent e) { }
            public void componentHidden(ComponentEvent e) {
                ApplicationService.getInstance().updateSizeWindow(AddFrame.class, getSize());
                ApplicationService.getInstance().updateLocationWindow(AddFrame.class, getLocation());
                disposePopup();
            }
        });

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
            addMouseListener(createPopupCloseMouseListener());
        }};
        add(panel);

        importanceSelect = new JComboBox<ImportanceType>() {{
            setModel(new DefaultComboBoxModel<>(ApplicationService.getInstance().getSortedImportance()));
            if (getModel().getSize() == 2)
                setSelectedIndex(1);
            setBounds(5, 5, 200, 30);
            addMouseListener(createPopupCloseMouseListener());
        }};
        panel.add(importanceSelect);

        JButton importanceButton = new JButton() {{
            setBounds(210, 5, 30, 30);
            setBorder(null);
            addActionListener(e -> {
                disposePopup();
                new FrameAddPropertyValues(ImportanceType.class);
            });
            setIcon(ImageService.getPlusButtonIcon());
        }};
        panel.add(importanceButton);

        payTypeSelect = new JComboBox<PayType>() {{
            setModel(new DefaultComboBoxModel<>(ApplicationService.getInstance().getSortedPayTypes()));
            if (getModel().getSize() == 2)
                setSelectedIndex(1);
            setBounds(5, 40, 200, 30);
            addMouseListener(createPopupCloseMouseListener());
        }};
        panel.add(payTypeSelect);

        JButton payTypeButton = new JButton() {{
            setBounds(210, 40, 30, 30);
            setBorder(null);
            addActionListener(e -> {
                disposePopup();
                new FrameAddPropertyValues(PayType.class);
            });
            setIcon(ImageService.getPlusButtonIcon());
        }};
        panel.add(payTypeButton);

        JLabel priceLabel = new JLabel("Стоимость покупки") {{
            setBounds(5, 75, 140, 20);
        }};
        panel.add(priceLabel);

        priceText = new JTextField() {{
            setBounds(145, 75, 90, 20);
            addKeyListener(new KeyListener() {
                public void keyTyped(KeyEvent e) { }
                public void keyPressed(KeyEvent e) { }
                public void keyReleased(KeyEvent e) {
                    priceText.setText(priceText.getText().replaceAll("[^0-9]", ""));
                }
            });
            addMouseListener(createPopupCloseMouseListener());
        }};
        panel.add(priceText);

        JLabel labelFromDate = new JLabel("Дата покупки") {{
            setBounds(5, 100, 140, 20);
        }};
        panel.add(labelFromDate);

        dateText = new JFormattedTextField(ApplicationProperties.FORMAT_DATE) {{
            setBounds(145, 100, 90, 20);
            setValue(new Date());
            addMouseListener(new MouseListener() {
                public void mouseReleased(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseClicked(MouseEvent e) {}
                public void mousePressed(MouseEvent e) {
                    disposePopup();
                    try { CalendarService.addPopupCalendarDialog(dateText, ApplicationProperties.FORMAT_DATE, null); } catch (ParseException ignore) { }
                }
            });
        }};
        panel.add(dateText);

        textDescription = new JTextArea() {{
            setLineWrap(true);
            setWrapStyleWord(true);
            addMouseListener(createPopupCloseMouseListener());
        }};

        JScrollPane textScrollPane = new JScrollPane() {{
            setViewportView(textDescription);
            setBounds(5, 130, 235, 80);
        }};
        panel.add(textScrollPane);

        userSelect = new JComboBox<Users>() {{
            setModel(new DefaultComboBoxModel<>(ApplicationService.getInstance().getSortedUsers()));
            if (getModel().getSize() == 2)
                setSelectedIndex(1);
            setBounds(5, 220, 200, 30);
            addActionListener(e -> disposePopup());
        }};
        panel.add(userSelect);

        JButton userButton = new JButton() {{
            setBorder(null);
            setBounds(210, 220, 30, 30);
            addActionListener(e -> {
                disposePopup();
                new FrameAddPropertyValues(Users.class);
            });
            setIcon(ImageService.getPlusButtonIcon());
        }};
        panel.add(userButton);

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


    public void addPayObject() {
        boolean checkParams = true;
        disposePopup();
        if (ApplicationProperties.EMPTY.equals(((ImportanceType) importanceSelect.getSelectedItem()).getCode())) {
            JLabel label = new JLabel("<html><font color=\"red\">Необходимо выбрать уровень важности</font></html>") {{
                setBounds(10, 0, 260, 30);
            }};
            importancePopup = new PopupDialog(importanceSelect, new Dimension(270, 40), new Component[]{label}, true, false);

            checkParams = false;
        }

        if (ApplicationProperties.EMPTY.equals(((PayType) payTypeSelect.getSelectedItem()).getCode())) {
            JLabel label = new JLabel("<html><font color=\"red\">Необходимо выбрать тип покупки</font></html>") {{
                setBounds(10, 0, 240, 30);
            }};
            payTypePopup = new PopupDialog(payTypeSelect, new Dimension(250, 40), new Component[]{label}, true, false);

            checkParams = false;
        }

        if (ApplicationProperties.EMPTY.equals(((Users) userSelect.getSelectedItem()).getCode())) {
            JLabel label = new JLabel("<html><font color=\"red\">Необходимо выбрать пользователя</font></html>") {{
                setBounds(10, 0, 240, 30);
            }};
            userPopup = new PopupDialog(userSelect, new Dimension(250, 40), new Component[]{label}, true, false);

            checkParams = false;
        }

        if (StringUtils.isBlank(priceText.getText())) {
            JLabel label = new JLabel("<html><font color=\"red\">Необходимо указать цену</font></html>") {{
                setBounds(10, 0, 190, 30);
            }};
            pricePopup = new PopupDialog(priceText, new Dimension(200, 40), new Component[]{label}, true, false);

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

    private MouseListener createPopupCloseMouseListener() {
        return new MouseListener() {
            public void mouseReleased(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                disposePopup();
            }
        };
    }

    /**
     * Добавляет переданное значение нового типа покупки в список типов.
     * По классу определяет в какой список добавить.
     * Устанавлмвает значение <code>item</code> выбраным в записанном списке.
     *
     * @param item      новое значение
     */
    public void addSelectElement(Object item) {
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

    public void showFrame() {
        textDescription.setText("");
        setVisible(true);
    }

    public void hideFrame() {
        disposePopup();
        setVisible(false);
    }

    public void disposePopup() {
        if (importancePopup != null) {
            importancePopup.closeDialog();
        }
        if (payTypePopup != null) {
            payTypePopup.closeDialog();
        }
        if (userPopup != null) {
            userPopup.closeDialog();
        }
        if (pricePopup != null) {
            pricePopup.closeDialog();
        }
    }
}

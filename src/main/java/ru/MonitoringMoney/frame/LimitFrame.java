package ru.MonitoringMoney.frame;


import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.FrameService;
import ru.MonitoringMoney.services.ImageService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LimitFrame extends JFrame {

    /**
     * Заголовок фрейма
     */
    private static final String FRAME_NAME = "Добавить лимит";


    private static final String TERM_INPUT_DEFAULT_TEXT = "Введите название";

    private JPanel panel;

    LimitFrame() {
        setLocation(ApplicationService.getInstance().getWindowLocation(LimitFrame.class));
        setSize(ApplicationService.getInstance().getWindowSize(LimitFrame.class));
        setResizable(false);
        setVisible(true);
        setTitle(FRAME_NAME);
        setIconImage(ImageService.getLimitImage());
        addComponentListener(FrameService.addComponentListener(LimitFrame.class, getSize(), getLocation(), () -> {}));

        panel = new JPanel(){{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);


        JTextField termInput = new JTextField() {{
            setBounds(5, 5, 235, 30);
            setText(TERM_INPUT_DEFAULT_TEXT);
            setDisabledTextColor(Color.LIGHT_GRAY);
            setSelectedTextColor(Color.LIGHT_GRAY);
            setSelectionColor(Color.LIGHT_GRAY);
            addMouseListener(FrameService.createMouseListener(() -> {
                if (TERM_INPUT_DEFAULT_TEXT.equals(getText())) {
                    setText("");
                }
            }));
            addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) { }
                public void focusLost(FocusEvent e) {
                    if ("".equals(getText()))
                        setText(TERM_INPUT_DEFAULT_TEXT);
                }
            });
        }};
        panel.add(termInput);

        JButton okButton = new JButton("Добавить") {{
            setBounds(5, 255, 115, 30);
            addActionListener(e -> addLimit());
        }};
        panel.add(okButton);

        JButton cancelButton = new JButton("Отмена") {{
            setBounds(125, 255, 115, 30);
            addActionListener(e -> dispose());
        }};
        panel.add(cancelButton);
    }

    private void addLimit() {

    }

}

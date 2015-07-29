package ru.MonitoringMoney.frame;


import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

public class TextAreaFrame extends JFrame {


    /** Ширина фрейма */
    private static final int FRAME_WIDTH = 250;

    /** Высота фрейма */
    private static final int FRAME_HEIGHT = 235;

    private static final String FRAME_NAME = "Редактирование описания";


    private JTextArea textArea;


    public TextAreaFrame(JTextField textField) throws ParseException {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - FRAME_WIDTH / 2, screenSize.height / 2 - FRAME_HEIGHT / 2);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        setVisible(true);
        setTitle(FRAME_NAME);

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);

        textArea = new JTextArea() {{
            setText(textField.getText());
            setLineWrap(true);
            setWrapStyleWord(true);
        }};
        JScrollPane textScrollPane = new JScrollPane() {{
            setViewportView(textArea);
            setBounds(5, 5, 235, 160);
        }};
        panel.add(textScrollPane);

        JButton okButton = new JButton("Применить") {{
            setBounds(5, 170, 115, 30);
            addActionListener(e -> {
                textField.setText(textArea.getText());
                dispose();
            });
        }};
        panel.add(okButton);

        JButton cancelButton = new JButton("Отмена") {{
            setBounds(125, 170, 115, 30);
            addActionListener(e -> dispose());
        }};
        panel.add(cancelButton);
    }
}

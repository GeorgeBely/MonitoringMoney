package ru.MonitoringMoney.services;


import ru.MonitoringMoney.ApplicationProperties;
import ru.mangeorge.awt.JButtonCellRenderer;
import ru.mangeorge.awt.service.CalendarService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;

public class FrameService {

    public static void addRemoveColumnView(JTable table) {
        table.getColumn(TableService.REMOVE_COLUMN).setMinWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMaxWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMinWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMaxWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setCellEditor(TableService.getJButtonCellEditor());
        table.getColumn(TableService.REMOVE_COLUMN).setCellRenderer(new JButtonCellRenderer(ImageService.getRemoveButtonIcon()));
    }

    public static MouseListener getMouseListenerPopupCalendarDialog(JFormattedTextField dateText, ClickFunction clickFunction) {
        return new MouseListener() {
            public void mouseReleased(MouseEvent e) { }
            public void mouseExited(MouseEvent e) { }
            public void mouseEntered(MouseEvent e) { }
            public void mouseClicked(MouseEvent e) { }
            public void mousePressed(MouseEvent e) {
                clickFunction.click();
                try {
                    CalendarService.addPopupCalendarDialog(dateText, ApplicationProperties.FORMAT_DATE, null);
                } catch (ParseException ignore) { }
            }
        };
    }

    /**
     * Создаёт компонент текст и добавляет его на панель
     *
     * @param panel          панель
     * @param bounds         размер и положение компонента
     * @param clickFunction  функция обрабатывающая нажатие кнопки
     * @return компонент текст
     */
    public static JTextArea createJTextArea(JPanel panel, Rectangle bounds, ClickFunction clickFunction) {
        JTextArea text = new JTextArea() {{
            setLineWrap(true);
            setWrapStyleWord(true);
            addMouseListener(createPopupCloseMouseListener(clickFunction));
        }};

        JScrollPane textScrollPane = new JScrollPane() {{
            setViewportView(text);
            setBounds(bounds);
        }};
        panel.add(textScrollPane);

        return text;
    }

    public static KeyListener createPriceKeyListener(JTextField priceField) {
        return new KeyListener() {
            public void keyTyped(KeyEvent e) { }
            public void keyPressed(KeyEvent e) { }
            public void keyReleased(KeyEvent e) {
                if (!priceField.getText().matches("([0-9]*)"))
                    priceField.setText(priceField.getText().replaceAll("[^0-9]", ""));
            }
        };
    }

    /** @return Событие на нажатие кнопки. Закрывает все всплывающие окна */
    public static MouseListener createPopupCloseMouseListener(ClickFunction clickFunction) {
        return new MouseListener() {
            public void mouseReleased(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                clickFunction.click();
            }
        };
    }


    public interface ClickFunction {
        void click();
    }
}

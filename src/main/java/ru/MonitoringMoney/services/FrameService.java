package ru.MonitoringMoney.services;


import ru.MonitoringMoney.main.ApplicationProperties;
import ru.MonitoringMoney.types.*;
import ru.mangeorge.awt.JButtonCellRenderer;
import ru.mangeorge.awt.service.CalendarService;
import ru.mangeorge.swing.graphics.PopupDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;


public class FrameService {

    public static void addRemoveColumnView(JTable table) {
        table.getColumn(TableService.REMOVE_COLUMN).setMinWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMaxWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMinWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMaxWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setCellEditor(TableService.getJButtonCellEditor());
        table.getColumn(TableService.REMOVE_COLUMN).setCellRenderer(new JButtonCellRenderer(ImageService.REMOVE_ICON));
    }

    public static ComponentListener addComponentListener(Class clazz, Dimension size, Point location, ClickFunction hideFunction) {
        return addComponentListener(clazz, size, location, hideFunction, () -> {});
    }

    public static ComponentListener addComponentListener(Class clazz, Dimension size, Point location, ClickFunction hideFunction, ClickFunction resizedFunction) {
        return new ComponentListener() {
            public void componentResized(ComponentEvent e) { resizedFunction.click(); }
            public void componentMoved(ComponentEvent e) { }
            public void componentShown(ComponentEvent e) { }
            public void componentHidden(ComponentEvent e) {
                ApplicationService.getInstance().updateSizeWindow(clazz, size);
                ApplicationService.getInstance().updateLocationWindow(clazz, location);
                hideFunction.click();
            }
        };
    }

    public static MouseListener getMouseListenerPopupCalendarDialog(JFormattedTextField dateText,
                                                                    CalendarService.DateFunction dateFunction,
                                                                    ClickFunction clickFunction) {
        return new MouseListener() {
            public void mouseReleased(MouseEvent e) { }
            public void mouseExited(MouseEvent e) { }
            public void mouseEntered(MouseEvent e) { }
            public void mouseClicked(MouseEvent e) { }
            public void mousePressed(MouseEvent e) {
                clickFunction.click();
                try {
                    CalendarService.addPopupCalendarDialog(dateText, ApplicationProperties.FORMAT_DATE, dateFunction);
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
            addMouseListener(createMouseListener(clickFunction));
        }};

        JScrollPane textScrollPane = new JScrollPane() {{
            setViewportView(text);
            setBounds(bounds);
        }};
        panel.add(textScrollPane);

        return text;
    }

    public static KeyListener createPriceKeyListener(JTextField priceField, ClickFunction function) {
        return new KeyListener() {
            public void keyTyped(KeyEvent e) { }
            public void keyPressed(KeyEvent e) { }
            public void keyReleased(KeyEvent e) {
                if (!priceField.getText().matches("([0-9]*)"))
                    priceField.setText(priceField.getText().replaceAll("[^0-9]", ""));

                function.click();
            }
        };
    }

    /** @return Событие на нажатие кнопки. */
    public static MouseListener createMouseListener(ClickFunction clickFunction) {
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

    /**
     * Создаёт выпадающий список для определённого типа и добавляет его на панель
     *
     * @param panel   панель
     * @param bounds  размеры списка и расположение
     * @param values  набор значений
     * @param <T>     тип списка
     * @return сформированные компонент список, с кнопкой добавления нового значения
     */
    public static <T> JComboBox<T> createSelectTypeValue(JPanel panel, Rectangle bounds, T[] values,
                                                   FrameService.ClickFunction addFunction, FrameService.ClickFunction selectFunction) {
        JButton addButton = new JButton() {{
            setBounds((int) (bounds.getX() + bounds.getWidth()) + 5, (int) bounds.getY(), 30, 30);
            setBorder(null);
            addActionListener(e -> addFunction.click());
            setIcon(ImageService.PLUS_ICON);
        }};
        panel.add(addButton);

        JComboBox<T> select =  new JComboBox<T>() {{
            setModel(new DefaultComboBoxModel<>(values));
            if (getModel().getSize() == 2)
                setSelectedIndex(1);
            setBounds(bounds);
            addMouseListener(FrameService.createMouseListener(selectFunction));
        }};
        panel.add(select);

        return select;
    }

    /**
     * Создаёт и добавляет на пнель {panel} выподающий список с множественным выбором
     *
     * @param panel   панель
     * @param bounds  размеры списка и расположение
     * @param values  набор значений
     * @param <T>     тип списка
     * @return сформированные компонент список с множественным выбором
     */
    public static  <T extends TypeValue> JComboBox<CheckBoxListService.CheckComboValue> createMultiSelectType(JPanel panel, T[] values, Rectangle bounds) {
        JComboBox<CheckBoxListService.CheckComboValue> select = new JComboBox<CheckBoxListService.CheckComboValue>() {
            public void setPopupVisible(boolean v) { }
            {
                setModel(CheckBoxListService.getModel(values));
                setBounds(bounds);
                setRenderer(new CheckBoxListService.CheckComboRenderer());
                addActionListener(new CheckBoxListService.CheckBoxList());
            }
        };
        panel.add(select);

        return select;
    }

    /**
     * Создаёт таблицу для определённого типа и добавляет её на панель
     *
     * @param panel   панель
     * @param bonds   размеры списка и расположение
     * @param <T>     тип списка
     * @return сформированная таблица, с кнопкой добавления нового значения
     */
    public static <T extends TypeValue> JTable createJTableTypeValue(JPanel panel, Rectangle bonds, Class<T> clazz) {
        JTable table = new JTable();
        JScrollPane editScrollPane = new JScrollPane() {{
            setViewportView(table);
            setBounds(bonds);
        }};

        JButton button = new JButton("Добавить") {{
            setBounds((int) bonds.getX() + 25, (int) (bonds.getHeight() + bonds.getY()) + 5, (int) bonds.getWidth() - 50, 30);
            addActionListener(e -> {
                try {
                    T type = clazz.newInstance();
                    ((DefaultTableModel) table.getModel()).addRow(new Object[]{"", type});
                } catch (Exception ignore) {}
            });
            setIcon(ImageService.PLUS_ICON);
        }};
        panel.add(button);

        panel.add(editScrollPane);

        table.setModel(TableService.getTypeValueTableData(clazz));
        FrameService.addRemoveColumnView(table);

        return table;
    }

    public static PopupDialog createErrorDialog(String title, JComponent select) {
        JLabel label = new JLabel("<html><font color=\"red\">" + title + "</font></html>") {{
            setBounds(10, 0, title.length() * 8, 30);
        }};
        return new PopupDialog(select, new Dimension(title.length() * 8, 40), new Component[]{label}, true, false);
    }

    public interface ClickFunction {
        void click();
    }
}

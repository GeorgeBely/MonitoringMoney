package ru.MonitoringMoney.services;


import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.frame.PopupDialog;
import ru.MonitoringMoney.main.MonitoringMoney;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

public class TableService {

    public static final String USER_COLUMN = "Платильщик";
    public static final String IMPORTANCE_COLUMN = "Важность";
    public static final String PAY_TYPE_COLUMN = "Тип покупки";
    public static final String PRICE_COLUMN = "Стоимость";
    public static final String DATE_COLUMN = "Дата";
    public static final String DESCRIPTION_COLUMN = "Описание";
    public static final String REMOVE_COLUMN = "X";


    public static DefaultTableModel getTableData() {
        DefaultTableModel dm = new DefaultTableModel();
        Object[] header = new Object[]{USER_COLUMN, IMPORTANCE_COLUMN, PAY_TYPE_COLUMN, PRICE_COLUMN, DATE_COLUMN, DESCRIPTION_COLUMN, REMOVE_COLUMN};
        List<PayObject> payObjects = ApplicationService.getPayObjects();
        Object[][] data = new Object[payObjects.size()][7];

        int index = 0;
        for (PayObject payObject : payObjects) {
            data[index][0] = payObject.getUser();
            data[index][1] = payObject.getImportance();
            data[index][2] = payObject.getPayType();
            data[index][3] = payObject.getPrice();
            data[index][4] = payObject.getDate();
            data[index][5] = payObject.getDescription();
            data[index][6] = payObject;

            index++;
        }
        dm.setDataVector(data, header);

        return dm;
    }


    public static class SelectCellEditor extends DefaultCellEditor {
        public SelectCellEditor(JComboBox<Object> comboBox, String columnName) {
            super(comboBox);
            if (TableService.IMPORTANCE_COLUMN.equals(columnName)) {
                comboBox.setModel(new DefaultComboBoxModel<>(ApplicationService.getInstance().importanceTypes.toArray()));
            } else if (TableService.PAY_TYPE_COLUMN.equals(columnName)) {
                comboBox.setModel(new DefaultComboBoxModel<>(ApplicationService.getInstance().payTypes.toArray()));
            } else if (TableService.USER_COLUMN.equals(columnName)) {
                comboBox.setModel(new DefaultComboBoxModel<>(ApplicationService.getInstance().users.toArray()));
            }
            comboBox.addActionListener(e -> fireEditingStopped());
        }
    }

    public static class DateCellEditor extends DefaultCellEditor {

        public JTextField textField;
        private DateFormat dateFormat;

        public DateCellEditor(JTextField textField, DateFormat dateFormat) {
            super(textField);
            textField.addMouseListener(new MouseListener() {
                public void mouseReleased(MouseEvent e) { }
                public void mouseExited(MouseEvent e) { }
                public void mouseEntered(MouseEvent e) { }
                public void mouseClicked(MouseEvent e) { }
                public void mousePressed(MouseEvent e) {
                    try { CalendarService.addPopupCalendarDialog(textField, CalendarService.TABLE_EDIT_CALENDAR_ACTION); } catch (ParseException ignore) { }
                }
            });
            textField.setEditable(false);
            this.dateFormat = dateFormat;
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            textField = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
            if (value instanceof Date)
                textField.setText(dateFormat.format((Date) value));
            return textField;
        }
    }

    public static class DateCellRenderer extends DefaultTableCellRenderer {

        private DateFormat dateFormat;

        public DateCellRenderer(DateFormat dateFormat) {
            super();
            this.dateFormat = dateFormat;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Date)
                ((DateCellRenderer) component).setText(dateFormat.format((Date) value));
            return component;
        }
    }

    public static class TextAreaCellEditor extends DefaultCellEditor {

        public TextAreaCellEditor(JTextField textField) {
            super(textField);
            textField.addMouseListener(new MouseListener() {
                public void mouseReleased(MouseEvent e) { }
                public void mouseExited(MouseEvent e) { }
                public void mouseEntered(MouseEvent e) { }
                public void mouseClicked(MouseEvent e) { }
                public void mousePressed(MouseEvent e) {

                    JTextArea textArea = new JTextArea() {{
                        setLineWrap(true);
                        setWrapStyleWord(true);
                        setText(textField.getText());
                    }};
                    textArea.addKeyListener(new KeyListener() {
                        public void keyTyped(KeyEvent e) { }
                        public void keyPressed(KeyEvent e) { }
                        public void keyReleased(KeyEvent e) {
                            textField.setText(textArea.getText());
                        }
                    });
                    JScrollPane textScrollPane = new JScrollPane() {{
                        setViewportView(textArea);
                        setBounds(10, 5, 279, 90);
                    }};

                    new PopupDialog(textField, new Dimension(300, 110), new Component[]{textScrollPane}, false, true);
                }
            });
            textField.setEditable(false);
        }
    }


    public static class ButtonCellRenderer extends JButton implements TableCellRenderer {

        public ButtonCellRenderer() {
            setOpaque(true);
            setIcon(ImageService.getRemoveButtonIcon());
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    public static class RemoveButtonCellEditor extends DefaultCellEditor {

        protected JButton button;
        private Object value;
        private JTable table;
        private int row;
        private int column;

        public RemoveButtonCellEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
            button.setIcon(ImageService.getRemoveButtonIcon());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.value = value;
            this.table = table;
            this.row = row;
            this.column = column;
            return button;
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            MonitoringMoney.mainFrame.editFrame.removeItemList.add((PayObject) value);
            ((DefaultTableModel) table.getModel()).removeRow(row);
            return true;
        }

        public Object getCellEditorValue() {
            if (((DefaultTableModel) table.getModel()).getDataVector().size() > row) {
                return ((Vector) ((DefaultTableModel) table.getModel()).getDataVector().get(row)).get(column);
            }
            return null;
        }
    }
}




package ru.MonitoringMoney.frame;

import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.services.TableService;
import ru.MonitoringMoney.types.ImportanceType;
import ru.MonitoringMoney.types.PayType;
import ru.MonitoringMoney.types.Users;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * Фрейм для редактирования покупок
 */
public class EditFrame extends JFrame implements Serializable {

    public static final long serialVersionUID = 1783363045918964188L;

    /**
     * Заголовок фрейма
     */
    private static final String FRAME_NAME = "Редактирование покупок";


    private JTable editPayObjectTable;
    private JLabel editPayObjectLabel;
    private JLabel editTypeValueLabel;
    private JButton editTypeValueButton;
    private JButton editPayObjectButton;
    private JScrollPane editPayObjectScrollPane;

    public List<PayObject> removeItemList = new ArrayList<>();


    public EditFrame() {
        setLocation(ApplicationService.getInstance().getWindowLocation(EditFrame.class));
        setSize(ApplicationService.getInstance().getWindowSize(EditFrame.class));
        setResizable(false);
        setVisible(true);
        setTitle(FRAME_NAME);
        setIconImage(ImageService.getEditImage());
        addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) { }
            public void componentMoved(ComponentEvent e) { }
            public void componentShown(ComponentEvent e) { }
            public void componentHidden(ComponentEvent e) {
                ApplicationService.getInstance().updateSizeWindow(EditFrame.class, getSize());
                ApplicationService.getInstance().updateLocationWindow(EditFrame.class, getLocation());
            }
        });

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);


        editPayObjectLabel = new JLabel("Редактор покупок") {{
            setBounds(161, 0, 200, 30);
        }};
        panel.add(editPayObjectLabel);

        editPayObjectButton = new JButton("Редактор покупок") {{
            setBounds(128, 0, 170, 30);
            setVisible(false);
            addActionListener(e -> {
                setVisible(false);
                editTypeValueButton.setVisible(true);
                editPayObjectLabel.setVisible(true);
                editTypeValueLabel.setVisible(false);
                editPayObjectScrollPane.setVisible(true);
            });
        }};
        panel.add(editPayObjectButton);


        editTypeValueLabel = new JLabel("Редактирование типов") {{
            setBounds(400, 0, 200, 30);
            setVisible(false);
        }};
        panel.add(editTypeValueLabel);


        editTypeValueButton = new JButton("Редактирование типов") {{
            setBounds(381, 0, 170, 30);
            addActionListener(e -> {
                setVisible(false);
                editPayObjectButton.setVisible(true);
                editPayObjectLabel.setVisible(false);
                editTypeValueLabel.setVisible(true);
                editPayObjectScrollPane.setVisible(false);
            });
        }};
        panel.add(editTypeValueButton);


        editPayObjectTable = new JTable();
        updateTable();
        editPayObjectScrollPane = new JScrollPane() {{
            setViewportView(editPayObjectTable);
            setBounds(5, 30, 645, 290);
        }};
        panel.add(editPayObjectScrollPane);

        JButton okButton = new JButton("Применить") {{
            setBounds(45, 325, 115, 30);
            addActionListener(e -> {
                updateData();
                dispose();
            });
        }};
        panel.add(okButton);

        JButton cancelButton = new JButton("Отмена") {{
            setBounds(500, 325, 115, 30);
            addActionListener(e -> dispose());
        }};
        panel.add(cancelButton);
    }

    public void updateData() {
        ApplicationService.getInstance().payObjects.removeAll(removeItemList);
        removeItemList.clear();
        for (Object obj : ((DefaultTableModel) editPayObjectTable.getModel()).getDataVector()) {
            Vector vector = (Vector) obj;
            PayObject payObject = (PayObject) vector.get(6);
            payObject.setUser((Users) vector.get(0));
            payObject.setImportance((ImportanceType) vector.get(1));
            payObject.setPayType((PayType) vector.get(2));
            if (vector.get(3) instanceof String) {
                try {
                    payObject.setPrice(Integer.parseInt((String) vector.get(3)));
                } catch (NumberFormatException ignore) {}
            }
            if (vector.get(4) instanceof String) {
                try {
                    payObject.setDate(ApplicationService.FORMAT_DATE.parse((String) vector.get(4)));
                } catch (ParseException ignore) {}
            }
            payObject.setDescription((String) vector.get(5));
        }
        ApplicationService.getInstance().updateAllFrequencyUse();
        MonitoringMoney.mainFrame.refreshText();
        ApplicationService.writeData();
    }

    public void updateTable() {
        editPayObjectTable.setModel(TableService.getTableData());
        editPayObjectTable.getColumn(TableService.REMOVE_COLUMN).setMinWidth(20);
        editPayObjectTable.getColumn(TableService.REMOVE_COLUMN).setMaxWidth(20);
        editPayObjectTable.getColumn(TableService.REMOVE_COLUMN).setMinWidth(20);
        editPayObjectTable.getColumn(TableService.REMOVE_COLUMN).setMaxWidth(20);
        editPayObjectTable.getColumn(TableService.REMOVE_COLUMN).setCellEditor(new TableService.RemoveButtonCellEditor());
        editPayObjectTable.getColumn(TableService.REMOVE_COLUMN).setCellRenderer(new TableService.ButtonCellRenderer());
        editPayObjectTable.getColumn(TableService.DESCRIPTION_COLUMN).setCellEditor(new TableService.TextAreaCellEditor(new JTextField()));
        editPayObjectTable.getColumn(TableService.DATE_COLUMN).setCellRenderer(new TableService.DateCellRenderer(ApplicationService.FORMAT_DATE));
        editPayObjectTable.getColumn(TableService.DATE_COLUMN).setCellEditor(new TableService.DateCellEditor(new JTextField(), ApplicationService.FORMAT_DATE));
        editPayObjectTable.getColumn(TableService.USER_COLUMN).setCellEditor(new TableService.SelectCellEditor(new JComboBox<>(), TableService.USER_COLUMN));
        editPayObjectTable.getColumn(TableService.IMPORTANCE_COLUMN).setCellEditor(new TableService.SelectCellEditor(new JComboBox<>(), TableService.IMPORTANCE_COLUMN));
        editPayObjectTable.getColumn(TableService.PAY_TYPE_COLUMN).setCellEditor(new TableService.SelectCellEditor(new JComboBox<>(), TableService.PAY_TYPE_COLUMN));
    }

}
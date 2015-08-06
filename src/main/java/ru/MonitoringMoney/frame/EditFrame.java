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
import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class EditFrame extends JFrame {

    /**
     * Ширина фрейма
     */
    private static final int FRAME_WIDTH = 660;

    /**
     * Высота фрейма
     */
    private static final int FRAME_HEIGHT = 390;

    /**
     * Заголовок фрейма
     */
    private static final String FRAME_NAME = "Редактирование покупок";


    private JTable table;

    public List<PayObject> removeItemList = new ArrayList<>();


    public EditFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - FRAME_WIDTH / 2, screenSize.height / 2 - FRAME_HEIGHT / 2);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        setVisible(true);
        setTitle(FRAME_NAME);
        setIconImage(ImageService.getEditImage());

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);


        JLabel infoLabel = new JLabel("Редактор покупок") {{
            setBounds(300, 0, 500, 30);
        }};
        panel.add(infoLabel);

        table = new JTable();
        updateTable();
        JScrollPane textScrollPane = new JScrollPane() {{
            setViewportView(table);
            setBounds(5, 30, 645, 290);
        }};
        panel.add(textScrollPane);

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
        for (Object obj : ((DefaultTableModel) table.getModel()).getDataVector()) {
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
        MonitoringMoney.mainFrame.refreshText();
        ApplicationService.writeData();
    }

    public void updateTable() {
        table.setModel(TableService.getTableData());
        table.getColumn(TableService.REMOVE_COLUMN).setMinWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMaxWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMinWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMaxWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setCellEditor(new TableService.RemoveButtonCellEditor());
        table.getColumn(TableService.REMOVE_COLUMN).setCellRenderer(new TableService.ButtonCellRenderer());
        table.getColumn(TableService.DESCRIPTION_COLUMN).setCellEditor(new TableService.TextAreaCellEditor(new JTextField()));
        table.getColumn(TableService.DATE_COLUMN).setCellRenderer(new TableService.DateCellRenderer(ApplicationService.FORMAT_DATE));
        table.getColumn(TableService.DATE_COLUMN).setCellEditor(new TableService.DateCellEditor(new JTextField(), ApplicationService.FORMAT_DATE));
        table.getColumn(TableService.USER_COLUMN).setCellEditor(new TableService.SelectCellEditor(new JComboBox<>(), TableService.USER_COLUMN));
        table.getColumn(TableService.IMPORTANCE_COLUMN).setCellEditor(new TableService.SelectCellEditor(new JComboBox<>(), TableService.IMPORTANCE_COLUMN));
        table.getColumn(TableService.PAY_TYPE_COLUMN).setCellEditor(new TableService.SelectCellEditor(new JComboBox<>(), TableService.PAY_TYPE_COLUMN));
    }

}
package ru.MonitoringMoney.frame;

import ru.MonitoringMoney.ApplicationProperties;
import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.services.TableService;
import ru.MonitoringMoney.types.*;
import ru.mangeorge.awt.*;
import ru.mangeorge.awt.service.JTableService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.Serializable;
import java.text.ParseException;
import java.util.*;
import java.util.List;


/**
 * Фрейм для редактирования покупок
 */
public class EditFrame extends JFrame implements Serializable {

    private static final long serialVersionUID = 1783363045918964188L;

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
    private JTable editImportanceTable;
    private JTable editPayTypeTable;
    private JTable editUserTable;
    private Container editTypeValueContainer;

    public List<PayObject> removePayObjectList = new ArrayList<>();
    public List<ImportanceType> removeImportanceList = new ArrayList<>();
    public List<PayType> removePayTypeList = new ArrayList<>();
    public List<Users> removeUserList = new ArrayList<>();


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
                editTypeValueContainer.setVisible(false);
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
                editTypeValueContainer.setVisible(true);
            });
        }};
        panel.add(editTypeValueButton);


        editPayObjectTable = new JTable();
        updatePayObjectTable();
        editPayObjectScrollPane = new JScrollPane() {{
            setViewportView(editPayObjectTable);
            setBounds(5, 30, 645, 290);
        }};
        panel.add(editPayObjectScrollPane);

        editTypeValueContainer = new Container() {{
            setBounds(5, 30, 645, 290);
            setVisible(false);
        }};
        panel.add(editTypeValueContainer);

        JPanel editTypeValuePanel = new JPanel() {{
            setBounds(0, 0, 645, 290);
            setFocusable(true);
            setLayout(null);
        }};
        editTypeValueContainer.add(editTypeValuePanel);

        editImportanceTable = new JTable();
        JScrollPane editImportanceScrollPane = new JScrollPane() {{
            setViewportView(editImportanceTable);
            setBounds(5, 5, 200, 240);
        }};
        editTypeValuePanel.add(editImportanceScrollPane);

        JButton importanceButton = new JButton("Добавить") {{
            setBounds(30, 250, 150, 30);
            addActionListener(e -> {
                ImportanceType type = new ImportanceType(ApplicationService.getInstance().getNewUniqueCode(), "");
                ((DefaultTableModel) editImportanceTable.getModel()).addRow(new Object[]{"", type});
            });
            setIcon(ImageService.getPlusButtonIcon());
        }};
        editTypeValuePanel.add(importanceButton);

        editPayTypeTable = new JTable();
        JScrollPane editPayTypeScrollPane = new JScrollPane() {{
            setViewportView(editPayTypeTable);
            setBounds(225, 5, 200, 240);
        }};
        editTypeValuePanel.add(editPayTypeScrollPane);

        JButton payTypeButton = new JButton("Добавить") {{
            setBounds(250, 250, 150, 30);
            addActionListener(e -> {
                PayType type = new PayType(ApplicationService.getInstance().getNewUniqueCode(), "");
                ((DefaultTableModel) editPayTypeTable.getModel()).addRow(new Object[]{"", type});
            });
            setIcon(ImageService.getPlusButtonIcon());
        }};
        editTypeValuePanel.add(payTypeButton);

        editUserTable = new JTable();
        JScrollPane editUserScrollPane = new JScrollPane() {{
            setViewportView(editUserTable);
            setBounds(445, 5, 200, 240);
        }};
        editTypeValuePanel.add(editUserScrollPane);
        updateTypeValueTable();

        JButton userButton = new JButton("Добавить") {{
            setBounds(470, 250, 150, 30);
            addActionListener(e -> {
                Users type = new Users(ApplicationService.getInstance().getNewUniqueCode(), "");
                ((DefaultTableModel) editUserTable.getModel()).addRow(new Object[]{"", type});
            });
            setIcon(ImageService.getPlusButtonIcon());
        }};
        editTypeValuePanel.add(userButton);

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
        ApplicationService.getInstance().payObjects.removeAll(removePayObjectList);
        removePayObjectList.clear();

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
                    payObject.setDate(ApplicationProperties.FORMAT_DATE.parse((String) vector.get(4)));
                } catch (ParseException ignore) {}
            }
            payObject.setDescription((String) vector.get(5));
        }

        for (Object obj : ((DefaultTableModel) editImportanceTable.getModel()).getDataVector()) {
            Vector vector = (Vector) obj;
            ImportanceType importanceType = (ImportanceType) vector.get(1);
            importanceType.setName((String) vector.get(0));
            if (!ApplicationService.getInstance().importanceTypes.contains(importanceType)) {
                ApplicationService.getInstance().importanceTypes.add(importanceType);
                MonitoringMoney.addFrame.addSelectElement(importanceType);
                MonitoringMoney.mainFrame.addSelectElement(importanceType);
            }
        }
        for (Object obj : ((DefaultTableModel) editPayTypeTable.getModel()).getDataVector()) {
            Vector vector = (Vector) obj;
            PayType payType = (PayType) vector.get(1);
            payType.setName((String) vector.get(0));
            if (!ApplicationService.getInstance().payTypes.contains(payType)) {
                ApplicationService.getInstance().payTypes.add(payType);
                MonitoringMoney.addFrame.addSelectElement(payType);
                MonitoringMoney.mainFrame.addSelectElement(payType);
            }
        }
        for (Object obj : ((DefaultTableModel) editUserTable.getModel()).getDataVector()) {
            Vector vector = (Vector) obj;
            Users user = (Users) vector.get(1);
            user.setName((String) vector.get(0));
            if (!ApplicationService.getInstance().users.contains(user)) {
                ApplicationService.getInstance().users.add(user);
                MonitoringMoney.addFrame.addSelectElement(user);
                MonitoringMoney.mainFrame.addSelectElement(user);
            }
        }

        Map<TypeValue, List<PayObject>> payObjectsUseRemoveTypeMap = new HashMap<>();
        for (PayObject payObject : ApplicationService.getInstance().payObjects) {
            if (removeImportanceList.contains(payObject.getImportance())) {
                if (payObjectsUseRemoveTypeMap.containsKey(payObject.getImportance())) {
                    payObjectsUseRemoveTypeMap.get(payObject.getImportance()).add(payObject);
                } else {
                    payObjectsUseRemoveTypeMap.put(payObject.getImportance(), new ArrayList<PayObject>(){{add(payObject);}});
                }
            }

            if (removeUserList.contains(payObject.getUser())) {
                if (payObjectsUseRemoveTypeMap.containsKey(payObject.getUser())) {
                    payObjectsUseRemoveTypeMap.get(payObject.getUser()).add(payObject);
                } else {
                    payObjectsUseRemoveTypeMap.put(payObject.getUser(), new ArrayList<PayObject>(){{add(payObject);}});
                }
            }

            if (removePayTypeList.contains(payObject.getPayType())) {
                if (payObjectsUseRemoveTypeMap.containsKey(payObject.getPayType())) {
                    payObjectsUseRemoveTypeMap.get(payObject.getPayType()).add(payObject);
                } else {
                    payObjectsUseRemoveTypeMap.put(payObject.getPayType(), new ArrayList<PayObject>(){{add(payObject);}});
                }
            }
        }

        removeImportanceList.stream()
                .filter(importanceType -> !payObjectsUseRemoveTypeMap.containsKey(importanceType))
                .filter(ApplicationService.getInstance().importanceTypes::contains)
                .forEach(value -> {
                    MonitoringMoney.addFrame.removeSelectElement(value);
                    MonitoringMoney.mainFrame.removeSelectElement(value);
                    ApplicationService.getInstance().importanceTypes.remove(value);
                });
        removePayTypeList.stream()
                .filter(payType -> !payObjectsUseRemoveTypeMap.containsKey(payType))
                .filter(ApplicationService.getInstance().payTypes::contains)
                .forEach(value -> {
                    MonitoringMoney.addFrame.removeSelectElement(value);
                    MonitoringMoney.mainFrame.removeSelectElement(value);
                    ApplicationService.getInstance().payTypes.remove(value);
                });
        removeUserList.stream()
                .filter(user -> !payObjectsUseRemoveTypeMap.containsKey(user))
                .filter(ApplicationService.getInstance().users::contains)
                .forEach(value -> {
                    MonitoringMoney.addFrame.removeSelectElement(value);
                    MonitoringMoney.mainFrame.removeSelectElement(value);
                    ApplicationService.getInstance().users.remove(value);
                });

        removePayTypeList.clear();
        removeUserList.clear();
        removeImportanceList.clear();

        ApplicationService.getInstance().updateAllFrequencyUse();
        MonitoringMoney.mainFrame.refreshText();
        ApplicationService.writeData();
    }

    public void updateTypeValueTable() {
        editImportanceTable.setModel(TableService.getTypeValueTableData(ImportanceType.class));
        editPayTypeTable.setModel(TableService.getTypeValueTableData(PayType.class));
        editUserTable.setModel(TableService.getTypeValueTableData(Users.class));
        addRemoveColumnView(editImportanceTable);
        addRemoveColumnView(editPayTypeTable);
        addRemoveColumnView(editUserTable);
    }

    public void updatePayObjectTable() {
        editPayObjectTable.setModel(TableService.getPayObjectTableData());
        addRemoveColumnView(editPayObjectTable);
        editPayObjectTable.getColumn(TableService.DESCRIPTION_COLUMN).setCellEditor(JTableService.getJTextAreaCellEditor(new Dimension(300, 110)));
        editPayObjectTable.getColumn(TableService.DATE_COLUMN).setCellEditor(new DateCellEditor(new JTextField(), ApplicationProperties.FORMAT_DATE, null));
        editPayObjectTable.getColumn(TableService.DATE_COLUMN).setCellRenderer(new DateCellRenderer(ApplicationProperties.FORMAT_DATE));
        editPayObjectTable.getColumn(TableService.USER_COLUMN).setCellEditor(new SelectCellEditor(new JComboBox<>(), TableService.getFieldsByColumn(TableService.USER_COLUMN)));
        editPayObjectTable.getColumn(TableService.IMPORTANCE_COLUMN).setCellEditor(new SelectCellEditor(new JComboBox<>(), TableService.getFieldsByColumn(TableService.IMPORTANCE_COLUMN)));
        editPayObjectTable.getColumn(TableService.PAY_TYPE_COLUMN).setCellEditor(new SelectCellEditor(new JComboBox<>(), TableService.getFieldsByColumn(TableService.PAY_TYPE_COLUMN)));
    }

    private void addRemoveColumnView(JTable table) {
        table.getColumn(TableService.REMOVE_COLUMN).setMinWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMaxWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMinWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMaxWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setCellEditor(TableService.getJButtonCellEditor());
        table.getColumn(TableService.REMOVE_COLUMN).setCellRenderer(new JButtonCellRenderer(ImageService.getRemoveButtonIcon()));
    }

}
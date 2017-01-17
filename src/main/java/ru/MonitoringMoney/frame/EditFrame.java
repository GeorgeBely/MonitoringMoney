package ru.MonitoringMoney.frame;

import ru.MonitoringMoney.ApplicationProperties;
import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.FrameService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.services.TableService;
import ru.MonitoringMoney.types.*;
import ru.mangeorge.awt.*;
import ru.mangeorge.awt.service.JTableService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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


    /** Компоненты окна редактирования покупок */
    private JTable editPayObjectTable;
    private JLabel editPayObjectLabel;
    private JButton editPayObjectButton;
    private JScrollPane editPayObjectScrollPane;

    /** Компоненты окна редактирования типов */
    private JLabel editTypeValueLabel;
    private JButton editTypeValueButton;
    private JTable editImportanceTable;
    private JTable editPayTypeTable;
    private JTable editUserTable;
    private Container editTypeValueContainer;

    /** Списки объектов, которые пользователь удаляет (Будут удалены только после того, как пользователь нажмёт кнопку применить) */
    public List<PayObject> removePayObjectList = new ArrayList<>();
    public List<TypeValue> removeList = new ArrayList<>();


    EditFrame() {
        setLocation(ApplicationService.getInstance().getWindowLocation(EditFrame.class));
        setSize(ApplicationService.getInstance().getWindowSize(EditFrame.class));
        setResizable(false);
        setVisible(true);
        setTitle(FRAME_NAME);
        setIconImage(ImageService.getEditImage());
        addComponentListener(FrameService.addComponentListener(EditFrame.class, getSize(), getLocation(), () -> {}));

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

        editImportanceTable = FrameService.createJTableTypeValue(editTypeValuePanel, new Rectangle(5, 5, 200, 240), ImportanceType.class);
        editPayTypeTable = FrameService.createJTableTypeValue(editTypeValuePanel, new Rectangle(225, 5, 200, 240), PayType.class);
        editUserTable = FrameService.createJTableTypeValue(editTypeValuePanel, new Rectangle(445, 5, 200, 240), Users.class);

        JButton okButton = new JButton("Применить") {{
            setBounds(45, 325, 115, 30);
            addActionListener(e -> {
                try {
                    updateData();
                    dispose();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            });
        }};
        panel.add(okButton);

        JButton cancelButton = new JButton("Отмена") {{
            setBounds(500, 325, 115, 30);
            addActionListener(e -> dispose());
        }};
        panel.add(cancelButton);
    }

    /**
     * Обновляет данные о покупках и типах
     */
    private void updateData() throws ParseException {
        ApplicationService.getInstance().payObjects.removeAll(removePayObjectList);
        removePayObjectList.clear();

        for (Object obj : ((DefaultTableModel) editPayObjectTable.getModel()).getDataVector()) {
            Vector vector = (Vector) obj;
            PayObject payObject = (PayObject) vector.get(6);
            payObject.setUser((Users) vector.get(0));
            payObject.setImportance((ImportanceType) vector.get(1));
            payObject.setPayType((PayType) vector.get(2));
            if (vector.get(3) instanceof String) {
                payObject.setPrice(Integer.parseInt((String) vector.get(3)));
            }
            if (vector.get(4) instanceof String) {
                payObject.setDate(ApplicationProperties.FORMAT_DATE.parse((String) vector.get(4)));
            }
            payObject.setDescription((String) vector.get(5));
        }

        updateTypes((DefaultTableModel) editImportanceTable.getModel(), ApplicationService.getInstance().importanceTypes);
        updateTypes((DefaultTableModel) editPayTypeTable.getModel(), ApplicationService.getInstance().payTypes);
        updateTypes((DefaultTableModel) editUserTable.getModel(), ApplicationService.getInstance().users);

        ApplicationService.getInstance().removeTypes(removeList);

        ApplicationService.getInstance().updateAllFrequencyUse();
        MonitoringMoney.mainFrame.updateData();
        ApplicationService.writeData();
    }

    /**
     * Добавляет атрибуты (тип покупки, пользователь, уровень важности) или обновляет их наименования
     *
     * @param model модель таблицы определённого атрибута.
     * @param types список существующих типов
     * @param <T>   Класс изменяемого атрибута
     */
    private <T extends TypeValue> void updateTypes(DefaultTableModel model, List<T> types) {
        for (Object obj : model.getDataVector()) {
            Vector vector = (Vector) obj;

            @SuppressWarnings("unchecked")
            T type = (T) vector.get(1);
            type.setName((String) vector.get(0));
            if (!types.contains(type)) {
                types.add(type);
                MonitoringMoney.addFrame.addSelectElement(type);
                MonitoringMoney.mainFrame.addSelectElement(type);
            }
        }
    }

    /**
     * Обновляет данные в таблице
     */
    void updatePayObjectTable() {
        editPayObjectTable.setModel(TableService.getPayObjectTableData());
        FrameService.addRemoveColumnView(editPayObjectTable);
        editPayObjectTable.getColumn(TableService.DESCRIPTION_COLUMN).setCellEditor(JTableService.getJTextAreaCellEditor(new Dimension(300, 110)));
        editPayObjectTable.getColumn(TableService.DATE_COLUMN).setCellEditor(new DateCellEditor(new JTextField(), ApplicationProperties.FORMAT_DATE, null));
        editPayObjectTable.getColumn(TableService.DATE_COLUMN).setCellRenderer(new DateCellRenderer(ApplicationProperties.FORMAT_DATE));
        editPayObjectTable.getColumn(TableService.USER_COLUMN).setCellEditor(new SelectCellEditor(new JComboBox<>(), TableService.getFieldsByColumn(TableService.USER_COLUMN)));
        editPayObjectTable.getColumn(TableService.IMPORTANCE_COLUMN).setCellEditor(new SelectCellEditor(new JComboBox<>(), TableService.getFieldsByColumn(TableService.IMPORTANCE_COLUMN)));
        editPayObjectTable.getColumn(TableService.PAY_TYPE_COLUMN).setCellEditor(new SelectCellEditor(new JComboBox<>(), TableService.getFieldsByColumn(TableService.PAY_TYPE_COLUMN)));
    }
}
package ru.MonitoringMoney.services;


import ru.MonitoringMoney.ApplicationProperties;
import ru.MonitoringMoney.PayObject;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.types.*;
import ru.mangeorge.awt.JButtonCellEditor;

import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.List;


/**
 * Сервис для работы с таблицей
 */
public class TableService {

    /** Наименования столбцов в таблице */
    public static final String USER_COLUMN = "Платильщик";
    public static final String IMPORTANCE_COLUMN = "Уровень важности";
    public static final String PAY_TYPE_COLUMN = "Тип покупки";
    private static final String PRICE_COLUMN = "Стоимость";
    public static final String DATE_COLUMN = "Дата";
    public static final String DESCRIPTION_COLUMN = "Описание";
    static final String REMOVE_COLUMN = "X";
    private static final String DESIRED_PURCHASE_COLUMN = "Желаемая покупка";


    /**
     * @param className наименование класса, значения типа которого нужно получить
     * @return модель данных со значениями указанного типа
     */
    public static DefaultTableModel getTypeValueTableData(Class className) {
        DefaultTableModel dm = new DefaultTableModel();

        String columnName = null;
        List<TypeValue> values = new ArrayList<>();
        if (className.equals(ImportanceType.class)) {
            values.addAll(ApplicationService.getInstance().getImportanceTypes());
            columnName = IMPORTANCE_COLUMN;
        } else if (className.equals(PayType.class)) {
            values.addAll(ApplicationService.getInstance().getPayTypes());
            columnName = PAY_TYPE_COLUMN;
        } else if (className.equals(Users.class)) {
            values.addAll(ApplicationService.getInstance().getUsers());
            columnName = USER_COLUMN;
        } else if (className.equals(DesiredPurchase.class)) {
            values.addAll(ApplicationService.getInstance().getDesiredPurchases());
            columnName = DESIRED_PURCHASE_COLUMN;
        }

        Object[] header = new Object[]{columnName, REMOVE_COLUMN};
        Object[][] data;
        if (!values.isEmpty()) {
            if (className.equals(DesiredPurchase.class))
                data = new Object[values.size()][2];
            else
                data = new Object[values.size() - 1][2];

            int index = 0;
            for (TypeValue value : values) {
                if (!ApplicationProperties.EMPTY.equals(value.getCode())) {
                    data[index][0] = value.getName();
                    data[index][1] = value;
                    index++;
                }
            }
        } else {
            data = new Object[0][2];
        }
        dm.setDataVector(data, header);
        return dm;
    }

    /**
     * @return данные для таблицы редактирования
     */
    public static DefaultTableModel getPayObjectTableData() {
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

    /**
     * @param columnName наименование столбца в таблице
     * @return массив типов для переданного столбца, или пустой массив в протином случае.
     */
    public static Object[] getFieldsByColumn(String columnName) {
        if (TableService.IMPORTANCE_COLUMN.equals(columnName)) {
            return ApplicationService.getInstance().getSortedImportance();
        } else if (TableService.PAY_TYPE_COLUMN.equals(columnName)) {
            return ApplicationService.getInstance().getSortedPayTypes();
        } else if (TableService.USER_COLUMN.equals(columnName)) {
            return ApplicationService.getInstance().getSortedUsers();
        }
        return new Object[0];
    }

    /**
     * @return Объект ячейки с заданной функцией при клике
     */
    static JButtonCellEditor getJButtonCellEditor() {
        JButtonCellEditor.ButtonClickFunction buttonClickFunction = (value, jTable, row) -> {
            if (value instanceof PayObject)
                MonitoringMoney.mainFrame.editFrame.removePayObjectList.add((PayObject) value);
            else if (value instanceof DesiredPurchase)
                MonitoringMoney.mainFrame.desiredPurchaseFrame.removeDesiredPurchases.add((DesiredPurchase) value);
            else
                MonitoringMoney.mainFrame.editFrame.removeList.add((TypeValue) value);

            ((DefaultTableModel) jTable.getModel()).removeRow(row);
        };
        return new JButtonCellEditor(ImageService.getRemoveButtonIcon(), buttonClickFunction);
    }
}




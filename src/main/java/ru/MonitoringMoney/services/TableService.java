package ru.MonitoringMoney.services;


import ru.MonitoringMoney.types.PayObject;
import ru.MonitoringMoney.frame.MainFrame;
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
    public static final String INCOME_TYPE_COLUMN = "Тип дохода";
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
        } else if (className.equals(IncomeType.class)) {
            values.addAll(ApplicationService.getInstance().getIncomeTypes());
            columnName = INCOME_TYPE_COLUMN;
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
                if (!TypeValue.EMPTY.equals(value.getCode())) {
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

    public static DefaultTableModel getIncomeTableData() {
        DefaultTableModel dm = new DefaultTableModel();
        Object[] header = new Object[]{USER_COLUMN, INCOME_TYPE_COLUMN, PRICE_COLUMN, DATE_COLUMN, DESCRIPTION_COLUMN, REMOVE_COLUMN};
        List<Income> values = ApplicationService.getInstance().getIncomes();
        Object[][] data = new Object[values.size()][header.length];

        int index = 0;
        for (Income value : values) {
            data[index][0] = value.getUser();
            data[index][1] = value.getType();
            data[index][2] = value.getAmountMoney();
            data[index][3] = value.getDate();
            data[index][4] = value.getDescription();
            data[index][5] = value;

            index++;
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
        List<PayObject> payObjects = ApplicationService.viewPayObjects;
        Object[][] data = new Object[payObjects.size()][header.length];

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
        } else if (TableService.INCOME_TYPE_COLUMN.equals(columnName)) {
            return ApplicationService.getInstance().getSortedIncomeTypes();
        }
        return new Object[0];
    }

    /**
     * @return Объект ячейки с заданной функцией при клике
     */
    static JButtonCellEditor getJButtonCellEditor() {
        JButtonCellEditor.ButtonClickFunction buttonClickFunction = (value, jTable, row) -> {
            if (value instanceof PayObject)
                MonitoringMoney.getFrame(MainFrame.class).editFrame.removePayObjectList.add((PayObject) value);
            else if (value instanceof DesiredPurchase)
                MonitoringMoney.getFrame(MainFrame.class).desiredPurchaseFrame.removeDesiredPurchases.add((DesiredPurchase) value);
            else if (value instanceof Income)
                MonitoringMoney.getFrame(MainFrame.class).editFrame.removeIncomeList.add((Income) value);
            else
                MonitoringMoney.getFrame(MainFrame.class).editFrame.removeList.add((TypeValue) value);

            ((DefaultTableModel) jTable.getModel()).removeRow(row);
        };
        return new JButtonCellEditor(ImageService.REMOVE_ICON, buttonClickFunction);
    }
}




package ru.MonitoringMoney.services;

import com.javaswingcomponents.calendar.JSCCalendar;
import com.javaswingcomponents.calendar.cellrenderers.CalendarCellRenderer;
import com.javaswingcomponents.calendar.cellrenderers.CellRendererComponentParameter;
import com.javaswingcomponents.calendar.listeners.CalendarSelectionEventType;
import com.javaswingcomponents.calendar.model.DayOfWeek;
import com.javaswingcomponents.calendar.plaf.darksteel.*;
import ru.MonitoringMoney.frame.PopupDialog;
import ru.MonitoringMoney.main.MonitoringMoney;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.Date;

/**
 * Сервис для работы с компонентом календарь.
 */
public class CalendarService {

    public static final String TABLE_EDIT_CALENDAR_ACTION = "editTableAction";


    /**
     * Создаёт компонент календарь.
     *
     * @return календарь.
     */
    public static JSCCalendar getCalendarComponent(Rectangle bound) {
        return new JSCCalendar() {{
            setBounds(bound);
            setUI(DarkSteelCalendarUI.createUI(this));
            getCalendarModel().setFirstDayOfWeek(DayOfWeek.MONDAY);
            setCalendarCellRenderer(new CustomCellRenderer());
        }};
    }

    public static void addPopupCalendarDialog(JTextField textField, String action) throws ParseException {
        JSCCalendar calendar = CalendarService.getCalendarComponent(new Rectangle(10, 5, 200, 200));
        Date date = ApplicationService.FORMAT_DATE.parse(textField.getText());
        calendar.getCalendarModel().setDisplayDate(date);
        calendar.getCalendarModel().setSelectedDate(date);
        PopupDialog popupDialog = new PopupDialog(textField, new Dimension(220, 225), new Component[]{calendar}, false, true);
        calendar.addCalendarSelectionListener(e -> {
            if (CalendarSelectionEventType.DATE_SELECTED.equals(e.getCalendarSelectionEventType())) {
                if (textField instanceof JFormattedTextField)
                    ((JFormattedTextField) textField).setValue(e.getSelectedDates().get(0));
                else
                    textField.setText(ApplicationService.FORMAT_DATE.format(e.getSelectedDates().get(0)));
                if (!TABLE_EDIT_CALENDAR_ACTION.equals(action))
                    MonitoringMoney.mainFrame.refreshText();
                popupDialog.closeDialog();
            }
        });
    }
}


/**
 * Класс описывающий ячейки календаря.
 * Переопределяет класс DarkSteelCalendarCellRenderer;
 */
class CustomCellRenderer extends JLabel implements CalendarCellRenderer {

    /**
     * Описание ячеек дат.
     */
    @Override
    public JComponent getCellRendererComponent(CellRendererComponentParameter parameterObject) {
        DarkSteelCalendarCellRenderer defaultCell = (DarkSteelCalendarCellRenderer)
                new DarkSteelCalendarCellRenderer().getCellRendererComponent(parameterObject);

        setHorizontalAlignment(defaultCell.getHorizontalAlignment());
        setIcon(defaultCell.getIcon());
        setText(parameterObject.getText());
        setOpaque(defaultCell.isOpaque());
        setForeground(defaultCell.getForeground());
        setBorder(defaultCell.getBorder());
        setBackground(defaultCell.getBackground());

        if (parameterObject.isCurrentMonth) {
            if (parameterObject.isWeekend || parameterObject.isHoliday) {
                setForeground(new Color(51, 142, 237));
                setOpaque(true);
            }

            if (parameterObject.isToday) {
                setForeground(new Color(237, 142, 62));
                setOpaque(true);
            }
        }
        return this;
    }

    /**
     * Описание шапки.
     */
    @Override
    public JComponent getHeadingCellRendererComponent(JSCCalendar calendar, String text) {
        return new DarkSteelCalendarCellRenderer().getHeadingCellRendererComponent(calendar, text);
    }
}
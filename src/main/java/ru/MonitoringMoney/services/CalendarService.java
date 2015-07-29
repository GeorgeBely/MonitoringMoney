package ru.MonitoringMoney.services;

import com.javaswingcomponents.calendar.JSCCalendar;
import com.javaswingcomponents.calendar.cellrenderers.CalendarCellRenderer;
import com.javaswingcomponents.calendar.cellrenderers.CellRendererComponentParameter;
import com.javaswingcomponents.calendar.model.DayOfWeek;
import com.javaswingcomponents.calendar.plaf.darksteel.*;

import javax.swing.*;
import java.awt.*;

/**
 * Сервис для работы с компонентом календарь.
 */
public class CalendarService {


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
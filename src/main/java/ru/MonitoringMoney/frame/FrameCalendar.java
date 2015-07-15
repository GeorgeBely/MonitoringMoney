package ru.MonitoringMoney.frame;

import com.javaswingcomponents.calendar.JSCCalendar;
import com.javaswingcomponents.calendar.listeners.CalendarSelectionEventType;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.services.CalendarService;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class FrameCalendar extends JFrame {

    /** Ширина фрейма */
    private static final int FRAME_WIDTH = 215;

    /** Высота фрейма */
    private static final int FRAME_HEIGHT = 235;

    private static final String FRAME_NAME = "Календарь";


    public FrameCalendar(JFormattedTextField calendarField) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - FRAME_WIDTH / 2, screenSize.height / 2 - FRAME_HEIGHT / 2);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        setVisible(true);
        setTitle(FRAME_NAME);

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);

        JSCCalendar calendar = CalendarService.getCalendarComponent(new Rectangle(5, 5, FRAME_WIDTH - 15, FRAME_HEIGHT - 35));
        calendar.getCalendarModel().setDisplayDate((Date) calendarField.getValue());
        calendar.getCalendarModel().setSelectedDate((Date) calendarField.getValue());
        calendar.addCalendarSelectionListener(e -> {
            if (CalendarSelectionEventType.DATE_SELECTED.equals(e.getCalendarSelectionEventType())) {
                calendarField.setValue(e.getSelectedDates().get(0));
                MonitoringMoney.frame.refreshText();
                dispose();
            }
        });
        panel.add(calendar);
    }
}



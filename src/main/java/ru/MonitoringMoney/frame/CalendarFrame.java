package ru.MonitoringMoney.frame;

import com.javaswingcomponents.calendar.JSCCalendar;
import com.javaswingcomponents.calendar.listeners.CalendarSelectionEventType;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.CalendarService;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.Date;

public class CalendarFrame extends JFrame {

    /** Ширина фрейма */
    private static final int FRAME_WIDTH = 215;

    /** Высота фрейма */
    private static final int FRAME_HEIGHT = 235;

    private static final String FRAME_NAME = "Календарь";


    private JSCCalendar calendar;


    public CalendarFrame(JFormattedTextField calendarField) {
        this();
        calendar.getCalendarModel().setDisplayDate((Date) calendarField.getValue());
        calendar.getCalendarModel().setSelectedDate((Date) calendarField.getValue());
        calendar.addCalendarSelectionListener(e -> {
            if (CalendarSelectionEventType.DATE_SELECTED.equals(e.getCalendarSelectionEventType())) {
                calendarField.setValue(e.getSelectedDates().get(0));
                MonitoringMoney.frame.refreshText();
                dispose();
            }
        });
    }

    public CalendarFrame(JTextField textField) throws ParseException {
        this();
        Date date = ApplicationService.FORMAT_DATE.parse(textField.getText());
        calendar.getCalendarModel().setDisplayDate(date);
        calendar.getCalendarModel().setSelectedDate(date);
        calendar.addCalendarSelectionListener(e -> {
            if (CalendarSelectionEventType.DATE_SELECTED.equals(e.getCalendarSelectionEventType())) {
                textField.setText(ApplicationService.FORMAT_DATE.format(e.getSelectedDates().get(0)));
                dispose();
            }
        });
    }


    public CalendarFrame() {
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

        calendar = CalendarService.getCalendarComponent(new Rectangle(5, 5, FRAME_WIDTH - 15, FRAME_HEIGHT - 35));
        panel.add(calendar);
    }
}



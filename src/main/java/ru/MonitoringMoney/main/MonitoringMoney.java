package ru.MonitoringMoney.main;

import ru.MonitoringMoney.ApplicationProperties;
import ru.MonitoringMoney.frame.AddFrame;
import ru.MonitoringMoney.frame.AddIncomeFrame;
import ru.MonitoringMoney.frame.DesiredPurchaseFrame;
import ru.MonitoringMoney.frame.MainFrame;
import ru.MonitoringMoney.services.ApplicationService;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Запуск приложения. Хранит данные о основном фрейме приложения и фрейме добавления покупки. Загружает данные из файла.
 */
public class MonitoringMoney implements Serializable {

    private static final long serialVersionUID = -2899149309177313402L;

    /** Версия приложения */
    public static final String VERSION = "1.4.24";

    /** Фреймы приложения */
    private static Map<Class<? extends Frame>, Frame> frames = new HashMap<>();


    public static void main(String args[]) throws IOException, ClassNotFoundException {
        if (!ApplicationProperties.BUY_FILE.exists())
            ApplicationService.createNewData();

        ApplicationService.readData();

        EventQueue.invokeLater(() -> frames.put(AddFrame.class, new AddFrame()));
        EventQueue.invokeLater(() -> frames.put(MainFrame.class, new MainFrame()));
        EventQueue.invokeLater(() -> frames.put(AddIncomeFrame.class, new AddIncomeFrame()));
        EventQueue.invokeLater(() -> frames.put(DesiredPurchaseFrame.class, new DesiredPurchaseFrame()));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Frame> T getFrame(Class<T> frameClass) {
        return (T) frames.get(frameClass);
    }
}

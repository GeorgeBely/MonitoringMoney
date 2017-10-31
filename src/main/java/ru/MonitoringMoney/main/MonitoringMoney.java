package ru.MonitoringMoney.main;

import ru.MonitoringMoney.frame.MainFrame;
import ru.MonitoringMoney.services.ApplicationService;

import javax.swing.*;
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
    public static final String VERSION = "1.4.26";

    /** Фреймы приложения */
    private static Map<Class<? extends JFrame>, JFrame> frames = new HashMap<>();


    public static void main(String args[]) throws IOException, ClassNotFoundException {
        if (!ApplicationProperties.BUY_FILE.exists())
            ApplicationService.createNewData();

        ApplicationService.readData();

        EventQueue.invokeLater(() -> frames.put(MainFrame.class, new MainFrame()));
    }

    @SuppressWarnings("unchecked")
    public static <T extends JFrame> T getFrame(Class<T> frameClass) {
        T frame = (T) frames.get(frameClass);
        if (frame == null) {
            try {
                frame = frameClass.newInstance();
                frames.put(frameClass, frame);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return frame;
    }
}

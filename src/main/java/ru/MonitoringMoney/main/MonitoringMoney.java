package ru.MonitoringMoney.main;

import ru.MonitoringMoney.ApplicationProperties;
import ru.MonitoringMoney.frame.AddFrame;
import ru.MonitoringMoney.frame.MainFrame;
import ru.MonitoringMoney.services.ApplicationService;

import java.awt.*;
import java.io.*;


public class MonitoringMoney implements Serializable {

    /** Версия приложения */
    public static final String VERSION = "1.4.20";


    /** Основной фрейм приложения */
    public static MainFrame mainFrame;

    /** Фрейм добавления покупки */
    public static AddFrame addFrame;


    public static void main(String args[]) throws IOException, ClassNotFoundException {
        if (!ApplicationProperties.BUY_FILE.exists())
            ApplicationService.createNewData();

        ApplicationService.readData();

        EventQueue.invokeLater(() -> addFrame = new AddFrame());
        EventQueue.invokeLater(() -> mainFrame = new MainFrame());
    }
}

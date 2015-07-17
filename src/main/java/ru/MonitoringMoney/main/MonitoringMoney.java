package ru.MonitoringMoney.main;

import ru.MonitoringMoney.frame.FrameAdd;
import ru.MonitoringMoney.frame.MainFrame;
import ru.MonitoringMoney.services.ApplicationService;

import java.awt.*;
import java.io.*;


public class MonitoringMoney implements Serializable {

    public static final String VERSION = "1.3.1";


    /** Основной фрейм приложения */
    public static MainFrame frame;

    /** Фрейм добавления покупки */
    public static FrameAdd frameAdd;


    public static void main(String args[]) throws IOException, ClassNotFoundException {
        if (!ApplicationService.buyFile.exists())
            ApplicationService.createNewData();

        ApplicationService.readData();

        EventQueue.invokeLater(() -> frameAdd = new FrameAdd());
        EventQueue.invokeLater(() -> frame = new MainFrame());
    }
}

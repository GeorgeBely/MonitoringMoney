package ru.MonitoringMoney.main;

import ru.MonitoringMoney.frame.MainFrameThread;
import ru.MonitoringMoney.services.ApplicationService;

import java.io.*;


public class MonitoringMoney implements Serializable {

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        if (!ApplicationService.buyFile.exists())
            ApplicationService.createNewData();

        ApplicationService.readData();

        new MainFrameThread().start();
    }
}

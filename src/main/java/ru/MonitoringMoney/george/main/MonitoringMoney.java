package main.java.ru.MonitoringMoney.george.main;

import main.java.ru.MonitoringMoney.george.frame.MainFrameThread;
import main.java.ru.MonitoringMoney.george.helpers.ApplicationHelper;

import java.io.*;


public class MonitoringMoney implements Serializable {

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        if (!ApplicationHelper.buyFile.exists())
            ApplicationHelper.createNewData();

        ApplicationHelper.readData();

        new MainFrameThread().start();
    }
}

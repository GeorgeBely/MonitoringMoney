package main.java.ru.MonitoringMoney.george.frame;


import java.awt.*;

public class MainFrameThread extends Thread {
    public static MainFrame frame;

    public void run() {
        EventQueue.invokeLater(() -> {
            try {
                frame = new MainFrame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

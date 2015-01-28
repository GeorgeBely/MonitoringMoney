package main.java.ru.MonitoringMoney.george;

import javax.swing.*;
import java.awt.*;

public class MainFrameThread extends Thread {
    public static MainFrame frame;

    public void run() {
        EventQueue.invokeLater(() -> {
            try {
                frame = new MainFrame();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

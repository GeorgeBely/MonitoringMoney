package main.java.ru.MonitoringMoney.george;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class MonitoringMoney implements Serializable {

    /** Файл с данными о покупках */
    public static final File buyFile = new File("MoneyData.mm");

    /** Список покупок */
    public static List<PayObject> payObjects = new ArrayList<>();


    public static void main(String args[]) throws IOException, ClassNotFoundException {
        if (!buyFile.exists())
            if(buyFile.createNewFile())
                new ObjectOutputStream(new FileOutputStream(MonitoringMoney.buyFile)).writeObject(payObjects);

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(buyFile));
            payObjects = (List<PayObject>) ois.readObject();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        new MainFrameThread().start();
    }
}

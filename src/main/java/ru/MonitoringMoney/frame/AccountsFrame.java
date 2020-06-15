package ru.MonitoringMoney.frame;


import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.FrameService;
import ru.MonitoringMoney.services.GraphicsService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.types.Account;
import ru.MonitoringMoney.types.PayObject;
import ru.MonitoringMoney.types.PayType;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;


/**
 * Created by  George Beliy at 09-08-2018
 */
public class AccountsFrame extends JFrame implements Serializable {

    private static final long serialVersionUID = 2306831544787529125L;

    private static final String FRAME_NAME = "Управление счетами";


    /** график "Категории" */
    private ChartPanel barPanel;
    private JFreeChart barChart;

    private JLabel totalSumResult;


    public AccountsFrame() {
        setResizable(true);
        setVisible(false);
        setTitle(FRAME_NAME);
        toFront();
        setIconImage(ImageService.ACCOUNTS_IMAGE);
        setLocation(ApplicationService.getInstance().getWindowLocation(this));
        setSize(ApplicationService.getInstance().getWindowSize(this));
        addComponentListener(FrameService.addComponentListener(AccountsFrame.class, getSize(), getLocation(), () -> {}, this::resizeFrame));

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);

        JButton buttonAdd = new JButton("Добавить счёт") {{
            setBounds(5, 5, 240, 30);
            addActionListener(e -> MonitoringMoney.getFrame(AddAccountFrame.class).showFrame());
        }};
        panel.add(buttonAdd);

        totalSumResult = new JLabel() {{
            setBounds(250, 5, 400, 20);
        }};
        panel.add(totalSumResult);

        barChart = ChartFactory.createBarChart("Расходы по счетам", "Счёт",
                "Сумма", getBarChartData());
        barChart.setBackgroundPaint(this.getBackground());
        barChart.getPlot().setNoDataMessage(GraphicsService.NO_DATA_MESSAGE);
        barPanel = new ChartPanel(barChart) {{
            setLocation(5, 50);
            setSize(super.getWidth() - 30, super.getHeight()  - 55);
            setVisible(true);
        }};
        panel.add(barPanel);
    }

    public CategoryDataset getBarChartData() {
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

        List<Account> accounts = ApplicationService.getInstance().getAccounts();
        List<PayType> payTypes = ApplicationService.getInstance().getPayTypes();

        Map<PayType, List<Account>> payTypeToAccounts = new HashMap<>();
        for (PayType payType : payTypes) {
            if (!payTypeToAccounts.containsKey(payType)) {
                payTypeToAccounts.put(payType, new ArrayList<>());
            }
            for (Account account : accounts) {
                if (account.getTypes().contains(payType)) {
                    payTypeToAccounts.get(payType).add(account);
                }
            }
        }

        Map<Account, Map<String, Integer>> sortDataMap = new TreeMap<>();
        Integer sumLimit = 0;
        Integer sumPrice = 0;
        for (Account account : accounts) {
            sortDataMap.put(account, new HashMap<>());
            sortDataMap.get(account).put("Предел", account.getLimit());
            sumLimit += account.getLimit();
        }
        for (PayObject payObject : ApplicationService.viewPayObjects) {
            for (Account account : payTypeToAccounts.get(payObject.getPayType())) {
                Map<String, Integer> accountValues = sortDataMap.get(account);

                if (accountValues.containsKey("Потрачено")) {
                    Integer price = accountValues.get("Потрачено");
                    accountValues.put("Потрачено", payObject.getPrice() + price);
                } else {
                    accountValues.put("Потрачено", payObject.getPrice());
                }
            }
            sumPrice += payObject.getPrice();
        }

        totalSumResult.setText("Общий лимит: " + sumLimit + "   Потрачено: " + sumPrice);

        for (Map.Entry<Account, Map<String, Integer>> entry : sortDataMap.entrySet()) {
            for (Map.Entry<String, Integer> value : entry.getValue().entrySet()) {
                dataSet.addValue(value.getValue(), value.getKey(), entry.getKey().getName());
            }
        }
        return dataSet;
    }


    void showFrame() {
        setVisible(true);
    }

    private void hideFrame() {
        setVisible(false);
    }

    public void updateData() {
        ((CategoryPlot) barChart.getPlot()).setDataset(getBarChartData());
    }

    private void resizeFrame() {
        Container frame = barPanel.getParent();
        barPanel.setSize(frame.getWidth() - 15, frame.getHeight() - 55);
    }
}

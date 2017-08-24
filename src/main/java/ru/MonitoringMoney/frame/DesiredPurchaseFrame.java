package ru.MonitoringMoney.frame;


import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.FrameService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.services.TableService;
import ru.MonitoringMoney.types.DesiredPurchase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;


/**
 * Фрейм для добавления и отображения желаемых покупок
 */
public class DesiredPurchaseFrame extends JFrame {

    private static final long serialVersionUID = -1405498408839775613L;

    private static final String FRAME_NAME = "Список желаемых покупок";


    private JTable desiredPurchaseTable;
    public List<DesiredPurchase> removeDesiredPurchases = new ArrayList<>();


    public DesiredPurchaseFrame() {
        setResizable(false);
        setVisible(false);
        setTitle(FRAME_NAME);
        setIconImage(ImageService.DESIRED_PURCHASE_IMAGE);
        setLocation(ApplicationService.getInstance().getWindowLocation(this));
        setSize(ApplicationService.getInstance().getWindowSize(this));
        addComponentListener(FrameService.addComponentListener(DesiredPurchaseFrame.class, getSize(), getLocation(), () -> {}));

        JPanel panel = new JPanel() {{
            setFocusable(true);
            setLayout(null);
        }};
        add(panel);

        desiredPurchaseTable = new JTable();
        JScrollPane desiredPurchaseScrollPane = new JScrollPane() {{
            setViewportView(desiredPurchaseTable);
            setBounds(5, 5, 285, 275);
        }};
        panel.add(desiredPurchaseScrollPane);

        JButton addButton = new JButton("Добавить") {{
            setBounds(75, 285, 150, 30);
            addActionListener(e -> {
                DesiredPurchase type = new DesiredPurchase(ApplicationService.getInstance().getNewUniqueCode(), "");
                ((DefaultTableModel) desiredPurchaseTable.getModel()).addRow(new Object[]{"", type});
            });
            setIcon(ImageService.PLUS_ICON);
        }};
        panel.add(addButton);

        JButton okButton = new JButton("Применить") {{
            setBounds(5, 335, 115, 30);
            addActionListener(e -> {
                updateData();
                hideFrame();
            });
        }};
        panel.add(okButton);

        JButton cancelButton = new JButton("Отмена") {{
            setBounds(175, 335, 115, 30);
            addActionListener(e -> hideFrame());
        }};
        panel.add(cancelButton);

        updateTypeValueTable();
    }

    private void updateData() {
        ApplicationService.getInstance().getDesiredPurchases().removeAll(removeDesiredPurchases.stream()
                .filter(ApplicationService.getInstance().getDesiredPurchases()::contains)
                .collect(Collectors.toList()));
        removeDesiredPurchases.clear();
        for (Object obj : ((DefaultTableModel) desiredPurchaseTable.getModel()).getDataVector()) {
            Vector vector = (Vector) obj;
            DesiredPurchase desiredPurchase = (DesiredPurchase) vector.get(1);
            desiredPurchase.setName((String) vector.get(0));
            if (!ApplicationService.getInstance().getDesiredPurchases().contains(desiredPurchase)) {
                ApplicationService.getInstance().getDesiredPurchases().add(desiredPurchase);
            }
        }
        ApplicationService.writeData();
    }

    private void updateTypeValueTable() {
        desiredPurchaseTable.setModel(TableService.getTypeValueTableData(DesiredPurchase.class));
        FrameService.addRemoveColumnView(desiredPurchaseTable);
    }

    void showFrame() {
        setVisible(true);
    }

    private void hideFrame() {
        setVisible(false);
    }
}

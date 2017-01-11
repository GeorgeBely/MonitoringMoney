package ru.MonitoringMoney.frame;


import ru.MonitoringMoney.services.ApplicationService;
import ru.MonitoringMoney.services.FrameService;
import ru.MonitoringMoney.services.ImageService;
import ru.MonitoringMoney.services.TableService;
import ru.MonitoringMoney.types.DesiredPurchase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Фрейм для добавления нового типа покупки
 */
public class DesiredPurchaseFrame extends JFrame {

    private static final long serialVersionUID = -1405498408839775613L;


    private static final String FRAME_NAME = "Список желаемых покупок";

    private JTable desiredPurchaseTable;
    public List<DesiredPurchase> removeDesiredPurchases = new ArrayList<>();


    DesiredPurchaseFrame() {
        setLocation(ApplicationService.getInstance().getWindowLocation(DesiredPurchaseFrame.class));
        setSize(ApplicationService.getInstance().getWindowSize(DesiredPurchaseFrame.class));
        setResizable(false);
        setVisible(true);
        setTitle(FRAME_NAME);

        addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentShown(ComponentEvent e) {
            }

            public void componentHidden(ComponentEvent e) {
                ApplicationService.getInstance().updateSizeWindow(DesiredPurchaseFrame.class, getSize());
                ApplicationService.getInstance().updateLocationWindow(DesiredPurchaseFrame.class, getLocation());
            }
        });

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
            setIcon(ImageService.getPlusButtonIcon());
        }};
        panel.add(addButton);

        JButton okButton = new JButton("Применить") {{
            setBounds(5, 335, 115, 30);
            addActionListener(e -> {
                updateData();
                dispose();
            });
        }};
        panel.add(okButton);

        JButton cancelButton = new JButton("Отмена") {{
            setBounds(175, 335, 115, 30);
            addActionListener(e -> dispose());
        }};
        panel.add(cancelButton);

        updateTypeValueTable();
    }


    private void updateData() {
        removeDesiredPurchases.stream()
                .filter(ApplicationService.getInstance().desiredPurchases::contains)
                .forEach(ApplicationService.getInstance().desiredPurchases::remove);
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
}

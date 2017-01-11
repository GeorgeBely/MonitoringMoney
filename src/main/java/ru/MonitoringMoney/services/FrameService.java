package ru.MonitoringMoney.services;


import ru.mangeorge.awt.JButtonCellRenderer;

import javax.swing.*;

public class FrameService {

    public static void addRemoveColumnView(JTable table) {
        table.getColumn(TableService.REMOVE_COLUMN).setMinWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMaxWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMinWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setMaxWidth(20);
        table.getColumn(TableService.REMOVE_COLUMN).setCellEditor(TableService.getJButtonCellEditor());
        table.getColumn(TableService.REMOVE_COLUMN).setCellRenderer(new JButtonCellRenderer(ImageService.getRemoveButtonIcon()));
    }

}

package ru.MonitoringMoney.services;


import ru.MonitoringMoney.main.MonitoringMoney;
import ru.MonitoringMoney.types.TypeValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CheckBoxListService {

    public static DefaultComboBoxModel<CheckBoxListService.CheckComboValue> getModel(List valueList) {
        CheckBoxListService.CheckComboValue[] stores = new CheckBoxListService.CheckComboValue[valueList.size()];
        int i = 0;
        for (Object type : valueList) {
            stores[i] = new CheckBoxListService.CheckComboValue((TypeValue) type, false);
            i++;
        }
        return new DefaultComboBoxModel<>(stores);
    }

    public static class CheckComboValue {
        private String id;
        private Boolean state;
        private TypeValue type;

        public CheckComboValue(TypeValue type, Boolean state) {
            this.id = type.getName();
            this.state = state;
            this.type = type;
        }

        public boolean isSelected() {
            return Boolean.TRUE.equals(state);
        }

        public TypeValue getType() {
            return type;
        }

        public void setState(Boolean state) {
            this.state = state;
        }
    }

    public static class CheckBoxList implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            CheckComboValue store = (CheckComboValue) cb.getSelectedItem();
            CheckComboRenderer ccr = (CheckComboRenderer) cb.getRenderer();

            if (ApplicationService.EMPTY.equals(store.getType().getCode())) {
                DefaultComboBoxModel defaultModel = (DefaultComboBoxModel) cb.getModel();
                for (int i = 0; i < defaultModel.getSize(); i++) {
                    CheckBoxListService.CheckComboValue value = (CheckBoxListService.CheckComboValue) defaultModel.getElementAt(i);
                    value.setState(false);
                }
            } else {
                ccr.checkBox.setSelected((store.state = !store.state));
            }
            MonitoringMoney.mainFrame.refreshText();
        }
    }

    public static class CheckComboRenderer implements ListCellRenderer {
        private JCheckBox checkBox;

        public CheckComboRenderer() {
            checkBox = new JCheckBox();
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            CheckComboValue store = (CheckComboValue) value;
            checkBox.setText(store.id);
            checkBox.setSelected(store.state);
            if (isSelected) {
                checkBox.setBackground(list.getSelectionBackground());
                checkBox.setForeground(list.getSelectionForeground());
            }
            else {
                checkBox.setBackground(list.getBackground());
                checkBox.setForeground(list.getForeground());
            }
            return checkBox;
        }
    }
}






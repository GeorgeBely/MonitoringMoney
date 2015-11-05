package ru.MonitoringMoney;

import ru.MonitoringMoney.frame.AddFrame;
import ru.MonitoringMoney.frame.EditFrame;
import ru.MonitoringMoney.frame.GraphicsFrame;
import ru.MonitoringMoney.frame.MainFrame;
import ru.MonitoringMoney.types.ImportanceType;
import ru.MonitoringMoney.types.PayType;

import java.awt.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ApplicationProperties {

    /** ��� ������� ���� �������� ������� */
    public static final String EMPTY = "empty";

    /** ���� � ������� � �������� */
    public static final File BUY_FILE = new File("MoneyData.mm");

    /** ������ ���� ��� ���� ����� ���� */
    public static final DateFormat FORMAT_DATE = DateFormat.getDateInstance(DateFormat.SHORT);

    /** ������ ���� ������������ ������ �������� ������ � ���*/
    public static final DateFormat FORMAT_MONTH_AND_YEAR = new SimpleDateFormat("LLLL yyyy");

    /** ����� �� ���������� ������� ������ �� ���������. ���� ������ ������ ������, �������� ������ ������ */
    public static final Map<Class, Dimension> DEFAULT_FRAME_SIZE = new HashMap<Class, Dimension>() {{
        put(AddFrame.class, new Dimension(250, 320));
        put(MainFrame.class, new Dimension(510, 260));
        put(GraphicsFrame.class, new Dimension(515, 335));
        put(EditFrame.class, new Dimension(660, 390));
    }};


    /**
     * ������ ������� ��������� �� ���������
     */
    public static final List<ImportanceType> DEFAULT_IMPORTANCE = new ArrayList<ImportanceType>() {{
        add(new ImportanceType(EMPTY, ""));
        add(new ImportanceType("very_important", "����������"));
        add(new ImportanceType("important", "�����"));
        add(new ImportanceType("medium", "��������"));
        add(new ImportanceType("low", "�������"));
        add(new ImportanceType("very_low", "�����������"));
    }};

    /**
     * ������ ����� ������� �� ���������
     */
    public static final List<PayType> DEFAULT_PAY_TYPES = new ArrayList<PayType>() {{
        add(new PayType(EMPTY, ""));
        add(new PayType("buz", "���������"));
        add(new PayType("dining_room", "��������"));
        add(new PayType("delicacy", "���������"));
        add(new PayType("duty", "�������"));
        add(new PayType("fast_food", "���� ���"));
        add(new PayType("medicine", "���������/��������"));
    }};
}

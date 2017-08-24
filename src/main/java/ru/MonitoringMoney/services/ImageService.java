package ru.MonitoringMoney.services;

import ru.MonitoringMoney.types.ImageCanvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;


/**
 * Сервис для работы с изображениями
 */
public class ImageService {

    /** Расположение изображений */
    private static final String LOCATION_IMAGES = "./images";

    /** Расположение иконок */
    private static final String LOCATION_ICONS = LOCATION_IMAGES + "/icons";

    /** Изображение для фрейма графиков */
    public static final Image GRAPHICS_IMAGE = getImage("graphicsButton.png");
    public static final ImageIcon GRAPHICS_ICON = GRAPHICS_IMAGE == null ? null : new ImageIcon(GRAPHICS_IMAGE);

    /** Изображение для фрейма редактирования */
    public static final Image EDIT_IMAGE = getImage("edit.png");
    public static final ImageIcon EDIT_ICON = EDIT_IMAGE == null ? null : new ImageIcon(EDIT_IMAGE);

    /** Изображение для фрейма добавления покупки и кнопок добавления */
    public static final Image PLUS_IMAGE = getImage("plus.png");
    public static final ImageIcon PLUS_ICON = PLUS_IMAGE == null ? null : new ImageIcon(PLUS_IMAGE);

    /** Основное изображение приложения */
    public static final Image MONEY_IMAGE = getImage("money.png");

    /** Изображение для фрейма добавления дохода */
    public static final Image ADD_INCOME_IMAGE = getImage("addIncome.png");
    public static final ImageIcon ADD_INCOME_ICON = ADD_INCOME_IMAGE == null ? null : new ImageIcon(ADD_INCOME_IMAGE);

    /** Иконка для фрейма желаемых покупок */
    public static final Image DESIRED_PURCHASE_IMAGE = getImage("desiredPurchase.png");
    public static final ImageIcon DESIRED_PURCHASE_ICON = DESIRED_PURCHASE_IMAGE == null ? null : new ImageIcon(DESIRED_PURCHASE_IMAGE);

    /** Иконка для кнопки удаления */
    private static final Image REMOVE_IMAGE = getImage("remove.png");
    static final ImageIcon REMOVE_ICON = REMOVE_IMAGE == null ? null : new ImageIcon(REMOVE_IMAGE);


    /**
     * @param imageName имя файла изображения
     * @return объект изображения
     */
    private static Image getImage(String imageName) {
        try {
            File imageFile = new File(LOCATION_ICONS + "/" + imageName);
            if (imageFile.exists()) {
                ApplicationService.getInstance().getImages().put(imageName, new ImageCanvas(ImageIO.read(imageFile)));
                ApplicationService.writeData();
            }
            ImageCanvas image = ApplicationService.getInstance().getImages().get(imageName);
            if (image != null)
                return image.getImage();

        } catch (IOException ignore) { }

        return null;
    }
}
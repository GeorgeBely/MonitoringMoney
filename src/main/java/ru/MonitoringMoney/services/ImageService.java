package ru.MonitoringMoney.services;

import ru.MonitoringMoney.ImageCanvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ImageService {

    /** Расположение изображений */
    private static final String LOCATION_IMAGES = "./images";

    /** Расположение иконок */
    private static final String LOCATION_ICONS = LOCATION_IMAGES + "/icons";


    /** @return иконку для кнопки отображения графиков. */
    public static ImageIcon getGraphicsButtonIcon() {
        return getIcon("graphicsButton.png");
    }

    /** @return изображение иконки графиков. */
    public static Image getGraphicsImage() {
        return getImage("graphicsButton.png");
    }

    /** @return изображение иконки редактирования. */
    public static Image getEditImage() {
        return getImage("edit.png");
    }

    /** @return иконку для кнопки добавления типов. */
    public static ImageIcon getPlusButtonIcon() {
        return getIcon("plus.png");
    }

    /** @return изображение иконки плюса */
    public static Image getPlusImage() {
        return getImage("plus.png");
    }

    /** @return изображение основной иконки приложения */
    public static Image getMoneyImage() {
        return getImage("money.png");
    }

    /** @return иконку для кнопки редактирования покупок. */
    public static ImageIcon getEditButtonIcon() {
        return getIcon("edit.png");
    }

    /** @return иконку для кнопки списка желаемых покупок */
    public static ImageIcon getDesiredPurchase() {
        return getIcon("desiredPurchase.png");
    }

    /** @return иконку для кнопки удаления покупки. */
    public static ImageIcon getRemoveButtonIcon() {
        return getIcon("remove.png");
    }


    /**
     * @param name имя файла иконки
     * @return объект иконки
     */
    public static ImageIcon getIcon(String name) {
        Image image = getImage(name);
        if (image == null)
            return null;
        return new ImageIcon(image);
    }

    /**
     * @param imageName имя файла изображения
     * @return объект изображения
     */
    public static Image getImage(String imageName) {
        try {
            ImageCanvas image = ApplicationService.getInstance().images.get(imageName);
            if (image != null && image.getImage() != null)
                return image.getImage();
            image = new ImageCanvas(ImageIO.read(new File(LOCATION_ICONS + "/" + imageName)));
            ApplicationService.getInstance().images.put(imageName, image);
            return image.getImage();
        } catch (IOException e) {
            return null;
        }
    }
}
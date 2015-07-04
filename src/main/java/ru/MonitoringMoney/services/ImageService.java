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



    /**
     * @return иконку для кнопки отображения графиков.
     */
    public static ImageIcon getGraphicsButtonIcon() {
        Image image = getImage("graphicsButton.jpg");
        if (image == null)
            return null;
        return new ImageIcon(image);
    }

    /**
     * @return иконку для кнопки добавления типов.
     */
    public static ImageIcon getPlusButtonIcon() {
        Image image = getImage("plus.png");
        if (image == null)
            return null;
        return new ImageIcon(image);
    }

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
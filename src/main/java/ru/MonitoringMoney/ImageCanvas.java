package ru.MonitoringMoney;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Объект обёртка для хранения изображений в сериализованном виде
 */
public class ImageCanvas implements Serializable {
    transient BufferedImage image;

    public ImageCanvas(BufferedImage image) {
        this.image = image;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ImageIO.write(image, "png", out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = ImageIO.read(in);
    }

    public BufferedImage getImage() {
        return image;
    }
}
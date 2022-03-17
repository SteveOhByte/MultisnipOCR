package io.steveoh.multisnip;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCaptureForm extends JFrame {

    private String imagePath;

    public ImageCaptureForm(String imagePath) {
        this.imagePath = imagePath;
        init();
    }

    private void init() {
        this.setAlwaysOnTop(true);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        this.setLayout(null);

        try {
            this.setContentPane(new ImagePanel(ImageIO.read(new File(imagePath))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Component c : getComponents()) {
            c.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }
}

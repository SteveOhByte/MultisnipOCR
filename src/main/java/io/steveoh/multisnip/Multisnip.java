package io.steveoh.multisnip;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Multisnip {

    public static String workingDirectory;
    public static String lang;
    public static String fileSeparator;
    public static boolean running = false;

    private static GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(false);
    private static GlobalMouseHook mouseHook = new GlobalMouseHook(false);

    private static boolean dragReady, dragging, windowsOpen;
    private static int sourceX, sourceY, destX, destY, width, height;

    private static ArrayList<String> images = new ArrayList<String>();
    private static ArrayList<ImageCaptureForm> forms = new ArrayList<ImageCaptureForm>();

    public static void main(String[] args) {
        lang = Locale.getDefault().getISO3Language();
        fileSeparator = Utils.getFileSpearator();
        workingDirectory = Utils.getWorkingDirectory();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        setupSysTray();

        keyboardHook.addKeyListener(new GlobalKeyAdapter() {
            @Override
            public void keyPressed(GlobalKeyEvent e) {
                if ((dragReady || dragging || windowsOpen) && e.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
                    clearForms();
                }

                if (!dragReady && e.isControlPressed() && e.isMenuPressed() && e.getVirtualKeyCode() == GlobalKeyEvent.VK_S) {
                    dragReady = true;
                    System.out.println("Drag ready");

                    try {
                        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
                        for (int i = 0; i < devices.length; i++) {
                            BufferedImage image = new Robot().createScreenCapture(devices[i].getDefaultConfiguration().getBounds());
                            image = colorImage(image, 0, 0, 0, 100);

                            ImageIO.write(image, "tif", new File(workingDirectory + fileSeparator + "screenImage" + (i + 1) + ".tif"));
                            images.add(workingDirectory + fileSeparator + "screenImage" + (i + 1) + ".tif");

                            ImageCaptureForm form = new ImageCaptureForm(workingDirectory + fileSeparator + "screenImage" + (i + 1) + ".tif");
                            width = devices[i].getDefaultConfiguration().getBounds().width;
                            height = devices[i].getDefaultConfiguration().getBounds().height;
                            form.setLocation(
                                    ((width / 2) - (form.getSize().width / 2)) + devices[i].getDefaultConfiguration().getBounds().x,
                                    ((height / 2) - (form.getSize().height / 2)) + devices[i].getDefaultConfiguration().getBounds().y
                            );

                            form.setVisible(true);
                            form.toFront();
                            form.requestFocus();
                            forms.add(form);
                        }
                        windowsOpen = true;

                    } catch (AWTException | IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        mouseHook.addMouseListener(new GlobalMouseAdapter() {
            @Override
            public void mousePressed(GlobalMouseEvent e) {
                if (e.getButton() != GlobalMouseEvent.BUTTON_LEFT) return;

                // Start dragging
                if (dragReady && !dragging) {
                    sourceX = MouseInfo.getPointerInfo().getLocation().x;
                    sourceY = MouseInfo.getPointerInfo().getLocation().y;
                    dragging = true;
                }
            }

            @Override
            public void mouseReleased(GlobalMouseEvent e) {
                if (e.getButton() != GlobalMouseEvent.BUTTON_LEFT) return;

                // Stop dragging
                if (dragReady && dragging) {
                    destX = MouseInfo.getPointerInfo().getLocation().x;
                    destY = MouseInfo.getPointerInfo().getLocation().y;

                    if (sourceX == destX || sourceY == destY) {
                        System.out.println("Could not complete operation, no area selected");

                        dragging = false;
                        dragReady = false;
                        return;
                    }

                    int topLeftX = Math.min(sourceX, destX);
                    int topLeftY = Math.min(sourceY, destY);
                    int bottomRightX = Math.max(sourceX, destX);
                    int bottomRightY = Math.max(sourceY, destY);

                    width = bottomRightX - topLeftX;
                    height = bottomRightY - topLeftY;

                    clearForms();

                    try {
                        BufferedImage image = new Robot().createScreenCapture(new Rectangle(topLeftX, topLeftY, width, height));
                        ImageIO.write(image, "tif", new File(workingDirectory + fileSeparator + "imgToOCR.tif"));
                        images.add(workingDirectory + fileSeparator + "imgToOCR.tif");

                        String ocr = new OCR().execute(workingDirectory + fileSeparator + "imgToOCR.tif");
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(new StringSelection(ocr), null);

                        cleanup();
                    } catch (AWTException | IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        running = true;

        try {
            while (running) {
                //Thread.sleep(128);
            }
        }
        finally {
            keyboardHook.shutdownHook();
            mouseHook.shutdownHook();
        }
    }

    private static void cleanup() {
        dragReady = false;
        dragging = false;
        windowsOpen = false;
        sourceX = 0;
        sourceY = 0;
        destX = 0;
        destY = 0;
        width = 0;
        height = 0;
        clearForms();
        for (String image : images) {
            if (Files.exists(Paths.get(image))) {
                try {
                    Files.delete(Paths.get(image));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void setupSysTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        try {
            final PopupMenu popupMenu = new PopupMenu();
            final TrayIcon trayIcon = new TrayIcon(ImageIO.read(new File(workingDirectory + fileSeparator + "tray.png")), "MultisnipOCR");
            final SystemTray tray = SystemTray.getSystemTray();

            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(e -> {
                System.exit(0);
            });
            popupMenu.add(exitItem);
            trayIcon.setPopupMenu(popupMenu);

            tray.add(trayIcon);
        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage colorImage(BufferedImage loadImg, int red, int green, int blue, int alpha /*Also the intesity*/) {
        Graphics g = loadImg.getGraphics();
        g.setColor(new Color(red, green, blue, alpha));
        g.fillRect(0, 0, loadImg.getWidth(), loadImg.getHeight());
        g.dispose();
        return loadImg;
    }

    private static void clearForms() {
        for (ImageCaptureForm form : forms) {
            form.dispose();
        }
        forms.clear();
        windowsOpen = false;
        dragReady = false;
        dragging = false;
    }
}

package sample;

import sample.printserv.ServerTLS;
import sample.printserv.messages.PrintableConfig;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;

public class Main {

    public static final String APPLICATION_NAME = "Print server";
    public static final String ICON_STR = "Print.png";
    static ServerTLS serverTLS;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });
    }

    private static void createGUI() {
//        JFrame frame = new JFrame(APPLICATION_NAME);
//        frame.setMinimumSize(new Dimension(300, 200));
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);

        serverTLS = new ServerTLS();

        Thread myThready = new Thread(serverTLS);	//Создание потока "myThready"
        myThready.start();
        setTrayIcon();
    }

    private static void setTrayIcon() {
        PrintableConfig prConf = new PrintableConfig();
        if(! SystemTray.isSupported() ) {
            return;
        }

        PopupMenu trayMenu = new PopupMenu();
        MenuItem item = new MenuItem("Exit");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        trayMenu.add(item);

        URL imageURL = Main.class.getResource(prConf.PrinterPNG);

        Image icon = Toolkit.getDefaultToolkit().getImage(imageURL);
        TrayIcon trayIcon = new TrayIcon(icon, APPLICATION_NAME, trayMenu);
        trayIcon.setImageAutoSize(true);

        SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        trayIcon.displayMessage(APPLICATION_NAME, "Application started!",
                TrayIcon.MessageType.INFO);
    }
}
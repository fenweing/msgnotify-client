package com.tuanbaol.messageclient;

import javax.swing.*;
import java.awt.EventQueue;


public class Msg extends JFrame {
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Msg frame = new Msg();

                            ImageIcon imageIcon1 = new ImageIcon(Msg.class.getResource("/icon.png"));
                            ImageIcon imageIcon2 = new ImageIcon(Msg.class.getResource("/cross.png"));
                            frame.setIconImage(imageIcon1.getImage());
                frame.setVisible(true);
                new Thread(() -> {
                    while (true) {
                        try {
                            Thread.sleep(500L);
                            frame.setIconImage(imageIcon1.getImage());
                            Thread.sleep(500L);
                            frame.setIconImage(imageIcon2.getImage());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Msg() {
        setBounds(100, 100, 260, 120);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel label = new JLabel("您有新的消息");
        label.setBounds(77, 30, 100, 15);
        getContentPane().add(label);

    }
}

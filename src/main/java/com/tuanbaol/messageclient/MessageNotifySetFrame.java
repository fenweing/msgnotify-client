package com.tuanbaol.messageclient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MessageNotifySetFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final MessageNotifyFrame notifyFrame;

    // 当前操作系统的托盘对象
    private SystemTray sysTray;

    // 托盘图标
    private TrayIcon trayIcon;

    // 图片
    private ImageIcon icon = null;

    // 消息是否需要闪烁。默认false不需要闪烁。这个状态需要跨线程修改的。
    // 所以为了健壮代码、线程安全，注意使用关键字volatile
    private volatile boolean msgNeedFlash = false;
    private String host = "localhost";
    private Integer port = 8099;
    private JTextArea messageArea;

    public MessageNotifySetFrame(MessageNotifyFrame notifyFrame) {
        this.notifyFrame = notifyFrame;
        setTitle("设置");
        setBounds(500, 200, 800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        addComponent();
    }

    private void addComponent() {
        Box b2 = Box.createVerticalBox();
        b2.add(Box.createRigidArea(new Dimension(100, 20)));
        //添加长度为40的垂直框架
        getContentPane().add(b2);
        JPanel fieldPanel = new JPanel();
        EmptyBorder panelBorder = new EmptyBorder(5, 5, 5, 5);
        fieldPanel.setBorder(panelBorder);
        JPanel ipPanel = new JPanel();
        JLabel ipLabel = new JLabel("服务器地址");
        JTextField ipField = new JTextField(16);
        ipField.setText(notifyFrame.getHost());
        ipPanel.add(ipLabel);
        ipPanel.add(ipField);
        JPanel portPanel = new JPanel();
        JLabel portLabel = new JLabel("端口");
        JTextField portField = new JTextField(16);
        portField.setText(String.valueOf(notifyFrame.getPort()));
        portPanel.add(portLabel);
        portPanel.add(portField);
        fieldPanel.add(ipPanel);
        fieldPanel.add(portPanel);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(panelBorder);
        JButton buttonSave = new JButton("保存");
        buttonPanel.add(buttonSave);
//        JButton buttonReconnect = new JButton("重连");
//        buttonPanel.add(buttonReconnect);

        b2.add(fieldPanel);    //添加按钮3
        b2.add(Box.createVerticalGlue());    //添加垂直组件
        b2.add(buttonPanel);    //添加按钮4
        b2.add(Box.createVerticalStrut(40));
        buttonSave.addActionListener(args -> {
            notifyFrame.configServer(ipField.getText(),Integer.valueOf(portField.getText()));
        });
    }

}

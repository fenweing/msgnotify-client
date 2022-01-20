package com.tuanbaol.messageclient;

import com.tuanbaol.messageclient.bean.Message;
import com.tuanbaol.messageclient.constant.WebsocketStatusEnum;
import com.tuanbaol.messageclient.util.DateUtils;
import com.tuanbaol.messageclient.util.StringUtil;
import com.tuanbaol.messageclient.websocket.ClientByNetty;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import static com.tuanbaol.messageclient.util.LogUtil.addTimeFormat;

public class MessageNotifyFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String ICON_NORMAL = "normal.jpg";
    private static final String ICON_ACTIVE = "active.png";
    private static final String ICON_TRAY = "tray.jpg";

    // 当前操作系统的托盘对象
    private SystemTray sysTray;

    // 托盘图标
    private TrayIcon trayIcon;

    private String host = "www.tuanbaol.com";
    private Integer port = 8099;


    private JTextArea messageArea;
    ClientByNetty clientByNetty;
    private Image normalImg = getImage(ICON_NORMAL);
    private Image activeImg = getImage(ICON_ACTIVE);
    private Image trayImg = getImage(ICON_TRAY);
    private FlashManager flashManager;


    public static void main(String[] args) {
        init();
    }

    public static void init() {
        EventQueue.invokeLater(() -> {
            try {
                MessageNotifyFrame notifyFrame = new MessageNotifyFrame().initFrame()
                        .initTrayIcon().initFlash().initReconnTask();
                new Thread(() -> {
                    try {
                        notifyFrame.initWebsocket();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private MessageNotifyFrame initReconnTask() {
        new Thread(() -> {
            MessageNotifyFrame notifyFrame = MessageNotifyFrame.this;
            while (true) {
                try {
                    synchronized (notifyFrame) {
                        if (notifyFrame.clientByNetty == null) {
                            notifyFrame.wait(30000);
                            continue;
                        }
                        if (WebsocketStatusEnum.DISCONNECTED.getCode().equals(clientByNetty.getStatus())) {
                            addMessageln(addTimeFormat("重新连接中..."));
                            initWebsocket();
                        }
                        notifyFrame.wait(30000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return this;
    }

    public MessageNotifyFrame initFrame() {
        setTitle("消息盒子");
        setBounds(400, 100, 1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 这句可以注释掉的，用托盘右键退出程序即可。
        setVisible(true);
        setIconImage(getImage(ICON_NORMAL));
        addComponent();
        addWindowListener(new NotifyClientWindowListener());
        return this;
    }

    private void addComponent() {
        addMenu();
        addMessageArea();
    }

    private void addMenu() {
        final JMenuBar menuBar = new JMenuBar();    //创建菜单栏
        setJMenuBar(menuBar);    //把菜单栏放到窗体上
        final JMenu menu_set = new JMenu();    //创建文件菜单
        menu_set.setText("设置");    //为文件菜单设置标题
        menuBar.add(menu_set);    //把文件菜单添加到菜单栏上
        final JMenuItem serverSet = new JMenuItem("服务器设置");    //创建打开菜单项
        menu_set.add(serverSet);
        //为打开菜单项添加监听器
        serverSet.addActionListener(arg0 -> {
            openSetFrame();    //调用方法，操作文件
        });
    }

    private void openSetFrame() {
        EventQueue.invokeLater(() -> {
            try {
                MessageNotifySetFrame setFrame = new MessageNotifySetFrame(MessageNotifyFrame.this);
                setFrame.setVisible(true);
                setFrame.toFront();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void addMessageArea() {
        JPanel jp = new JPanel();
        JTextArea jta = new JTextArea("", 10, 100);
        jta.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
//        jta.setOpaque(true);
        jta.setBackground(new Color(165, 157, 172));
        jta.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(jta);
        Dimension size = jta.getPreferredSize();    //获得文本域的首选大小
        jsp.setBounds(0, 0, size.width, size.height);
        jsp.setBorder(new EmptyBorder(5, 5, 5, 5));
        jp.add(jsp);    //将JScrollPane添加到JPanel容器中
        add(jsp, BorderLayout.CENTER);
        this.messageArea = jta;
    }

    private Image getImage(String iconName) {
        return new ImageIcon(MessageNotifyFrame.class.getResource("/" + iconName)).getImage();
    }

    private void addMessage(Message message) {
        addMessageln(message.getTime() + " " + message.getTitle() + "（" + message.getSrcPack() + "）");
        addMessageln(message.getBody());
        addMessageln("");
    }

    public void addMessage(String message) {
        messageArea.append(message);
    }

    public void addMessageln(String message) {
        addMessage(message + "\r\n");
        refreshScrollPane();
    }

    private void refreshScrollPane() {
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }

    private void flashOnce() {
        flashManager.deskFlashOnce();
    }

    private MessageNotifyFrame initTrayIcon() {
        createTrayIcon();
        return this;
    }

    private void createTrayIcon() {
        // 实例化当前操作系统的托盘对象
        sysTray = SystemTray.getSystemTray();

        // Java托盘程序必须有一个右键菜单
        PopupMenu popupMenu = new PopupMenu();
        MenuItem menuOpen = new MenuItem("Open Msg");
        MenuItem menuExit = new MenuItem("Exit");
        popupMenu.add(menuOpen);
        popupMenu.add(menuExit);

        // 为右键弹出菜单项添加事件
        menuOpen.addActionListener(e -> openNotifyFrame(MessageNotifyFrame.this));
        menuExit.addActionListener(e -> System.exit(0));

        // 实例化托盘图标
        trayIcon = new TrayIcon(getImage(ICON_TRAY), "消息盒子", popupMenu);
        // 图标大小自适应
        trayIcon.setImageAutoSize(true);

        // 将托盘图标添入托盘
        try {
            sysTray.add(trayIcon);
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
    }


    private MessageNotifyFrame initFlash() {
        this.flashManager = new FlashManager(this);
        flashManager.deskFlash();
        flashManager.trayFlash();
        return this;
    }

    private void openNotifyFrame(MessageNotifyFrame notifyFrame) {
        EventQueue.invokeLater(() -> {
            notifyFrame.setVisible(true);
            notifyFrame.toFront();
        });
    }

    private MessageNotifyFrame initWebsocket() throws Exception {
        clientByNetty = new ClientByNetty(this);
        try {
            clientByNetty.init();
        } catch (Exception e) {
            String errorMsg = new String(e.getMessage().getBytes("utf-8"), "utf-8");
            Logger.info("初始化websocket失败-{}", errorMsg);
            addMessageln(StringUtil.addSquareEn(DateUtils.formatNowYmdhms()) + "初始化websocket失败:" + errorMsg);
        }
        return this;
    }

    public void setFlashManagerNewMsg(boolean newMsg) {
        flashManager.setNewMsg(newMsg);
    }

    public void obtainMessage(Message message) {
        //更新newMsg和判断-执行闪动做同步
        setFlashManagerNewMsg(true);
        addMessage(message);
        flashOnce();
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void configServer(String host, Integer port) {
        boolean notNeedReconnect = StringUtils.equals(this.host, host) && this.port.equals(port)
                && WebsocketStatusEnum.CONNECTED.getCode().equals(clientByNetty.getStatus());
        if (notNeedReconnect) {
            return;
        }
        this.host = host;
        this.port = port;
        try {
            clientByNetty.close();
            initWebsocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onConnChanged(WebsocketStatusEnum statusEnum) {
        addMessageln(addTimeFormat(statusEnum.getName()));
    }

    private class NotifyClientWindowListener implements WindowListener {
        @Override
        public void windowOpened(WindowEvent e) {
            flashManager.addEvent(e);
            setFlashManagerNewMsg(false);
        }

        @Override
        public void windowClosing(WindowEvent e) {
            flashManager.addEvent(e);
            setFlashManagerNewMsg(false);
        }

        @Override
        public void windowClosed(WindowEvent e) {
            flashManager.addEvent(e);
            setFlashManagerNewMsg(false);
        }

        @Override
        public void windowIconified(WindowEvent e) {
            flashManager.addEvent(e);
            setFlashManagerNewMsg(false);
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            flashManager.addEvent(e);
            setFlashManagerNewMsg(false);
        }

        @Override
        public void windowActivated(WindowEvent e) {
            flashManager.addEvent(e);
            setFlashManagerNewMsg(false);
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            flashManager.addEvent(e);
            setFlashManagerNewMsg(false);
        }
    }


    private class FlashManager {
        private final MessageNotifyFrame notifyFrame;
        private volatile boolean newMsg = false;
        private Object newMsgLock = new Object();
        private Object flashLock = new Object();


        Deque<WindowEvent> eventQueue = new LinkedBlockingDeque<>(2);
        private int ID_ACTIVE = 205;
        private int ID_DEACTIVE = 206;
        private int ID_ICONIFIED = 203;
        private int ID_DEICONIFIED = 204;
        private int ID_OPENED = 200;
        private int ID_CLOSING = 201;


        public FlashManager(MessageNotifyFrame notifyFrame) {
            this.notifyFrame = notifyFrame;
        }

        public void deskFlashOnce() {
            try {
                synchronized (flashLock) {
                    setIconImage(activeImg);
                    flashLock.wait(500L);
                    setIconImage(normalImg);
                    flashLock.wait(500L);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void trayFlashOnce() {
            try {
                synchronized (flashLock) {
                    trayIcon.setImage(activeImg);
                    flashLock.wait(500);
                    trayIcon.setImage(trayImg);
                    flashLock.wait(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void syncWait(long timeout) {
            try {
                synchronized (flashLock) {
                    flashLock.wait(timeout);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        public void deskFlash() {
            new Thread(() -> {
                while (true) {
                    try {
                        if (shouldFlash()) {
                            deskFlashOnce();
                        } else {
                            if (getIconImage() != normalImg) {
                                setIconImage(normalImg);
                            }
                            syncWait(500L);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, "deskFlash").start();
        }

        public void trayFlash() {
            new Thread(() -> {
                while (true) {
                    try {
                        if (shouldFlash()) {
                            trayFlashOnce();
                        } else {
                            if (trayIcon.getImage() != trayImg) {
                                trayIcon.setImage(trayImg);
                            }
                            syncWait(500L);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, "trayFlash").start();
        }


        public void addEvent(WindowEvent e) {
            if (eventQueue.size() >= 2) {
                eventQueue.poll();
            }
            eventQueue.offer(e);
        }

        private boolean shouldFlash() {
            WindowEvent last = eventQueue.peekLast();
            if (last == null) {
                return false;
            }
            boolean windowFlashState = false;
            if (last.getID() == ID_ICONIFIED) {
                windowFlashState = true;
            }
            if (eventQueue.size() >= 2) {
                WindowEvent peek = eventQueue.peek();
                if (peek.getID() == ID_ICONIFIED && last.getID() == ID_DEACTIVE) {
                    windowFlashState = true;
                }
            }
            return newMsg && windowFlashState;
        }

        public void syncSetNewMsg(boolean newMsg) {
            synchronized (newMsgLock) {
                setNewMsg(newMsg);
            }
        }

        public void setNewMsg(boolean newMsg) {
            this.newMsg = newMsg;
        }
    }
}

package com.tuanbaol.messageclient;

public class Application {
    private static String DEFAULT_LOG_PATH = "/log/msgnotifier";

    public static void main(String[] args) {
        Logger.init(DEFAULT_LOG_PATH);
        MessageNotifyFrame.init();
    }
}

package com.tuanbaol.messageclient;

import com.tuanbaol.messageclient.exception.ServiceException;
import com.tuanbaol.messageclient.util.DateUtils;
import com.tuanbaol.messageclient.util.StringUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static com.tuanbaol.messageclient.util.StringUtil.*;

public class Logger {
    private static LogWriter writer;

    public static void init(String filePath) {
        writer = new LogWriter(filePath);
    }

    public static void info(String msg,Object... params) {
        writer.writeln(decorate(formatByRegex(msg,params),"INFO"));
    }

    public static void error(String msg,Object... params) {
        writer.writeln(decorate(formatByRegex(msg,params),"ERROR"));
    }

    public static void warn(String msg,Object... params) {
        writer.writeln(decorate(formatByRegex(msg,params),"WARN"));
    }

    public static String decorate(String msg, String level) {
        return addSquareEn(level) + addSquareEn(DateUtils.formatNowYmdhms()) + orDefault(msg);
    }

    static class LogWriter {
        private String filePath;
        private File logFile;

        public LogWriter(String filePath) {
            this.filePath = filePath;
            buildFile();
        }

        public LogWriter(File logFile) {
            this.logFile = logFile;
        }

        private void buildFile() {
            filePath = endWithReplace(filePath, "/", "");
            File dir = new File(filePath);
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs();
            }
            try {
                File file = new File(filePath+"/"+getLogName());
                if (!file.exists()) {
                    boolean newFile = file.createNewFile();
                    if (!newFile) {
                        throw new ServiceException("创建日志文件失败。");
                    }
                }
                logFile = file;
            } catch (Exception e) {
                throw new ServiceException("创建日志文件失败。", e);
            }
        }

        public String getLogName() {
            return "log_" + DateUtils.formatNowYmd() + ".log";
        }

        public LogWriter write(String msg) {
            msg = StringUtil.orDefault(msg);
            try {
                FileUtils.write(logFile, msg, "utf-8", true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }

        public LogWriter writeln(String msg) {
            write(StringUtil.orDefault(msg) + "\r\n");
            return this;
        }

    }

    public static void main(String[] args) {
        Logger.init("/log/msg/log.log");
    }
}

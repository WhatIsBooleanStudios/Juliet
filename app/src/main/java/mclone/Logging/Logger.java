package mclone.Logging;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Logger {
    public Logger(boolean logFile, String filePath) {
        logToFile = logFile;
        if (logToFile)
            pathToFile = filePath;

        logToFile(this, null, null);

    }

    public static Logger get() {
        if (staticLogger == null) {
            staticLogger = new Logger(true, pathToFile);
        }

        return staticLogger;
    }

    public enum LogType {
        ERROR,
        WARN,
        TRACE,
        INFO
    }

    private static String getTimestamp() {
        return "[" + java.time.LocalTime.now().getHour() + ":"
                + java.time.LocalTime.now().getMinute() + ":"
                + java.time.LocalTime.now().getSecond() + "]";
    }

    private void logToFile(Object obj, LogType logType, String msg) {
        try {
            File file = new File(pathToFile);
            if (!file.isFile())
                if (!file.createNewFile())
                    throw new Exception("file path error");

            FileReader reader = new FileReader(file);
            String current = "";
            int character;

            while ((character = reader.read()) != -1) {
                current += (char) character;
            }

            reader.close();
            String className = obj == null ? "" : obj.getClass().getName();
            FileWriter writer = new FileWriter(file);
            String fileMsg = "\n\nLogging started at " + getTimestamp() + "\n-------------------------------";
            if (logType != null && msg != null)
                fileMsg = switch (logType) {
                    case ERROR -> "\nERROR " + getTimestamp() + " " + className + " " + msg;
                    case INFO -> "\nINFO  " + getTimestamp() + " " + className + " " + msg;
                    case TRACE -> "\nTRACE " + getTimestamp() + " " + className + " " + msg;
                    case WARN -> "\nWARN  " + getTimestamp() + " " + className + " " + msg;
                };
            writer.write(current + fileMsg);

            writer.flush();
            writer.close();
        } catch (Exception e) {
            handleMessage(obj, LogType.ERROR, e.getMessage());
        }
    }

    private boolean handleMessage(Object obj, LogType logType, String msg) {
        if (msg == null)
            return false;

        switch (logType) {
            case ERROR -> System.out.print("\033[31;49;1mERROR\033[37;49;1m " + getTimestamp());
            case INFO -> System.out.print("\033[32;49;1mINFO\033[37;49;1m  " + getTimestamp());
            case TRACE -> System.out.print("\033[34;49;1mTRACE\033[37;49;1m " + getTimestamp());
            case WARN -> System.out.print("\033[33;49;1mWARN\033[37;49;1m  " + getTimestamp());
        }
        System.out.print("\033[37;49m " + msg + "\033[0m\n");

        if (!msg.equals("file path error")) {
            if (logToFile)
                this.logToFile(obj, logType, msg);
            return true;
        } else
            return false;

    }

    public int error(Object obj, String msg) {
        if (obj == null) {
            error(this.getClass(), "invalid class ptr");
            return 1;
        }

        if (msg == null || msg.equals("")) {
            error(obj, "invalid message");
            return 2;
        }

        if (handleMessage(obj, LogType.ERROR, msg))
            return 0;

        return 3;
    }

    public int error(String msg) {

        if (msg == null || msg.equals("")) {
            error("invalid message");
            return 2;
        }

        if (handleMessage(null, LogType.ERROR, msg))
            return 0;

        return 3;
    }

    public int warn(Object obj, String msg) {
        if (obj == null) {
            error(this.getClass(), "invalid class ptr");
            return 1;
        }

        if (msg == null || msg.equals("")) {
            error(obj, "invalid message");
            return 2;
        }

        if (handleMessage(obj, LogType.WARN, msg))
            return 0;

        return 3;
    }

    public int trace(Object obj, String msg) {
        if (obj == null) {
            error(this.getClass(), "invalid class ptr");
            return 1;
        }

        if (msg == null || msg.equals("")) {
            error(obj, "invalid message");
            return 2;
        }

        if (handleMessage(obj, LogType.TRACE, msg))
            return 0;

        return 3;
    }

    public int info(Object obj, String msg) {
        if (obj == null) {
            error(this.getClass(), "invalid class ptr");
            return 1;
        }

        if (msg == null || msg.equals("")) {
            error(obj, "invalid message");
            return 2;
        }

        if (handleMessage(obj, LogType.INFO, msg))
            return 0;

        return 3;
    }

    private static Logger staticLogger = null;
    private static String pathToFile = "log.txt";
    private static boolean logToFile = false;
}

package mclone.Logging;

import java.io.*;

public class Logger {
    public static void initialize(boolean logToFile, String pathToFile) {
        Logger.logToFile = logToFile;
        Logger.pathToFile = pathToFile;

        if(logToFile) {
            try {
                File logFile = new File(pathToFile);
                if (!logFile.isFile() && !logFile.createNewFile())
                    error("File path error!");

                fileOutputStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(logFile, true)));
                logHeaderToFile();

            } catch (Exception e) {
                error(e.getMessage());
            }
        }
    }

    public static void shutdown() {
        if(fileOutputStream != null)
            fileOutputStream.close();
    }

    private Logger() {}
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

    private static void logHeaderToFile() {
        String fileMsg = "\n\nLogging started at " + getTimestamp() + "\n-------------------------------";
        fileOutputStream.println(fileMsg);
    }

    private static void logToFile(String caller, Object obj, LogType logType, String msg) {
        try {
            String messageSource =
                (obj == null ? "" : obj.toString()) +
                (caller.equals("") || obj == null ? "" : "@") +
                caller;
            String toPrint = switch (logType) {
                case ERROR -> "ERROR " + getTimestamp() + " " + messageSource + " " + msg;
                case INFO -> "INFO  " + getTimestamp() + " " + messageSource + " " + msg;
                case TRACE -> "TRACE " + getTimestamp() + " " + messageSource + " " + msg;
                case WARN -> "WARN  " + getTimestamp() + " " + messageSource + " " + msg;
            };

            fileOutputStream.println(toPrint);
        } catch (Exception e) {
            handleMessage("Logger.logToFile", obj, LogType.ERROR, e.getMessage());
        }
    }

    private static boolean handleMessage(String caller, Object obj, LogType logType, String msg) {
        if (msg == null)
            return false;
        switch (logType) {
            case ERROR -> System.out.print("\033[31;49;1mERROR\033[37;49;1m " + getTimestamp());
            case INFO -> System.out.print("\033[32;49;1mINFO\033[37;49;1m  " + getTimestamp());
            case TRACE -> System.out.print("\033[34;49;1mTRACE\033[37;49;1m " + getTimestamp());
            case WARN -> System.out.print("\033[33;49;1mWARN\033[37;49;1m  " + getTimestamp());
        }

        if(obj != null) {
            System.out.print(obj);
        }

        if(caller != null && !caller.isEmpty()) {
            if(obj != null) {
                System.out.print("@");
            }
            System.out.print(caller);
        }
        System.out.print("\033[37;49m " + msg + "\033[0m\n");

        if (logToFile) {
            logToFile(caller, obj, logType, msg);
        }

        return true;
    }

    public static int error(String caller, Object obj, String msg) {
        if (handleMessage(caller, obj, LogType.ERROR, msg))
            return 0;

        return 3;
    }

    public static int error(Object obj, String msg) {
        if (handleMessage("", obj, LogType.ERROR, msg)) {
            return 0;
        }

        return 3;
    }

    public static int error(String caller, String msg) {
        if (handleMessage(caller, null, LogType.ERROR, msg)) {
            return 0;
        }

        return 3;
    }

    public static int error(String msg) {

        if (msg == null || msg.equals("")) {
            error("invalid message");
            return 2;
        }

        if (handleMessage("", null, LogType.ERROR, msg))
            return 0;

        return 3;
    }

    public static int warn(String caller, Object obj, String msg) {
        if (handleMessage(caller, obj, LogType.WARN, msg)) {
            return 0;
        }

        return 3;
    }

    public static int warn(Object obj, String msg) {
        if (handleMessage("", obj, LogType.WARN, msg)) {
            return 0;
        }

        return 3;
    }

    public static int warn(String caller, String msg) {
        if(handleMessage(caller, null, LogType.WARN, msg)) {
            return 0;
        }

        return 3;
    }

    public static int warn(String msg) {
        if (handleMessage("", null, LogType.WARN, msg)) {
            return 0;
        }

        return 3;
    }

    public static int trace(String caller, Object obj, String msg) {
        if (handleMessage(caller, obj, LogType.TRACE, msg))
            return 0;

        return 3;
    }
    public static int trace(Object obj, String msg) {
        if (handleMessage("", obj, LogType.TRACE, msg))
            return 0;

        return 3;
    }

    public static int trace(String caller, String msg) {
        if (handleMessage(caller, null, LogType.TRACE, msg)) {
            return 0;
        }

        return 3;
    }

    public static int trace(String msg) {
        if (handleMessage("", null, LogType.TRACE, msg))
            return 0;

        return 3;
    }

    public static int info(String caller, Object obj, String msg) {
        if (handleMessage(caller, obj, LogType.INFO, msg))
            return 0;

        return 3;
    }
    public static int info(Object obj, String msg) {
        if (handleMessage("", obj, LogType.INFO, msg))
            return 0;

        return 3;
    }

    public static int info(String caller, String msg) {
        if(handleMessage(caller, null, LogType.INFO, msg)) {
            return 0;
        }

        return 3;
    }

    public static int info(String msg) {
        if(handleMessage("", null, LogType.INFO, msg)) {
            return 0;
        }

        return 3;
    }

    private static String pathToFile = "log.txt";
    private static PrintStream fileOutputStream = null;
    private static boolean logToFile = false;
}

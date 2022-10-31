package mclone.Logging;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Logger {
    public Logger(boolean logToFile, String pathToFile) {
        this.m_LogToFile = logToFile;
        if (this.m_LogToFile)
            this.m_PathToFile = pathToFile;

        logToFile(this, null, null);

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
            File file = new File(this.m_PathToFile);
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

            FileWriter writer = new FileWriter(file);
            String fileMsg = "\n\nLogging started at " + getTimestamp() + "\n-------------------------------";
            if (logType != null && msg != null)
                switch (logType) {
                    case ERROR:
                        fileMsg = "\nERROR " + getTimestamp() + " " + obj.getClass().getName() + " " + msg;
                        break;
                    case INFO:
                        fileMsg = "\nINFO  " + getTimestamp() + " " + obj.getClass().getName() + " " + msg;
                        break;
                    case TRACE:
                        fileMsg = "\nTRACE " + getTimestamp() + " " + obj.getClass().getName() + " " + msg;
                        break;
                    case WARN:
                        fileMsg = "\nWARN  " + getTimestamp() + " " + obj.getClass().getName() + " " + msg;
                        break;
                }
            writer.write(current + fileMsg);

            writer.flush();
            writer.close();
        } catch (Exception e) {
            handleMessage(obj, LogType.ERROR, e.getMessage());
        }
    }

    private boolean handleMessage(Object obj, LogType logType, String msg) {
        if(msg == null)
            return false;
            
        switch (logType) {
            case ERROR:
                System.out.print("\033[31;49;1mERROR\033[37;49;1m " + getTimestamp());
                break;
            case INFO:
                System.out.print("\0\033[32;49;1mINFO\033[37;49;1m  " + getTimestamp());
                break;
            case TRACE:
                System.out.print("\033[34;49;1mTRACE\033[37;49;1m " + getTimestamp());
                break;
            case WARN:
                System.out.print("\033[33;49;1mWARN\033[37;49;1m  " + getTimestamp());
                break;
            default:
                System.out.print("\033[31;49;1mERROR\033[37;49;1m " + getTimestamp());
                break;

        }
        System.out.print("\033[37;49m " + msg + "\033[0m\n");

        if (!msg.equals("file path error")) {
            if(this.m_LogToFile)
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

    private String m_PathToFile;
    private boolean m_LogToFile;
}

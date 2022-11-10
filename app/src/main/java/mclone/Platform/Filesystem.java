package mclone.Platform;

import mclone.Logging.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Filesystem {

    public static byte[] getFileBytesFromResourceDirectory(Class cls, String name) {
        try {
            return cls.getResourceAsStream(name).readAllBytes();
        } catch(Exception e) {
            Logger.error("Filesystem.getFileBytesFromResourceDirectory", "Failed to load file!");
            Logger.error(e.getMessage());
            return null;
        }
    }

    public static byte[] getFileBytesFromResourceDirectory(String name) {
        return getFileBytesFromResourceDirectory(Filesystem.class, name);
    }

    public static String getFileTextFromResourceDirectory(Class cls, String name) {
        return new String(Objects.requireNonNull(getFileBytesFromResourceDirectory(cls, name)), StandardCharsets.UTF_8);
    }

    public static String getFileTextFromResourceDirectory(String name) {
        return new String(Objects.requireNonNull(getFileBytesFromResourceDirectory(name)), StandardCharsets.UTF_8);
    }

    private Filesystem() {} // Only static members
}

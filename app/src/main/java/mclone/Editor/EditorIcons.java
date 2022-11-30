package mclone.Editor;

import mclone.GFX.OpenGL.Texture;
import mclone.Platform.Filesystem;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

class EditorIcons {
    private EditorIcons() {}

    public static void load() {
        addTexture = loadTexture("/IonicIcons/add.png");
    }

    public static void dispose() {
        addTexture.dispose();
    }

    private static Texture loadTexture(String path) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] dataBytes = Filesystem.getFileBytesFromResourceDirectory(path);

            ByteBuffer byteBuffer = stack.malloc(dataBytes.length);
            byteBuffer.put(dataBytes);
            byteBuffer.flip();

            return new Texture(path, byteBuffer, new int[1], new int[1], true);
        }
    }

    public static Texture addTexture;
}

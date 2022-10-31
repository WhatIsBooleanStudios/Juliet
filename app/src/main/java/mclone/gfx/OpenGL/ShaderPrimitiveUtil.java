package mclone.gfx.OpenGL;

import static org.lwjgl.opengl.GL33.*;

public class ShaderPrimitiveUtil {
    public enum ShaderPrimitiveType {
        FLOAT32,
        FLOAT64,
        INT32,
        UINT32,
        INT16,
        UINT16
    }

    public static int getSizeOfType(ShaderPrimitiveType type) {
        switch (type) {
            case FLOAT32:
            case INT32:
            case UINT32:
                return 4;
            case FLOAT64:
                return 8;
            case INT16:
            case UINT16:
                return 2;
        }

        return -1;
    }

    public static int mapShaderTypeToGLType(ShaderPrimitiveType type) {
        switch(type) {
            case FLOAT32:
                return GL_FLOAT;
            case FLOAT64:
                return GL_DOUBLE;
            case INT32:
                return GL_INT;
            case UINT32:
                return GL_UNSIGNED_INT;
            case INT16:
                return GL_SHORT;
            case UINT16:
                return GL_UNSIGNED_SHORT;
        }

        return -1;
    }
}

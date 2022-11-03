package mclone.gfx.OpenGL;

import static org.lwjgl.opengl.GL33.*;

public class ShaderPrimitiveUtil {
    public enum ShaderPrimitiveType {
        FLOAT32,
        FLOAT64,
        INT32,
        UINT32,
        INT16,
        UINT16,

        VEC2,
        VEC3,
        VEC4,
        MAT2,
        MAT3,
        MAT4
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
            case VEC2:
                return 4 * 2;
            case VEC3:
                return 4 * 3;
            case VEC4:
                return 4 * 4;
            case MAT2:
                return 2 * 2 * 4;
            case MAT3:
                return 3 * 3 * 4;
            case MAT4:
                return 4 * 4 * 4;
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
            case VEC2:
                return GL_FLOAT_VEC2;
            case VEC3:
                return GL_FLOAT_VEC3;
            case VEC4:
                return GL_FLOAT_VEC4;
            case MAT2:
                return GL_FLOAT_MAT2;
            case MAT3:
                return GL_FLOAT_MAT3;
            case MAT4:
                return GL_FLOAT_MAT4;
        }

        return -1;
    }
}

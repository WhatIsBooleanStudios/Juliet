package mclone.gfx.OpenGL;

import static org.lwjgl.opengl.GL33C.*;

public abstract class HardwareBuffer {
    enum UsageHints {
        USAGE_DYNAMIC,
        USAGE_STATIC
    }

    public static int usageHintToGLUsageHint(UsageHints hint) {
        int GL_usageHint = 0;
        switch (hint) {
            case USAGE_DYNAMIC:
                GL_usageHint = GL_DYNAMIC_DRAW;
                break;
            case USAGE_STATIC:
                GL_usageHint = GL_STATIC_DRAW;
                break;
        }

        return GL_usageHint;
    }

    public abstract void bind();
    public abstract void unBind();

    public abstract long getMaxSize();

    public abstract void dispose();
}

package juliet.GFX.OpenGL;

import static org.lwjgl.opengl.GL33C.*;

/**
 * Base type for all OpenGL buffers
 */
public abstract class HardwareBuffer {
    /**
     * Allocation hints for buffers
     */
    public enum UsageHints {
        /**
         * The buffer will be written to and read often
         */
        USAGE_DYNAMIC,

        /**
         * The buffer will be read frequently but not written to
         */
        USAGE_STATIC
    }

    /**
     * Map juliet usage hints to OpenGL usage hints
     * @param hint The juliet usage hint
     * @return The corresponding OpenGL usage hint
     */
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

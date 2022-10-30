package mclone.gfx.OpenGL;

public abstract class HardwareBuffer {
    enum UsageHints {
        USAGE_DYNAMIC,
        USAGE_STATIC
    }

    public abstract void bind();
    public abstract void unBind();

    public abstract long getMaxSize();

    public abstract void dispose();
}

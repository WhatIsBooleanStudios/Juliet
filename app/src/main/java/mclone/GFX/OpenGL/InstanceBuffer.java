package mclone.GFX.OpenGL;

import mclone.Logging.Logger;

import java.nio.Buffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryUtil.memAddress;

public class InstanceBuffer extends HardwareBuffer {
    public InstanceBuffer(Buffer data, long size, UsageHints usageHints) {
        id = glGenBuffers();
        setInitializeBufferData(data, size, usageHints);
        maxSize = size;
    }

    private void setInitializeBufferData(Buffer data, long size, UsageHints hint) {
        glBindBuffer(GL_ARRAY_BUFFER, id);
        int GL_usageHint = 0;
        switch (hint) {
            case USAGE_DYNAMIC:
                GL_usageHint = GL_DYNAMIC_DRAW;
                break;
            case USAGE_STATIC:
                GL_usageHint = GL_STATIC_DRAW;
                break;
        }
        if (data != null)
            nglBufferData(GL_ARRAY_BUFFER, size, memAddress(data), GL_usageHint);
        else if (size > 0) {
            glBufferData(GL_ARRAY_BUFFER, size, GL_usageHint);
        } else {
            Logger.error("InstanceBuffer.setInitializeVBOData", this, "If data is null, size must be > 0");
        }
    }

    @Override
    public void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, id);
    }

    @Override
    public void unBind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public long getMaxSize() {
        return maxSize;
    }

    @Override
    public void dispose() {
        glDeleteBuffers(id);
    }

    public void setData(Buffer data, long size) {
        bind();
        nglBufferSubData(GL_ARRAY_BUFFER, 0, size, memAddress(data));
    }

    protected int id;
    private long maxSize;
}

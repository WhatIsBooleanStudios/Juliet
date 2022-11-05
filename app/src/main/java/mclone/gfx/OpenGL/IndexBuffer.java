package mclone.gfx.OpenGL;

import mclone.Logging.Logger;

import java.nio.Buffer;

import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryUtil.*;

public class IndexBuffer extends HardwareBuffer {

	public IndexBuffer(Buffer data, long size, UsageHints usage) {
        id = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
        int GL_usageHint = usageHintToGLUsageHint(usage);
        if(data != null) {
            nglBufferData(GL_ELEMENT_ARRAY_BUFFER, size, memAddress(data), GL_usageHint);
        } else if(size > 0) {
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, size, GL_usageHint);
        } else {
            Logger.error("IndexBuffer.new", this, "If data is null then size must be greater than 0");
        }

        maxSize = size;
    }

    public void setData(Buffer data, long size) {
        if(size > maxSize) {
            Logger.error("IndexBuffer.setData", this, "Data size must be less than maximum size!");
            return;
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
        nglBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, size, memAddress(data));
    }

    @Override
    public void bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    }

    @Override
    public long getMaxSize() {
        return maxSize;
    }

    @Override
    public void unBind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public void dispose() {
        glDeleteBuffers(id);
        id = 0;
    }

    @Override
    public String toString() {
        return "IndexBuffer(id=" + id + ")";
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(id != 0) {
            Logger.warn("IndexBuffer.finalize", this, "Garbage collection called but object not freed!");
        }
    }

    private int id = 0;
    private long maxSize;

}

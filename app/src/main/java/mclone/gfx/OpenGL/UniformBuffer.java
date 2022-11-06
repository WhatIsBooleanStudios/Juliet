package mclone.gfx.OpenGL;

import mclone.Logging.Logger;

import java.nio.Buffer;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL33C.*;

/**
 * Stores GPU uniform data in a OpenGL buffer for more efficient access by shaders.
 * Essentially, these buffers are bound to binding points and multiple shaders can then connect their
 * uniform blocks to these binding points to access the data within them.
 * @see Shader#setUniformBuffer(String, int)
 */
public class UniformBuffer extends HardwareBuffer {
    /**
     * Create a Uniform Buffer
     * @param data The data to be copied into the buffer can be null if size > 0
     * @param size The size in bytes of data. If data is null, the constructor creates an empty buffer with this size
     * @param usage GPU allocation hints for OpenGL
     */
    public UniformBuffer(Buffer data, long size, HardwareBuffer.UsageHints usage) {
        id = glGenBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, id);
        int GL_Usage = HardwareBuffer.usageHintToGLUsageHint(usage);
        if(data != null && data.remaining() > 0) {
            nglBufferData(GL_UNIFORM_BUFFER, size, memAddress(data), GL_Usage);
        } else if(size > 0) {
            glBufferData(GL_UNIFORM_BUFFER, size, GL_Usage);
        } else {
            Logger.error("UniformBuffer.new", this, "Buffer must be non-null and have contents if size <= 0 in UniformBuffer");
        }

        maxSize = size;
    }

    public void setData(Buffer data, long size) {
        bind();
        if(size > maxSize) {
            Logger.error("UniformBuffer.setData", this, "Uniform buffer is too small for a data size of " + size);
            return;
        }
        nglBufferSubData(GL_UNIFORM_BUFFER, 0, size, memAddress(data));
    }

	@Override
	public void bind() {
        glBindBuffer(GL_UNIFORM_BUFFER, id);
	}

	@Override
	public void unBind() {
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
	}

    public void setToBindingPoint(int bindingPoint) {
        bind();
        glBindBufferBase(GL_UNIFORM_BUFFER, bindingPoint, id);
    }

	@Override
	public long getMaxSize() {
        return maxSize;
	}

	@Override
	public void dispose() {
        glDeleteBuffers(id);
        id = 0;
	}

    @Override
    public String toString() {
        return "UniformBuffer(id=" + id + ")";
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(id != 0) {
            Logger.warn("UniformBuffer.finalize", this, "Garbage collection called but object not disposed.");
        }
    }

    private long maxSize = 0;
    private int id;
}

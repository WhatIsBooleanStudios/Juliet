package mclone.GFX.OpenGL;

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

    /**
     * Copy data into the UniformBuffer
     * @param data The Data to be copied into the UniformBuffer
     * @param size The size of the data where 0 < size < UniformBuffer#getMaxSize()
     */
    public void setData(Buffer data, long size) {
        bind();
        if(size > maxSize) {
            Logger.error("UniformBuffer.setData", this, "Uniform buffer is too small for a data size of " + size);
            return;
        }
        nglBufferSubData(GL_UNIFORM_BUFFER, 0, size, memAddress(data));
    }

    /**
     * Bind the uniform buffer
     */
    @Override
	public void bind() {
        glBindBuffer(GL_UNIFORM_BUFFER, id);
	}

    /**
     * UnBind the uniform buffer
     */
    @Override
	public void unBind() {
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
	}

    /**
     * Bind the uniform buffer to the given binding point
     * @param bindingPoint The binding point to set the uniform buffer too. Must be negative or exceed the maximum defined
     *                     by the hardware/drivers
     */
    public void setToBindingPoint(int bindingPoint) {
        bind();
        glBindBufferBase(GL_UNIFORM_BUFFER, bindingPoint, id);
    }

    /**
     * Get the max size in bytes of the UniformBuffer (set in the constructor)
     * @return The maximum size of the UniformBuffer in bytes
     */
	@Override
	public long getMaxSize() {
        return maxSize;
	}

    /**
     * Free the memory and objects associated with this UniformBuffer
     */
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

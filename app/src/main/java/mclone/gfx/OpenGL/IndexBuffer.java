package mclone.gfx.OpenGL;

import mclone.Logging.Logger;

import java.nio.Buffer;

import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * A wrapper around the OpenGL Index Buffer type which is a buffer that tells the order of the vertices in a vertex buffer
 * to draw which allows the user to save GPU-bandwidth by reducing the number of bytes sent to the GPU and reduces
 * vertex duplication
 */
public class IndexBuffer extends HardwareBuffer {

    /**
     * Create an index buffer
     * @param data The data to be copied into the index buffer. Can be null.
     * @param size The size of the data to be copied into the buffer. If data is null, an empty buffer of size is allocated
     * @param usage Allocation hints for the GPU
     */
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

    /**
     * Set a subsection of the data in the buffer
     * @param data The data to copy in
     * @param size The size of the data to copy in where 0 < size < getMaxSize()
     */
    public void setData(Buffer data, long size) {
        if(size > maxSize) {
            Logger.error("IndexBuffer.setData", this, "Data size must be less than maximum size!");
            return;
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
        nglBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, size, memAddress(data));
    }

    /**
     * Bind the index buffer
     */
    @Override
    public void bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    }

    /**
     * @return The maximum size of the buffer
     */
    @Override
    public long getMaxSize() {
        return maxSize;
    }

    /**
     * UnBind the buffer set the state such that no buffer is bound.
     */
    @Override
    public void unBind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Free the memory and objects associated with this buffer
     */
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

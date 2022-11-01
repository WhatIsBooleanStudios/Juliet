package mclone.gfx.OpenGL;

import java.nio.ByteBuffer;
import java.nio.Buffer;

import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryUtil.*;

public class IndexBuffer extends HardwareBuffer {

	public IndexBuffer(Buffer data, long size, UsageHints usage) {
        m_ID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_ID);
        int GL_usageHint = usageHintToGLUsageHint(usage);
        if(data != null) {
            nglBufferData(GL_ELEMENT_ARRAY_BUFFER, size, memAddress(data), GL_usageHint);
        } else if(size > 0) {
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, size, GL_usageHint);
        } else {
            System.out.println("IndexBuffer: if data is null then size must be greater than 0");
        }

        m_maxSize = size;
    }

    public void setData(Buffer data, long size) {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_ID);
        nglBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, size, memAddress(data));
    }

    @Override
    public void bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_ID);
    }

    @Override
    public long getMaxSize() {
        return m_maxSize;
    }

    @Override
    public void unBind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public void dispose() {
        glDeleteBuffers(m_ID);
    }

    private int m_ID = 0;
    private long m_maxSize;

}

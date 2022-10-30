package mclone.gfx;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL33C.*;

public class VertexBuffer extends HardwareBuffer {
    public VertexBuffer(ByteBuffer data, long size, UsageHints hint, VertexBufferLayout layout) {
        m_layout = layout;
        m_maxSize = size;

        createVBO();
        createVAO();
    }

    private void createVAO() {
        m_VAOID = glGenVertexArrays();
    }

    private void setInitializeVBOData(ByteBuffer data, long size, UsageHints hint) {
        glBindBuffer(GL_ARRAY_BUFFER, m_VBOID);
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
            glBufferData(GL_ARRAY_BUFFER, data, GL_usageHint);
        else if (size > 0) {
            glBufferData(GL_ARRAY_BUFFER, size, GL_usageHint);
        } else {
            System.out.println("VertexBuffer: If data is null, size must be > 0");
        }
    }

    private void setVertexBufferToVertexArray() {
        glBindVertexArray(m_VAOID);
        glBindBuffer(GL_VERTEX_ARRAY, m_VBOID);

        long offset = 0;
        for (int i = 0; i < m_layout.getNumAttributes(); i++) {
            VertexBufferLayout.VertexAttribute attrib = m_layout.getAttribute(i);
            ShaderPrimitiveUtil.ShaderPrimitiveType type = attrib.getType();
            if (type == ShaderPrimitiveUtil.ShaderPrimitiveType.UINT32  ||
                type == ShaderPrimitiveUtil.ShaderPrimitiveType.INT32   ||
                type == ShaderPrimitiveUtil.ShaderPrimitiveType.UINT16  ||
                type == ShaderPrimitiveUtil.ShaderPrimitiveType.INT16) {
                glVertexAttribIPointer(i, attrib.getCount(), ShaderPrimitiveUtil.mapShaderTypeToGLType(type), m_layout.getStride(), offset);
            } else if(type == ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32) {
                glVertexAttribPointer(i, attrib.getCount(), ShaderPrimitiveUtil.mapShaderTypeToGLType(type), false, m_layout.getStride(), offset);
            } else if(type == ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT64) {
                System.out.println("OpenGL 3.3 does not support 64 bit floats in vertex arrays!");
                // TODO: consider upgrading to opengl 4.5 which was released in 2014
                //glVertexAttribLPointer(i, attrib.getCount(), ShaderPrimitiveUtil.mapShaderTypeToGLType(type), false, m_layout.getStride(), offset);
            }
            glEnableVertexAttribArray(i);

            offset += (long) ShaderPrimitiveUtil.getSizeOfType(type) * attrib.getCount();
        }
    }

    private void createVBO() {
        m_VBOID = glGenBuffers();
    }

    @Override
    public void bind() {
        glBindVertexArray(m_VAOID);
    }

    @Override
    public void unBind() {
        glBindVertexArray(0);
    }

    @Override
    public long getMaxSize() {
        return m_maxSize;
    }

    @Override
    public void dispose() {
        glDeleteBuffers(m_VBOID);
        glDeleteVertexArrays(m_VAOID);
    }

    private int m_VAOID = 0;
    private int m_VBOID = 0;
    private VertexBufferLayout m_layout;
    private final long m_maxSize;
}

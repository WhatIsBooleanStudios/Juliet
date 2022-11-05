package mclone.gfx.OpenGL;

import mclone.Logging.Logger;

import static org.lwjgl.system.MemoryUtil.*;
import java.nio.Buffer;

import static org.lwjgl.opengl.GL33C.*;

public class VertexBuffer extends HardwareBuffer {
    public VertexBuffer(Buffer data, long size, UsageHints hint, VertexBufferLayout layout) {
        this.layout = layout;
        maxSize = size;

        createVBO();
        setInitializeVBOData(data, size, hint);
        createVAO();
        setVertexBufferToVertexArray();
    }

    private void createVAO() {
        vaoID = glGenVertexArrays();
    }

    private void setInitializeVBOData(Buffer data, long size, UsageHints hint) {
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
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
            Logger.error("VertexBuffer.setInitializeVBOData", this, "If data is null, size must be > 0");
        }
    }

    public void setData(Buffer data, long size) {
        glBindVertexArray(vaoID);
        nglBufferSubData(GL_VERTEX_ARRAY, 0, size, memAddress(data));
    }

    private void setVertexBufferToVertexArray() {
        glBindVertexArray(vaoID);
        glBindBuffer(GL_ARRAY_BUFFER, vboID);

        long offset = 0;
        for (int i = 0; i < layout.getNumAttributes(); i++) {
            VertexBufferLayout.VertexAttribute attrib = layout.getAttribute(i);
            ShaderPrimitiveUtil.ShaderPrimitiveType type = attrib.getType();
            if (type == ShaderPrimitiveUtil.ShaderPrimitiveType.UINT32  ||
                type == ShaderPrimitiveUtil.ShaderPrimitiveType.INT32   ||
                type == ShaderPrimitiveUtil.ShaderPrimitiveType.UINT16  ||
                type == ShaderPrimitiveUtil.ShaderPrimitiveType.INT16) {
                glVertexAttribIPointer(i, attrib.getCount(), ShaderPrimitiveUtil.mapShaderTypeToGLType(type), layout.getStride(), offset);
            } else if(type == ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32) {
                glVertexAttribPointer(i, attrib.getCount(), ShaderPrimitiveUtil.mapShaderTypeToGLType(type), false, layout.getStride(), offset);
            } else if(type == ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT64) {
                Logger.error("VertexBuffer.setVertexBufferToVertexArray", this, "OpenGL 3.3 does not support 64 bit floats in vertex arrays!");
                // TODO: consider upgrading to opengl 4.5 which was released in 2014
                //glVertexAttribLPointer(i, attrib.getCount(), ShaderPrimitiveUtil.mapShaderTypeToGLType(type), false, m_layout.getStride(), offset);
            }
            glEnableVertexAttribArray(i);

            offset += (long) ShaderPrimitiveUtil.getSizeOfType(type) * attrib.getCount();
        }
    }

    private void createVBO() {
        vboID = glGenBuffers();
    }

    @Override
    public void bind() {
        glBindVertexArray(vaoID);
    }

    @Override
    public void unBind() {
        glBindVertexArray(0);
    }

    @Override
    public long getMaxSize() {
        return maxSize;
    }

    @Override
    public void dispose() {
        glDeleteBuffers(vboID);
        glDeleteVertexArrays(vaoID);
        vboID = 0;
        vaoID = 0;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(vaoID != 0 || vboID != 0) {
            Logger.warn("VertexBuffer.finalize", this, "Garbage collection called but Uniform buffer not freed!");
        }
    }

    @Override
    public String toString() {
        return "VertexBuffer(VBOID=" + vboID + " VAOID=" + vaoID + ")";
    }

    private int vaoID = 0;
    private int vboID = 0;
    private VertexBufferLayout layout;
    private final long maxSize;
}

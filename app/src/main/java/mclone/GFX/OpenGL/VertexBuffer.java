package mclone.GFX.OpenGL;

import mclone.Logging.Logger;

import static org.lwjgl.system.MemoryUtil.*;
import java.nio.Buffer;

import static org.lwjgl.opengl.GL33C.*;

/**
 * Wrapper over the OpenGL Vertex Buffer which is a Hardware Buffer that can reside in CPU and GPU memory.
 * This is used to describe the vertices of meshes during drawing.
 */
public class VertexBuffer extends HardwareBuffer {
    /**
     * Create and allocate a VertexBuffer
     * @param data The data to be copied into the vertex buffer. Can be null, but requires size > 0. Can be freed after this function call
     * @param size The size in bytes of data. If data is null, size is used to allocate an empty buffer
     * @param hint An allocation hint for the OpenGL driver
     * @param layout The layout of the data that will be entered into the buffer
     */
    public VertexBuffer(Buffer data, long size, UsageHints hint, VertexBufferLayout layout) {
        this.layout = layout;
        maxSize = size;

        createVBO();
        setInitializeVBOData(data, size, hint);
        createVAO();
        setVertexBufferToVertexArray();
    }

    /**
     * Update the data in a vertex buffer
     * @param data The new data. Must not be null. Can be freed after this function call
     * @param size The size of the data. Must be greater than 0 and less than the maximumSize which is the size specified
     *             during the creation of the buffer.
     */
    public void setData(Buffer data, long size) {
        glBindVertexArray(vaoID);
        nglBufferSubData(GL_ARRAY_BUFFER, 0, size, memAddress(data));
    }

    /**
     * Bind the vertex buffer
     */
    @Override
    public void bind() {
        glBindVertexArray(vaoID);
    }

    /**
     * UnBind the vertex buffer. After this, no vertex buffer will be bound.
     */
    @Override
    public void unBind() {
        glBindVertexArray(0);
    }

    /**
     * Retrieve the maximum size of the buffer
     * @return The maximum size in bytes of the VertexBuffer. Specified during creation of the buffer.
     */
    @Override
    public long getMaxSize() {
        return maxSize;
    }

    public void setInstanceBuffer(InstanceBuffer instanceBuffer) {
        glBindBuffer(GL_ARRAY_BUFFER, instanceBuffer.id);
        glBindVertexArray(vaoID);

        if(currentInstanceBuffer != instanceBuffer) {
            long offset = 0;
            for (int i = 0; i < layout.getNumInstanceAttributes(); i++) {
                VertexBufferLayout.VertexAttribute attrib = layout.getInstanceAttribute(i);
                ShaderPrimitiveUtil.ShaderPrimitiveType type = attrib.getType();
                if (type == ShaderPrimitiveUtil.ShaderPrimitiveType.UINT32 ||
                    type == ShaderPrimitiveUtil.ShaderPrimitiveType.INT32 ||
                    type == ShaderPrimitiveUtil.ShaderPrimitiveType.UINT16 ||
                    type == ShaderPrimitiveUtil.ShaderPrimitiveType.INT16) {
                    instanceBuffer.bind();
                    glVertexAttribIPointer(i + layout.getNumVertexAttributes(), attrib.getCount(), ShaderPrimitiveUtil.mapShaderTypeToGLType(type), layout.getInstanceAttributeStride(), offset);
                    instanceBuffer.unBind();
                } else if (type == ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32) {
                    instanceBuffer.bind();
                    glVertexAttribPointer(i + layout.getNumVertexAttributes(), attrib.getCount(), ShaderPrimitiveUtil.mapShaderTypeToGLType(type), false, layout.getInstanceAttributeStride(), offset);
                    instanceBuffer.unBind();
                } else if (type == ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT64) {
                    Logger.error("VertexBuffer.setVertexBufferToVertexArray", this, "OpenGL 3.3 does not support 64 bit floats in vertex arrays!");
                    // TODO: consider upgrading to opengl 4.5 which was released in 2014
                    //glVertexAttribLPointer(i, attrib.getCount(), ShaderPrimitiveUtil.mapShaderTypeToGLType(type), false, m_layout.getStride(), offset);
                }
                glEnableVertexAttribArray(i + layout.getNumVertexAttributes());
                glVertexAttribDivisor(i + layout.getNumVertexAttributes(), 1);

                offset += (long) ShaderPrimitiveUtil.getSizeOfType(type) * attrib.getCount();
            }
            currentInstanceBuffer = instanceBuffer;
        }
    }

    /**
     * Free the memory and objects associated with the VertexBuffer. This should be called when you are finished with
     * it.
     */
    @Override
    public void dispose() {
        glDeleteBuffers(vboID);
        glDeleteVertexArrays(vaoID);
        vboID = 0;
        vaoID = 0;
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

    private void setVertexBufferToVertexArray() {
        glBindVertexArray(vaoID);
        glBindBuffer(GL_ARRAY_BUFFER, vboID);

        long offset = 0;
        for (int i = 0; i < layout.getNumVertexAttributes(); i++) {
            VertexBufferLayout.VertexAttribute attrib = layout.getVertexAttribute(i);
            ShaderPrimitiveUtil.ShaderPrimitiveType type = attrib.getType();
            if (type == ShaderPrimitiveUtil.ShaderPrimitiveType.UINT32  ||
                type == ShaderPrimitiveUtil.ShaderPrimitiveType.INT32   ||
                type == ShaderPrimitiveUtil.ShaderPrimitiveType.UINT16  ||
                type == ShaderPrimitiveUtil.ShaderPrimitiveType.INT16) {
                glVertexAttribIPointer(i, attrib.getCount(), ShaderPrimitiveUtil.mapShaderTypeToGLType(type), layout.getVertexAttributeStride(), offset);
            } else if(type == ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32) {
                glVertexAttribPointer(i, attrib.getCount(), ShaderPrimitiveUtil.mapShaderTypeToGLType(type), false, layout.getVertexAttributeStride(), offset);
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

    protected int vaoID = 0;
    private int vboID = 0;
    private VertexBufferLayout layout;
    private InstanceBuffer currentInstanceBuffer = null;
    private final long maxSize;
}

package mclone.GFX.OpenGL;

import mclone.Logging.Logger;

import java.util.ArrayList;

/**
 * Describes the layout of the contents of a VertexBuffer
 * @see VertexBuffer
 */
public class VertexBufferLayout {
    /**
     * Describes a singular attribute of a vertex in a VertexBuffer
     */
    public static class VertexAttribute {
        /**
         * Creates a VertexAttribute
         * @param type The type of attribute
         * @param count The number of primitives in the attribute
         */
        public VertexAttribute(ShaderPrimitiveUtil.ShaderPrimitiveType type, int count) {
            this.count = count;
            this.type = type;
        }

        /**
         * Get the size of the attribute
         * @return The size, in bytes, of the attribute
         */
        public int getSize() {
            return count * ShaderPrimitiveUtil.getSizeOfType(type);
        }

        /**
         * Get the type of the attribute
         * @return The type of the attribute
         */
        public ShaderPrimitiveUtil.ShaderPrimitiveType getType() { return type; }

        /**
         * Get the number of primitives in the attribute
         * @return The number of primitives in the attribute
         */
        public int getCount() { return count; }

        private ShaderPrimitiveUtil.ShaderPrimitiveType type;
        private int count;
    }

    /**
     * Creates a VertexBufferLayout
     * @param attributes A list of all the attributes (in order) of the layout
     */
    public VertexBufferLayout(ArrayList<VertexAttribute> vertexAttributes, ArrayList<VertexAttribute> instanceAttributes) {
        this.vertexAttributes = vertexAttributes;
        this.instanceAttributes = instanceAttributes;
    }

    /**
     * Get the attribute at the index specified
     * @param index The index, where 0 <= index < getNumAttributes()
     * @return the attribute at the specified index
     * @see VertexBufferLayout#getNumVertexAttributes()
     */
    public VertexAttribute getVertexAttribute(int index) {
        return vertexAttributes.get(index);
    }

    /**
     * Get the number of attributes in the layout
     * @return The number of attributes in the layout
     */
    public int getNumVertexAttributes() {
        return vertexAttributes.size();
    }

    /**
     * Get the total size of all the attributes. This means that if you had an array of these attributes, the return value
     * would be the byte distance between one attribute of one vertex to the same attribute of another
     * @return The size of one Vertex's worth of attributes
     */
    public int getVertexAttributeStride() {
        int stride = 0;
        for(VertexAttribute attribute : vertexAttributes) {
            int size = attribute.getSize();
            if(size <= 0) {
                break;
            }
            stride += size;
        }

        return stride;
    }

    /**
     * Get the instance attribute at the index specified
     * @param index The index, where 0 <= index < getNumAttributes()
     * @return the attribute at the specified index
     * @see VertexBufferLayout#getNumInstanceAttributes()
     */
    public VertexAttribute getInstanceAttribute(int index) {
        return instanceAttributes.get(index);
    }

    /**
     * Get the number of instance attributes in the layout
     * @return The number of attributes in the layout
     */
    public int getNumInstanceAttributes() {
        return instanceAttributes.size();
    }

    /**
     * Get the total size of all the attributes. This means that if you had an array of these attributes, the return value
     * would be the byte distance between one attribute of one vertex to the same attribute of another
     * @return The size of one Vertex's worth of attributes
     */
    public int getInstanceAttributeStride() {
        int stride = 0;
        for(VertexAttribute attribute : instanceAttributes) {
            int size = attribute.getSize();
            if(size <= 0) {
                break;
            }
            stride += size;
        }

        return stride;
    }

    ArrayList<VertexAttribute> vertexAttributes;
    ArrayList<VertexAttribute> instanceAttributes;
}

package mclone.GFX.OpenGL;

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
    public VertexBufferLayout(ArrayList<VertexAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * Get the attribute at the index specified
     * @param index The index, where 0 <= index < getNumAttributes()
     * @return the attribute at the specified index
     * @see VertexBufferLayout#getNumAttributes()
     */
    public VertexAttribute getAttribute(int index) {
        return attributes.get(index);
    }

    /**
     * Get the number of attributes in the layout
     * @return The number of attributes in the layout
     */
    public int getNumAttributes() {
        return attributes.size();
    }

    /**
     * Get the total size of all the attributes. This means that if you had an array of these attributes, the return value
     * would be the byte distance between one attribute of one vertex to the same attribute of another
     * @return The size of one Vertex's worth of attributes
     */
    public int getStride() {
        int stride = 0;
        for(VertexAttribute attribute : attributes) {
            int size = attribute.getSize();
            if(size <= 0) {
                break;
            }
            stride += size;
        }

        return stride;
    }

    ArrayList<VertexAttribute> attributes;
}

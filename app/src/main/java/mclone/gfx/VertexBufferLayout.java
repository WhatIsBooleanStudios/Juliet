package mclone.gfx;

import java.util.ArrayList;

public class VertexBufferLayout {
    public class VertexAttribute {
        public VertexAttribute(ShaderPrimitiveUtil.ShaderPrimitiveType type, int count) {
            m_count = count;
            m_type = type;
        }

        public int getSize() {
            return m_count * ShaderPrimitiveUtil.getSizeOfType(m_type);
        }

        public ShaderPrimitiveUtil.ShaderPrimitiveType getType() { return m_type; }
        public int getCount() { return m_count; }

        private ShaderPrimitiveUtil.ShaderPrimitiveType m_type;
        private int m_count;
    }

    public VertexBufferLayout(ArrayList<VertexAttribute> attributes) {
        m_attributes = attributes;
    }

    public VertexAttribute getAttribute(int index) {
        return m_attributes.get(index);
    }

    public int getNumAttributes() {
        return m_attributes.size();
    }

    public int getStride() {
        int stride = 0;
        for(VertexAttribute attribute : m_attributes) {
            int size = attribute.getSize();
            if(size <= 0) {
                System.out.println("Vertex attribute at offset " + stride + " has an invalid value");
                break;
            }
            stride += size;
        }

        return stride;
    }

    ArrayList<VertexAttribute> m_attributes;
}

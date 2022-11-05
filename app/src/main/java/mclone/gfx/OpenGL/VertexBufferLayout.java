package mclone.gfx.OpenGL;

import java.util.ArrayList;

public class VertexBufferLayout {
    public static class VertexAttribute {
        public VertexAttribute(ShaderPrimitiveUtil.ShaderPrimitiveType type, int count) {
            this.count = count;
            this.type = type;
        }

        public int getSize() {
            return count * ShaderPrimitiveUtil.getSizeOfType(type);
        }

        public ShaderPrimitiveUtil.ShaderPrimitiveType getType() { return type; }
        public int getCount() { return count; }

        private ShaderPrimitiveUtil.ShaderPrimitiveType type;
        private int count;
    }

    public VertexBufferLayout(ArrayList<VertexAttribute> attributes) {
        this.attributes = attributes;
    }

    public VertexAttribute getAttribute(int index) {
        return attributes.get(index);
    }

    public int getNumAttributes() {
        return attributes.size();
    }

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

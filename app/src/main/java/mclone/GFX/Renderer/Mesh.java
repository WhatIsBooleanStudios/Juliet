package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.*;
import mclone.Logging.Logger;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Objects;

class Mesh {
    protected Mesh(String name, AIMesh mesh, Material material) {
        this.name = name;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int numVertices = mesh.mNumVertices();
            vertexCount = numVertices;

            this.material = material;

            ArrayList<VertexBufferLayout.VertexAttribute> vertexAttributes = new ArrayList<>();
            vertexAttributes.add(new VertexBufferLayout.VertexAttribute(ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32, 3));
            vertexAttributes.add(new VertexBufferLayout.VertexAttribute(ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32, 3));
            vertexAttributes.add(new VertexBufferLayout.VertexAttribute(ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32, 2));
            ArrayList<VertexBufferLayout.VertexAttribute> instanceAttributes = new ArrayList<>();
            instanceAttributes.add(new VertexBufferLayout.VertexAttribute(ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32, 4)); // world position
            instanceAttributes.add(new VertexBufferLayout.VertexAttribute(ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32, 4)); // world position
            instanceAttributes.add(new VertexBufferLayout.VertexAttribute(ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32, 4)); // world position
            instanceAttributes.add(new VertexBufferLayout.VertexAttribute(ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32, 4)); // world position
            VertexBufferLayout layout = new VertexBufferLayout(vertexAttributes, instanceAttributes);

            FloatBuffer vertexBufferData = MemoryUtil.memAllocFloat(8 * numVertices);
            int index = 0;
            for (int i = 0; i < numVertices; i++) {
                AIVector3D vertex = mesh.mVertices().get(i);
                AIVector3D normal = mesh.mNormals().get(i);
                AIVector3D textureCoords = mesh.mTextureCoords(0).get(i);
                vertexBufferData.put(index++, vertex.x());
                vertexBufferData.put(index++, vertex.y());
                vertexBufferData.put(index++, vertex.z());
                vertexBufferData.put(index++, normal.x());
                vertexBufferData.put(index++, normal.y());
                vertexBufferData.put(index++, normal.z());
                vertexBufferData.put(index++, textureCoords.x());
                vertexBufferData.put(index++, 1 - textureCoords.y());
            }
            index = 0;


            vbo = new VertexBuffer(vertexBufferData, (long) layout.getVertexAttributeStride() * vertexCount, HardwareBuffer.UsageHints.USAGE_STATIC, layout);

            int numIndices = 0;
            for (int i = 0; i < mesh.mNumFaces(); i++) {
                numIndices += mesh.mFaces().get(i).mNumIndices();
                if(mesh.mFaces().get(i).mNumIndices() != 3) {
                    Logger.warn("Num indices: " + mesh.mFaces().get(i).mNumIndices());
                }
            }
            IntBuffer indexBufferData = MemoryUtil.memAllocInt(numIndices);
            int currentIndex = 0;
            for (int i = 0; i < mesh.mNumFaces(); i++) {
                AIFace face = mesh.mFaces().get(i);
                for (int j = 0; j < face.mNumIndices(); j++) {
                    indexBufferData.put(currentIndex, face.mIndices().get(j));
                    currentIndex++;
                }
            }

            ibo = new IndexBuffer(indexBufferData, (currentIndex + 1) * 4L, HardwareBuffer.UsageHints.USAGE_STATIC);
            indexCount = currentIndex + 1;

            MemoryUtil.memFree(vertexBufferData);
            MemoryUtil.memFree(indexBufferData);
        }
    }

    @Override
    public String toString() {
        return "Mesh(name=\"" + name + "\", " + "numVertices=" + vertexCount + ", numIndices=" + indexCount + "\")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mesh mesh = (Mesh) o;
        return name.equals(mesh.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    protected final VertexBuffer getVertexBuffer() {
        return vbo;
    }

    protected final IndexBuffer getIndexBuffer() {
        return ibo;
    }

    protected int getVertexCount() { return vertexCount; }
    protected int getIndexCount() { return indexCount; }
    protected final Material getMaterial() { return material; }

    protected void dispose() {
        if(!disposed) {
            vbo.dispose();
            ibo.dispose();
            disposed = true;
        } else {
            Logger.warn("Mesh.dispose", this, "Mesh is already disposed!");
        }
    }

    private boolean disposed = false;

    String name;
    private int vertexCount;
    private VertexBuffer vbo;

    private int indexCount;
    private IndexBuffer ibo;
    private Material  material;
}

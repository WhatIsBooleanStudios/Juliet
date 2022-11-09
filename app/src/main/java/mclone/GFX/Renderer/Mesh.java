package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.*;
import mclone.Logging.Logger;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector2D;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Mesh {
    public Mesh(AIMesh mesh, Material material) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            Logger.trace("begin mesh");
            int numVertices = mesh.mNumVertices();
            vertexCount = numVertices;

            this.material = material;

            ArrayList<VertexBufferLayout.VertexAttribute> vertexAttributes = new ArrayList<>();
            vertexAttributes.add(new VertexBufferLayout.VertexAttribute(ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32, 3));
            vertexAttributes.add(new VertexBufferLayout.VertexAttribute(ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32, 2));
            VertexBufferLayout layout = new VertexBufferLayout(vertexAttributes);

            Logger.trace("numVertices: " + numVertices);
            FloatBuffer vertexBufferData = MemoryUtil.memAllocFloat(5 * numVertices);
            int index = 0;
            for (int i = 0; i < numVertices; i++) {
                AIVector3D vertex = mesh.mVertices().get(i);
                AIVector3D textureCoords = mesh.mTextureCoords(0).get(i);
                vertexBufferData.put(index++, vertex.x());
                vertexBufferData.put(index++, vertex.y());
                vertexBufferData.put(index++, vertex.z());
                vertexBufferData.put(index++, textureCoords.x());
                vertexBufferData.put(index++, 1 - textureCoords.y());
            }
            index = 0;


            vbo = new VertexBuffer(vertexBufferData, (long) layout.getStride() * vertexCount, HardwareBuffer.UsageHints.USAGE_STATIC, layout);

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
            Logger.trace("end mesh");
        }
    }

    public void tempDraw(Shader shader) {
        material.getDiffuse().bind(0);
        GraphicsAPI.drawIndexed(shader, vbo, ibo, indexCount);
    }

    private int vertexCount;
    private VertexBuffer vbo;

    private int indexCount;
    private IndexBuffer ibo;
    private Material  material;
}

package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.*;
import mclone.Logging.Logger;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Mesh {
    public Mesh(AIMesh mesh) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int numVertices = mesh.mNumVertices();
            Logger.trace("numVertices: " + numVertices);
            FloatBuffer vertexBufferData = stack.mallocFloat(3 * numVertices);
            for (int i = 0; i < numVertices; i++) {
                AIVector3D vertex = mesh.mVertices().get(i);
                vertexBufferData.put(i * 3, vertex.x());
                vertexBufferData.put(i * 3 + 1, vertex.y());
                vertexBufferData.put(i * 3 + 2, vertex.z());
            }

            vertexCount = numVertices;

            ArrayList<VertexBufferLayout.VertexAttribute> vertexAttributes = new ArrayList<>();
            vertexAttributes.add(new VertexBufferLayout.VertexAttribute(ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32, 3));
            VertexBufferLayout layout = new VertexBufferLayout(vertexAttributes);

            vbo = new VertexBuffer(vertexBufferData, (long) layout.getStride() * vertexCount, HardwareBuffer.UsageHints.USAGE_STATIC, layout);

            int numIndices = 0;
            for (int i = 0; i < mesh.mNumFaces(); i++) {
                numIndices += mesh.mFaces().get(i).mNumIndices();
            }
            IntBuffer indexBufferData = stack.mallocInt(numIndices);
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
        }
    }

    public void tempDraw(Shader shader) {
        GraphicsAPI.drawIndexed(shader, vbo, ibo, indexCount);
    }

    private int vertexCount;
    private VertexBuffer vbo;

    private int indexCount;
    private IndexBuffer ibo;
}

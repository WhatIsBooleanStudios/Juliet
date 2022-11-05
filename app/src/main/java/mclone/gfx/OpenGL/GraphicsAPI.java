package mclone.gfx.OpenGL;

import static org.lwjgl.opengl.GL33C.*;

public class GraphicsAPI {
    public static void setClearColor(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }
    public static void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    public static void draw(Shader shader, VertexBuffer vbo, int vertexCount) {
        shader.bind();
        vbo.bind();
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
    }

    public static void drawIndexed(Shader shader, VertexBuffer vbo, IndexBuffer ibo, int indexCount) {
        shader.bind();
        vbo.bind();
        ibo.bind();
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }

    // Do not create an instance of this
    private GraphicsAPI() {}
}

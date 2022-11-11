package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.GraphicsAPI;
import mclone.GFX.OpenGL.Shader;
import mclone.GFX.OpenGL.ShaderBuilder;
import mclone.Platform.Filesystem;

import java.awt.*;

public class Renderer {
    public Renderer() {
        String vertexShaderSource = Filesystem.getFileTextFromResourceDirectory("/Shaders/StaticMesh/StaticMesh.vert");
        String fragmentShaderSource = Filesystem.getFileTextFromResourceDirectory("/Shaders/StaticMesh/StaticMesh.frag");
        ShaderBuilder shaderBuilder = new ShaderBuilder();
        shaderBuilder.setShaderSource(
            "/Shaders/StaticMesh/StaticMesh.vert", vertexShaderSource,
            "/Shaders/StaticMesh/StaticMesh.frag", fragmentShaderSource
        );
        shaderBuilder.addUniformBuffer("Camera");
        this.shader = shaderBuilder.createShader("StaticMeshShader");
    }

    public void begin(CameraController camera) {
        camera.bindToBindingPoint(0);
        shader.setUniformBuffer("Camera", 0);
    }

    public void drawModel(Model model) {
        for(Mesh mesh : model.getMeshes()) {
            mesh.getMaterial().getDiffuse().bind(0);
            GraphicsAPI.drawIndexed(shader, mesh.getVertexBuffer(), mesh.getIndexBuffer(), mesh.getIndexCount());
        }
    }

    public void end() {

    }

    private Shader shader;
}
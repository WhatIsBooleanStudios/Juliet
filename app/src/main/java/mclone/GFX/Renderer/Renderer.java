package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.GraphicsAPI;
import mclone.GFX.OpenGL.Shader;
import mclone.GFX.OpenGL.ShaderBuilder;
import mclone.GFX.OpenGL.ShaderPrimitiveUtil;
import mclone.Platform.Filesystem;
import org.joml.Vector3f;
import org.joml.Vector3fc;

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
        shaderBuilder.addUniform("uTranslation", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        shaderBuilder.addUniform("camPos", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        this.shader = shaderBuilder.createShader("StaticMeshShader");
    }

    public void begin(CameraController camera) {
        currentCamera = camera;
        camera.bindToBindingPoint(0);
        shader.setUniformBuffer("Camera", 0);
    }

    public void drawModel(Model model, Vector3fc position) {
        for(Mesh mesh : model.getMeshes()) {
            shader.bind();
            mesh.getMaterial().getDiffuse().bind(0);
            mesh.getMaterial().getNormal().bind(1);
            mesh.getMaterial().getMetallic().bind(2);
            mesh.getMaterial().getRoughness().bind(3);
            shader.setUniformVec3("uTranslation", position);
            shader.setUniformVec3("camPos", currentCamera.getCameraPosition());
            GraphicsAPI.drawIndexed(shader, mesh.getVertexBuffer(), mesh.getIndexBuffer(), mesh.getIndexCount());
        }
    }

    public void end() {

    }

    public void shutdown() {
        TextureCache.clear();
    }

    private CameraController currentCamera;
    private Shader shader;
}

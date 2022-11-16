package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.GraphicsAPI;
import mclone.GFX.OpenGL.Shader;
import mclone.GFX.OpenGL.ShaderBuilder;
import mclone.GFX.OpenGL.ShaderPrimitiveUtil;
import mclone.Logging.Logger;
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
        shaderBuilder.addUniform("lightPositions[0]", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        shaderBuilder.addUniform("lightPositions[1]", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        shaderBuilder.addUniform("lightPositions[2]", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        shaderBuilder.addUniform("lightPositions[3]", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        shaderBuilder.addUniform("diffuseColor", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        shaderBuilder.addUniform("metallic", ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32);
        shaderBuilder.addUniform("roughness", ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32);
        shaderBuilder.addUniform("ao", ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32);
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
            if(mesh.getMaterial().getDiffuseTexture() != null) {
                mesh.getMaterial().getDiffuseTexture().bind(0);
                shader.setUniformVec3("diffuseColor", new Vector3f(-1.0f));
            } else {
                shader.setUniformVec3("diffuseColor", mesh.getMaterial().getDiffuseColor());
            }
            shader.setUniformVec3("uTranslation", position);
            shader.setUniformVec3("camPos", currentCamera.getCameraPosition());
            shader.setUniformVec3("lightPositions[0]", new Vector3f(0.0f, -0.5f, -1.0f));
            shader.setUniformVec3("lightPositions[1]", new Vector3f(0.5f, 0.0f, -1.0f));
            shader.setUniformVec3("lightPositions[2]", new Vector3f(-0.5f, 0.0f, -1.0f));
            shader.setUniformVec3("lightPositions[3]", new Vector3f(0.0f, 0.5f, -1.0f));
            shader.setUniformFloat("metallic", mesh.getMaterial().getMetallic());
            shader.setUniformFloat("roughness", mesh.getMaterial().getRoughness());
            shader.setUniformFloat("ao", 1.0f);
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

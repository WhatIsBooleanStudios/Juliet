package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.*;
import mclone.Logging.Logger;
import mclone.Platform.Filesystem;
import mclone.Platform.Window;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Renderer {
    public Renderer(Window window) {
        this.window = window;
        this.temporaryInstanceBuffer = new InstanceBuffer(null, 3 * 4, HardwareBuffer.UsageHints.USAGE_DYNAMIC);
        guiManager = new GUIManager(window);

        String vertexShaderSource = Filesystem.getFileTextFromResourceDirectory("/Shaders/StaticMesh/StaticMesh.vert");
        String instancedVertexShaderSource = Filesystem.getFileTextFromResourceDirectory("/Shaders/StaticMesh/StaticMeshInstanced.vert");
        String fragmentShaderSource = Filesystem.getFileTextFromResourceDirectory("/Shaders/StaticMesh/StaticMesh.frag");
        ShaderBuilder shaderBuilder = new ShaderBuilder();
        shaderBuilder.setShaderSource(
            "/Shaders/StaticMesh/StaticMesh.vert", instancedVertexShaderSource,
            "/Shaders/StaticMesh/StaticMesh.frag", fragmentShaderSource
        );
        shaderBuilder.addUniformBuffer("Camera");
        shaderBuilder.addUniformBuffer("PointLights");
        shaderBuilder.addUniformBuffer("SpotLights");
        shaderBuilder.addUniform("camPos", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        shaderBuilder.addUniform("diffuseColor", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        shaderBuilder.addUniform("metallic", ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32);
        shaderBuilder.addUniform("roughness", ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32);
        shaderBuilder.addUniform("numPointLights", ShaderPrimitiveUtil.ShaderPrimitiveType.INT32);
        shaderBuilder.addUniform("numSpotLights", ShaderPrimitiveUtil.ShaderPrimitiveType.INT32);
        this.instancedShader = shaderBuilder.createShader("InstancedStaticMeshShader");
        shaderBuilder.setShaderSource(
            "/Shaders/StaticMesh/StaticMesh.vert", vertexShaderSource,
            "/Shaders/StaticMesh/StaticMesh.frag", fragmentShaderSource
        );
        shaderBuilder.addUniform("uTranslation", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        this.shader = shaderBuilder.createShader("StaticMeshShader");

        guiManager.init();
    }

    public void begin() {
        guiManager.newFrame();
    }

    public void beginModelRendering(@NotNull CameraController camera) {
        camera.bindToBindingPoint(0);
        shader.bind();
        shader.setUniformVec3("camPos", camera.getCameraPosition());
        instancedShader.bind();
        instancedShader.setUniformVec3("camPos", camera.getCameraPosition());

        lightManager.updateLights();
        lightManager.pointLightUBO.setToBindingPoint(1);
        lightManager.spotLightUBO.setToBindingPoint(2);

        shader.setUniformBuffer("Camera", 0);
        shader.setUniformBuffer("PointLights", 1);
        shader.setUniformBuffer("SpotLights", 2);
        shader.setUniformInt("numPointLights", lightManager.getNumPointLights());
        shader.setUniformInt("numSpotLights", lightManager.getNumSpotLights());

        instancedShader.setUniformBuffer("Camera", 0);
        instancedShader.setUniformBuffer("PointLights", 1);
        instancedShader.setUniformBuffer("SpotLights", 2);
        instancedShader.setUniformInt("numPointLights", lightManager.getNumPointLights());
        instancedShader.setUniformInt("numSpotLights", lightManager.getNumSpotLights());
    }

    public void drawModel(@NotNull Model model, @NotNull Vector3fc position) {
        for(Mesh mesh : model.getMeshes()) {
            shader.bind();
            if(mesh.getMaterial().getDiffuseTexture() != null) {
                mesh.getMaterial().getDiffuseTexture().bind(0);
                shader.setUniformVec3("diffuseColor", new Vector3f(-1.0f));
            } else {
                shader.setUniformVec3("diffuseColor", mesh.getMaterial().getDiffuseColor());
            }
            shader.setUniformVec3("uTranslation", position);
            shader.setUniformFloat("metallic", mesh.getMaterial().getMetallic());
            shader.setUniformFloat("roughness", mesh.getMaterial().getRoughness());
            GraphicsAPI.drawIndexed(shader, mesh.getVertexBuffer(), mesh.getIndexBuffer(), mesh.getIndexCount());
        }
    }

    public void drawModelInstanced(Model model, InstanceBuffer instanceBuffer, int numInstances) {
        for(Mesh mesh : model.getMeshes()) {
            instancedShader.bind();
            if(mesh.getMaterial().getDiffuseTexture() != null) {
                mesh.getMaterial().getDiffuseTexture().bind(0);
                instancedShader.setUniformVec3("diffuseColor", new Vector3f(-1.0f));
            } else {
                instancedShader.setUniformVec3("diffuseColor", mesh.getMaterial().getDiffuseColor());
            }
            instancedShader.setUniformFloat("metallic", mesh.getMaterial().getMetallic());
            instancedShader.setUniformFloat("roughness", mesh.getMaterial().getRoughness());
            GraphicsAPI.drawInstancedIndexed(instancedShader, mesh.getVertexBuffer(), mesh.getIndexBuffer(), instanceBuffer, mesh.getIndexCount(), numInstances);
        }
    }

    public void endModelRendering() {}

    public void end() {
        guiManager.endFrame();
    }

    public void shutdown() {
        shader.dispose();
        instancedShader.dispose();
        materialCache.clear();
        textureCache.clear();
        materialCache.clear();
        lightManager.shutdown();
        guiManager.shutdown();
    }

    public TextureCache getTextureCache() { return textureCache; }
    public MaterialCache getMaterialCache() { return materialCache; }
    public ModelLoader getModelLoader() { return modelLoader; }
    public LightManager getLightManager() { return lightManager; }
    public GUIManager getGUIManager() { return guiManager; }

    private final Shader shader;
    private final Shader instancedShader;

    final Window window;
    final ModelLoader modelLoader = new ModelLoader(this);
    final LightManager lightManager = new LightManager();
    final TextureCache textureCache = new TextureCache();
    final MaterialCache materialCache = new MaterialCache();
    final InstanceBuffer temporaryInstanceBuffer;

    final GUIManager guiManager;
}

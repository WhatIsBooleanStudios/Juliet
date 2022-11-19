package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.*;
import mclone.Platform.Filesystem;
import mclone.Platform.Window;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import static org.lwjgl.nuklear.Nuklear.*;

public class Renderer {
    public Renderer(Window window) {
        this.window = window;
        guiManager = new GUIManager(window.getNativeHandle());

        String vertexShaderSource = Filesystem.getFileTextFromResourceDirectory("/Shaders/StaticMesh/StaticMesh.vert");
        String fragmentShaderSource = Filesystem.getFileTextFromResourceDirectory("/Shaders/StaticMesh/StaticMesh.frag");
        ShaderBuilder shaderBuilder = new ShaderBuilder();
        shaderBuilder.setShaderSource(
            "/Shaders/StaticMesh/StaticMesh.vert", vertexShaderSource,
            "/Shaders/StaticMesh/StaticMesh.frag", fragmentShaderSource
        );
        shaderBuilder.addUniformBuffer("Camera");
        shaderBuilder.addUniformBuffer("PointLights");
        shaderBuilder.addUniformBuffer("SpotLights");
        shaderBuilder.addUniform("uTranslation", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        shaderBuilder.addUniform("camPos", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        shaderBuilder.addUniform("diffuseColor", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        shaderBuilder.addUniform("metallic", ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32);
        shaderBuilder.addUniform("roughness", ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32);
        shaderBuilder.addUniform("numPointLights", ShaderPrimitiveUtil.ShaderPrimitiveType.INT32);
        shaderBuilder.addUniform("numSpotLights", ShaderPrimitiveUtil.ShaderPrimitiveType.INT32);
        this.shader = shaderBuilder.createShader("StaticMeshShader");

        guiManager.init();
    }

    public void begin(@NotNull CameraController camera) {
        camera.bindToBindingPoint(0);
        shader.bind();
        shader.setUniformVec3("camPos", camera.getCameraPosition());

        lightManager.updateLights();
        lightManager.pointLightUBO.setToBindingPoint(1);
        lightManager.spotLightUBO.setToBindingPoint(2);
    }

    public void beginModelRendering() {
        shader.setUniformBuffer("Camera", 0);
        shader.setUniformBuffer("PointLights", 1);
        shader.setUniformBuffer("SpotLights", 2);
        shader.setUniformInt("numPointLights", lightManager.getNumPointLights());
        shader.setUniformInt("numSpotLights", lightManager.getNumSpotLights());
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

    public void endModelRendering() {}

    public void end() {
        guiManager.render(NK_ANTI_ALIASING_ON, GUIManager.MAX_VERTEX_BUFFER, GUIManager.MAX_ELEMENT_BUFFER);
    }

    public void shutdown() {
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

    final Window window;
    final ModelLoader modelLoader = new ModelLoader(this);
    final LightManager lightManager = new LightManager();
    final TextureCache textureCache = new TextureCache();
    final MaterialCache materialCache = new MaterialCache();

    final GUIManager guiManager;
}

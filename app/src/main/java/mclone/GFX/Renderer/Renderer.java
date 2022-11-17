package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.*;
import mclone.Platform.Filesystem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.FloatBuffer;

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
        shaderBuilder.addUniformBuffer("PointLights");
        shaderBuilder.addUniform("uTranslation", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        shaderBuilder.addUniform("camPos", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        shaderBuilder.addUniform("diffuseColor", ShaderPrimitiveUtil.ShaderPrimitiveType.VEC3);
        shaderBuilder.addUniform("metallic", ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32);
        shaderBuilder.addUniform("roughness", ShaderPrimitiveUtil.ShaderPrimitiveType.FLOAT32);
        shaderBuilder.addUniform("numPointLights", ShaderPrimitiveUtil.ShaderPrimitiveType.INT32);
        this.shader = shaderBuilder.createShader("StaticMeshShader");

        pointLightUBO = new UniformBuffer(null, 4L * PointLight.dataBufferNumFloats() * MAX_POINT_LIGHTS, HardwareBuffer.UsageHints.USAGE_DYNAMIC);
    }

    public void begin(@NotNull CameraController camera) {
        camera.bindToBindingPoint(0);
        shader.bind();
        shader.setUniformVec3("camPos", camera.getCameraPosition());

        pointLightUBO.setToBindingPoint(1);
    }

    public void beginModelRendering() {
        shader.setUniformBuffer("Camera", 0);
        shader.setUniformBuffer("PointLights", 1);
        shader.setUniformInt("numPointLights", currentPointLightIndex);
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

    public void endModelRendering() {

    }

    public void beginLightConfiguration() {

    }

    public void attachPointLight(@NotNull PointLight light) {
        System.arraycopy(light.dataBuffer, 0, pointLightsData, PointLight.dataBufferNumFloats() * currentPointLightIndex, PointLight.dataBufferNumFloats());
        currentPointLightIndex++;
    }

    public void endLightConfiguration() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat((currentPointLightIndex) * PointLight.dataBufferNumFloats());
            buffer.put(pointLightsData, 0, (currentPointLightIndex) * PointLight.dataBufferNumFloats());
            buffer.flip();
            pointLightUBO.setData(buffer, (long)(currentPointLightIndex) * PointLight.dataBufferNumFloats() * 4);
        }
    }

    public void end() {
        currentPointLightIndex = 0;
    }

    public void shutdown() {
        materialCache.clear();
        textureCache.clear();
        pointLightUBO.dispose();
        materialCache.clear();
    }

    public TextureCache getTextureCache() { return textureCache; }
    public MaterialCache getMaterialCache() { return materialCache; }
    public ModelLoader getModelLoader() { return modelLoader; }

    private final Shader shader;

    ModelLoader modelLoader = new ModelLoader(this);
    TextureCache textureCache = new TextureCache();
    MaterialCache materialCache = new MaterialCache();
    public static final int MAX_POINT_LIGHTS = 128;
    private int currentPointLightIndex = 0;
    private final float[] pointLightsData = new float[MAX_POINT_LIGHTS * PointLight.dataBufferNumFloats()];
    private final UniformBuffer pointLightUBO;
}

package mclone.gfx.OpenGL;

import java.util.ArrayList;
import static mclone.gfx.OpenGL.Shader.ShaderBindingDescription;

public class ShaderBuilder {
    public ShaderBuilder() {}

    public void setShaderSource(String vertexShaderName, String vertexShader, String fragmentShaderName, String fragmentShader) {
        shaderSource = new Shader.ShaderSource(vertexShaderName, vertexShader, fragmentShaderName,fragmentShader);
    }

    public void addUniform(String name, ShaderPrimitiveUtil.ShaderPrimitiveType type) {
        uniformDescriptions.add(new Shader.ShaderBindingDescription.UniformDescription(name, type));
    }

    public void addUniformBuffer(String name) {
        uniformBufferDescriptions.add(new ShaderBindingDescription.UniformBufferDescription(name));
    }

    public Shader createShader(String name) {
        ShaderBindingDescription description = new ShaderBindingDescription(uniformDescriptions, uniformBufferDescriptions);
        return new Shader(name, shaderSource, description);
    }

    private ArrayList<Shader.ShaderBindingDescription.UniformDescription> uniformDescriptions = 
        new ArrayList<>();

    private ArrayList<Shader.ShaderBindingDescription.UniformBufferDescription> uniformBufferDescriptions =
        new ArrayList<>();
    private Shader.ShaderSource shaderSource;
}

package mclone.gfx.OpenGL;

import java.util.ArrayList;
import static mclone.gfx.OpenGL.Shader.ShaderBindingDescription;

public class ShaderBuilder {
    public ShaderBuilder() {}

    public void setShaderSource(String vertexShader, String fragmentShader) {
        shaderSource = new Shader.ShaderSource(vertexShader, fragmentShader);
    }

    public void addUniform(String name, ShaderPrimitiveUtil.ShaderPrimitiveType type) {
        uniformDescriptions.add(new Shader.ShaderBindingDescription.UniformDescription(name, type));
    }

    public void addUniformBuffer(String name) {
        uniformBufferDescriptions.add(new ShaderBindingDescription.UniformBufferDescription(name));
    }

    public Shader get() {
        ShaderBindingDescription description = new ShaderBindingDescription(uniformDescriptions, uniformBufferDescriptions);
        return new Shader(shaderSource, description);
    }

    private ArrayList<Shader.ShaderBindingDescription.UniformDescription> uniformDescriptions = 
        new ArrayList<>();

    private ArrayList<Shader.ShaderBindingDescription.UniformBufferDescription> uniformBufferDescriptions =
        new ArrayList<>();
    private Shader.ShaderSource shaderSource;
}

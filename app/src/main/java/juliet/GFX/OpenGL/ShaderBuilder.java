package juliet.GFX.OpenGL;

import java.util.ArrayList;
import static juliet.GFX.OpenGL.Shader.ShaderBindingDescription;

/**
 * Provides an easier way to construct a shader
 */
public class ShaderBuilder {

    /**
     * Create a new shader builder
     */
    public ShaderBuilder() {}

    /**
     * Set the source code of the shader
     * @param vertexShaderName The name of the vertex shader
     * @param vertexShader The vertex shader source code
     * @param fragmentShaderName The name of the fragment shader
     * @param fragmentShader The fragment shader source code
     */
    public void setShaderSource(String vertexShaderName, String vertexShader, String fragmentShaderName, String fragmentShader) {
        shaderSource = new Shader.ShaderSource(vertexShaderName, vertexShader, fragmentShaderName,fragmentShader);
    }

    /**
     * Adds a uniform to the uniforms list for shader creation
     * @param name The name of the uniform
     * @param type The type of the uniform
     */
    public void addUniform(String name, ShaderPrimitiveUtil.ShaderPrimitiveType type) {
        uniformDescriptions.add(new Shader.ShaderBindingDescription.UniformDescription(name, type));
    }

    /**
     * Add a uniform buffer to the uniform buffer list for shader creation
     * @param name The name of the uniform buffer
     */
    public void addUniformBuffer(String name) {
        uniformBufferDescriptions.add(new ShaderBindingDescription.UniformBufferDescription(name));
    }

    /**
     * Create the shader with previously the previously set criteria
     * @param name The name of the shader to be created
     * @return A shader with the specified criteria
     */
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

package juliet.GFX.OpenGL;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import juliet.Logging.Logger;
import juliet.GFX.OpenGL.Shader.ShaderBindingDescription.UniformDescription;
import juliet.GFX.OpenGL.Shader.ShaderBindingDescription.UniformBufferDescription;
import juliet.GFX.OpenGL.ShaderPrimitiveUtil.ShaderPrimitiveType;

import java.util.ArrayList;
import java.util.HashMap;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33C.*;

/**
 * A program the runs on the GPU. This is used for translating worlds-space coordinates to screen-space and setting the
 * color of pixels on the screen
 */
public class Shader {

    /**
     * Describes the source code of the shader
     */
    public static class ShaderSource {
        /**
         * Create a ShaderSource object
         * @param vertexShaderName The name of the vertex shader
         * @param vs The vertex shader source code
         * @param fragmentShaderName The name of the fragment shader
         * @param fs The fragment shader source code
         */
        public ShaderSource(String vertexShaderName, String vs, String fragmentShaderName, String fs) {
            this.vs = vs;
            this.fs = fs;
            this.vsName = vertexShaderName;
            this.fsName = fragmentShaderName;
        }

        /**
         * @return The vertex shader source code
         */
        public String getVS() { return vs; }

        /**
         * @return The name of the vertex shader
         */
        public String getVsName() { return vsName; }

        /**
         * @return The fragment shader source code
         */
        public String getFS() { return fs; }

        /**
         * @return The name of the fragment shader.
         */
        public String getFsName() { return fsName; }

        private final String vs;
        private final String vsName;
        private final String fs;
        private final String fsName;
    }

    /**
     * Describes the bindings (modifiable parameters to shaders) on a shader
     */
    public static class ShaderBindingDescription {
        /**
         * Describes a uniform(shader global parameter) that can be set on the shader
         */
        public static class UniformDescription {
            /**
             * Create a UniformDescription
             * @param name The name of the uniform
             * @param type The type of the uniform
             */
            public UniformDescription(String name, ShaderPrimitiveType type) {
                this.name = name;
                this.type = type;
            }

            /**
             * @return The name of the uniform description
             */
            String getName() { return name; }

            /**
             * @return The type of the uniform description
             */
            ShaderPrimitiveType getType() { return type; }

            private final String name;
            private final ShaderPrimitiveType type;

            @Override
            public boolean equals(Object d) {
                if(d.getClass() != this.getClass()) return false;
                UniformDescription description = (UniformDescription)d;
                return name.equals(description.name) && type == description.type;
            }

            @Override
            public int hashCode() {
                return (name + "_" + type.toString()).hashCode();
            }
        }

        /**
         * Describes a uniform buffer that can be bound to the shader
         * @see UniformBuffer
         */
        public static class UniformBufferDescription {
            /**
             * Create a UniformBufferDescription
             * @param name The name of the UniformBuffer
             */
            public UniformBufferDescription(String name) {
                this.name = name;
            }

            @Override
            public int hashCode() {
                return name.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                if(obj.getClass() != this.getClass()) return false;
                return name.equals(((UniformBufferDescription)obj).name);
            }

            String name;
        }

        /**
         * Create a shader binding description
         * @param uniforms List of uniforms
         * @param uniformBuffers List of uniform buffer bindings
         */
        public ShaderBindingDescription(ArrayList<UniformDescription> uniforms, ArrayList<UniformBufferDescription> uniformBuffers) {
            this.uniforms = uniforms;
            this.uniformBuffers = uniformBuffers;
        }

        /**
         * @return The uniforms in this shader binding description
         */
        public final ArrayList<UniformDescription> getUniforms() { return uniforms; }

        /**
         * @return The uniform buffer descriptions of this shader binding
         */
        public final ArrayList<UniformBufferDescription> getUniformBuffers() { return uniformBuffers; }

        private ArrayList<UniformDescription> uniforms;

        private ArrayList<UniformBufferDescription> uniformBuffers;
    }


    /**
     * Create a Shader
     * @param shaderName The name of the shader
     * @param src The source of the shader
     * @param bindingDescription Description of the bindings used by the shader
     */
    public Shader(String shaderName, ShaderSource src, ShaderBindingDescription bindingDescription) {
        id = glCreateProgram();
        this.name = shaderName;

        int vs = compileShader(src.getVsName(), GL_VERTEX_SHADER, src.getVS());
        int fs = compileShader(src.getFsName(), GL_FRAGMENT_SHADER, src.getFS());

        glAttachShader(id, vs);
        glAttachShader(id, fs);
        linkProgram(id);

        glDeleteShader(vs);
        glDeleteShader(fs);

        for(UniformDescription description : bindingDescription.uniforms) {
            int location = glGetUniformLocation(id, description.name);
            if(location < 0) {
                Logger.error("Shader.new", this, "Cannot find uniform \"" + description.name + "\"");
            }
            uniformLocationMap.put(description, location);
        }

        for(UniformBufferDescription description : bindingDescription.uniformBuffers) {
            int blockIndex = glGetUniformBlockIndex(id, description.name);
            if(blockIndex < 0) {
                Logger.error("Shader.new", this, "Failed to find uniform buffer block index for ubo \"" + description.name + "\"");
                continue;
            }
            uniformBufferBlockIndexMap.put(description, blockIndex);
        }
    }

    /**
     * Bind the shader
     */
    public void bind() {
        glUseProgram(id);
    }

    /**
     * Unbind this shader and set OpenGL to a state where no shaders are bound.
     */
    public void unBind() {
        glUseProgram(0);
    }

    /**
     * Free memory and objects associated with this shader
     */
    public void dispose() {
        glDeleteProgram(id);
    }

    /**
     * Connect the shader to a uniform buffer
     * @param name The name of the uniform buffer
     * @param bindingPoint The binding point that the UniformBuffer is bound to
     */
    public void setUniformBuffer(String name, int bindingPoint) {
        bind();
        int blockIndex = uniformBufferBlockIndexMap.getOrDefault(new UniformBufferDescription(name), -1);
        if(blockIndex < 0) {
            Logger.error("Shader.setUniformBuffer", this, "Failed to find uniform buffer block index for ubo \"" + name + "\"");
            return;
        }
        glUniformBlockBinding(id, blockIndex, bindingPoint);
    }

    /**
     * Set a float uniform in the shader
     * @param name The name of the uniform
     * @param value The value of the uniform
     */
    public void setUniformFloat(String name, float value) {
        bind();
        UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.FLOAT32);
        if(!uniformLocationMap.containsKey(description)) {
            Logger.error("Shader.setUniformFloat", this, "Shader does not contain the float uniform \"" + name + "\"");
            return;
        }

        Integer location = uniformLocationMap.get(description);
        glUniform1f(location, value);
    }

    /**
     * Set an integer uniform in the shader
     * @param name The name of the uniform
     * @param value The value of the uniform
     */
    public void setUniformInt(String name, int value) {
        bind();
        UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.INT32);
        if(!uniformLocationMap.containsKey(description)) {
            Logger.error("Shader.setUniformInt", this, "Shader does not contain the integer uniform \"" + name + "\"");
            return;
        }

        Integer location = uniformLocationMap.get(description);
        glUniform1i(location, value);
    }

    /**
     * Set an unsigned integer uniform in the shader
     * @param name The name of the uniform
     * @param value The value of the uniform
     */
    public void setUniformUint(String name, int value) {
        bind();
        UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.UINT32);
        if(!uniformLocationMap.containsKey(description)) {
            Logger.error("setUniformUint", this, "Shader does not contain the unsigned integer uniform \"" + name + "\"");
            return;
        }

        Integer location = uniformLocationMap.get(description);
        glUniform1ui(location, value);
    }

    /**
     * Set an 2-float vector uniform in the shader
     * @param name The name of the uniform
     * @param value The value of the uniform
     */
    public void setUniformVec2(String name, Vector2fc value) {
        bind();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.VEC2);
            if(!uniformLocationMap.containsKey(description)) {
                Logger.error("Shader.setUniformVec2", this, "Shader does not contain the vector2f uniform \"" + name + "\"");
                return;
            }

            Integer location = uniformLocationMap.get(description);
            FloatBuffer buffer = stack.mallocFloat(2);
            glUniform2fv(location, value.get(buffer));
        }
    }


    /**
     * Set an 3-float vector uniform in the shader
     * @param name The name of the uniform
     * @param value The value of the uniform
     */
    public void setUniformVec3(String name, Vector3fc value) {
        bind();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.VEC3);
            if(!uniformLocationMap.containsKey(description)) {
                Logger.error("Shader.setUniformVec3" , this,"Shader does not contain the vector3f uniform \"" + name + "\"");
                return;
            }

            Integer location = uniformLocationMap.get(description);
            FloatBuffer buffer = stack.mallocFloat(3);
            glUniform3fv(location, value.get(buffer));
        }
    }

    /**
     * Set an 4-float vector uniform in the shader
     * @param name The name of the uniform
     * @param value The value of the uniform
     */
    public void setUniformVec4(String name, Vector4fc value) {
        bind();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.VEC4);
            if(!uniformLocationMap.containsKey(description)) {
                Logger.error("Shader.setUniformVec4", this, "Shader does not contain the vector4f uniform \"" + name + "\"");
                return;
            }

            Integer location = uniformLocationMap.get(description);
            FloatBuffer buffer = stack.mallocFloat(4);
            glUniform4fv(location, value.get(buffer));
        }
    }

    /**
     * Set a 2x2-float matrix uniform in the shader
     * @param name The name of the uniform
     * @param value The value of the uniform
     */
    public void setUniformMat2(String name, Matrix2fc value) {
        bind();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.MAT2);
            if(!uniformLocationMap.containsKey(description)) {
                Logger.error("Shader.setUniformMat2", this, "Shader does not contain the matrix2f uniform \"" + name + "\"");
                return;
            }

            Integer location = uniformLocationMap.get(description);
            FloatBuffer buffer = stack.mallocFloat(4);
            glUniformMatrix2fv(location, false, value.get(buffer));
        }
    }

    /**
     * Set a 3x3-float matrix uniform in the shader
     * @param name The name of the uniform
     * @param value The value of the uniform
     */
    public void setUniformMat3(String name, Matrix3fc value) {
        bind();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.MAT3);
            if(!uniformLocationMap.containsKey(description)) {
                Logger.error("Shader.setUniformMat3", this, "Shader does not contain the matrix3f uniform \"" + name + "\"");
                return;
            }

            Integer location = uniformLocationMap.get(description);
            FloatBuffer buffer = stack.mallocFloat(9);
            glUniformMatrix3fv(location, false, value.get(buffer));
        }
    }

    /**
     * Set a 4x4-float matrix uniform in the shader
     * @param name The name of the uniform
     * @param value The value of the uniform
     */
    public void setUniformMat4(String name, Matrix4fc value) {
        bind();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.MAT4);
            if(!uniformLocationMap.containsKey(description)) {
                Logger.error("Shader.setUniformMat4", this, "Shader does not contain the matrix4f uniform \"" + name + "\"");
                return;
            }

            Integer location = uniformLocationMap.get(description);
            FloatBuffer buffer = stack.mallocFloat(16);
            glUniformMatrix4fv(location, false, value.get(buffer));
        }
    }

    @Override
    public String toString() {
        return "juliet.OpenGL.Shader(\"" + name + "\")";
    }

    private int compileShader(String name, int type, String src) {
        int shader = glCreateShader(type);
        glShaderSource(shader, src);
        glCompileShader(shader);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer success = stack.mallocInt(1);
            glGetShaderiv(shader, GL_COMPILE_STATUS, success);
            if(success.get() <= 0) {
                Logger.error("Shader.compileShader", this, "SHADER COMPILE ERROR@\"" + name + "\":\n" + glGetShaderInfoLog(shader));
                return -1;
            } else {
                return shader;
            }
        }
    }

    private boolean linkProgram(int program) {
        glLinkProgram(program);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer success = stack.mallocInt(1);
            glGetProgramiv(program, GL_LINK_STATUS, success);
            if(success.get() <= 0) {
                Logger.error("Shader.linkProgram", this, "SHADER LINK ERROR:\n" + glGetProgramInfoLog(program));
                return false;
            } else {
                return true;
            }
        }
    }

    private int id;
    private String name;

    private final HashMap<UniformDescription, Integer> uniformLocationMap = new HashMap<>();
    private final HashMap<UniformBufferDescription, Integer> uniformBufferBlockIndexMap = new HashMap<>();
}


package mclone.gfx.OpenGL;

import org.joml.Matrix2f;
import org.joml.Matrix3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import mclone.Logging.Logger;
import mclone.gfx.OpenGL.Shader.ShaderBindingDescription.UniformDescription;
import mclone.gfx.OpenGL.Shader.ShaderBindingDescription.UniformBufferDescription;
import mclone.gfx.OpenGL.ShaderPrimitiveUtil.ShaderPrimitiveType;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33C.*;

public class Shader {

    public static class ShaderSource {
        public ShaderSource(String vs, String fs) {
            m_vs = vs;
            m_fs = fs;
        }

        public String getVS() { return m_vs; }
        public String getFS() { return m_fs; }

        private String m_vs;
        private String m_fs;
    }

    public static class ShaderBindingDescription {
        public static class UniformDescription {
            public UniformDescription(String name, ShaderPrimitiveType type) {
                this.name = name;
                this.type = type;
            }

            String getName() { return name; }
            ShaderPrimitiveType getType() { return type; }

            String name;
            ShaderPrimitiveType type;

            @Override
            public boolean equals(Object d) {
                if(d.getClass() != this.getClass()) return false;
                UniformDescription description = (UniformDescription)d;
                return name == description.name && type == description.type;
            }

            @Override
            public int hashCode() {
                return (name + "_" + type.toString()).hashCode();
            }
        }

        public static class UniformBufferDescription {
            public UniformBufferDescription(String name) {
                this.name = name;
            }

            @Override
            public int hashCode() {
                return name.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return name.equals(((UniformBufferDescription)obj).name);
            }

            String name;
        }

        public ShaderBindingDescription(ArrayList<UniformDescription> uniforms, ArrayList<UniformBufferDescription> uniformBuffers) {
            this.uniforms = uniforms;
            this.uniformBuffers = uniformBuffers;
        }

        ArrayList<UniformDescription> getUniforms() { return uniforms; }
        ArrayList<UniformBufferDescription> getUniformBuffers() { return uniformBuffers; }

        ArrayList<UniformDescription> uniforms;

        ArrayList<UniformBufferDescription> uniformBuffers;
    }


    public Shader(ShaderSource src, ShaderBindingDescription bindingDescription) {
        m_ID = glCreateProgram();
        int vs = compileShader(GL_VERTEX_SHADER, src.getVS());
        int fs = compileShader(GL_FRAGMENT_SHADER, src.getFS());

        glAttachShader(m_ID, vs);
        glAttachShader(m_ID, fs);
        linkProgram(m_ID);

        glDeleteShader(vs);
        glDeleteShader(fs);

        for(UniformDescription description : bindingDescription.uniforms) {
            int location = glGetUniformLocation(m_ID, description.name);
            if(location < 0) {
                System.out.println("failed to find uniform!");
            }
            uniformLocationMap.put(description, location);
        }

        for(UniformBufferDescription description : bindingDescription.uniformBuffers) {
            int blockIndex = glGetUniformBlockIndex(m_ID, description.name);
            if(blockIndex < 0) {
                System.out.println("failed to find uniform buffer block index for ubo \"" + description.name + "\"");
                continue;
            }
            uniformBufferBlockIndexMap.put(description, blockIndex);
        }
    }

    public void bind() {
        glUseProgram(m_ID);
    }

    public void unBind() {
        glUseProgram(0);
    }

    public void dispose() {
        glDeleteProgram(m_ID);
    }

    public void setUniformBuffer(String name, int bindingPoint) {
        bind();
        int blockIndex = uniformBufferBlockIndexMap.getOrDefault(new UniformBufferDescription(name), -1);
        if(blockIndex < 0) {
            System.out.println("failed to find block index for uniform buffer \"" + name + "\"");
            return;
        }
        glUniformBlockBinding(m_ID, blockIndex, bindingPoint);
    }

    public void setUniformFloat(String name, float value) {
        bind();
        UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.FLOAT32);
        if(!uniformLocationMap.containsKey(description)) {
            System.out.println("Shader does not contain the float uniform \"" + name + "\"");
            return;
        }

        Integer location = uniformLocationMap.get(description);
        glUniform1f(location, value);
    }

    public void setUniformInt(String name, int value) {
        bind();
        UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.INT32);
        if(!uniformLocationMap.containsKey(description)) {
            System.out.println("Shader does not contain the integer uniform \"" + name + "\"");
            return;
        }

        Integer location = uniformLocationMap.get(description);
        glUniform1i(location, value);
    }

    public void setUniformUint(String name, int value) {
        bind();
        UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.UINT32);
        if(!uniformLocationMap.containsKey(description)) {
            System.out.println("Shader does not contain the unsigned integer uniform \"" + name + "\"");
            return;
        }

        Integer location = uniformLocationMap.get(description);
        glUniform1ui(location, value);
    }
    
    public void setUniformVec2(String name, Vector2f value) {
        bind();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.VEC2);
            if(!uniformLocationMap.containsKey(description)) {
                System.out.println("Shader does not contain the vector2f uniform \"" + name + "\"");
                return;
            }

            Integer location = uniformLocationMap.get(description);
            FloatBuffer buffer = stack.mallocFloat(2);
            glUniform2fv(location, value.get(buffer));
        }
    }

    
    public void setUniformVec3(String name, Vector3f value) {
        bind();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.VEC3);
            if(!uniformLocationMap.containsKey(description)) {
                System.out.println("Shader does not contain the vector3f uniform \"" + name + "\"");
                return;
            }

            Integer location = uniformLocationMap.get(description);
            FloatBuffer buffer = stack.mallocFloat(3);
            glUniform3fv(location, value.get(buffer));
        }
    }

    public void setUniformVec4(String name, Vector4f value) {
        bind();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.VEC4);
            if(!uniformLocationMap.containsKey(description)) {
                System.out.println("Shader does not contain the vector4f uniform \"" + name + "\"");
                return;
            }

            Integer location = uniformLocationMap.get(description);
            FloatBuffer buffer = stack.mallocFloat(4);
            glUniform4fv(location, value.get(buffer));
        }
    }

    public void setUniformMat2(String name, Matrix2f value) {
        bind();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.MAT2);
            if(!uniformLocationMap.containsKey(description)) {
                System.out.println("Shader does not contain the matrix2f uniform \"" + name + "\"");
                return;
            }

            Integer location = uniformLocationMap.get(description);
            FloatBuffer buffer = stack.mallocFloat(4);
            glUniformMatrix2fv(location, false, value.get(buffer));
        }
    }
 
    public void setUniformMat3(String name, Matrix3f value) {
        bind();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.MAT3);
            if(!uniformLocationMap.containsKey(description)) {
                System.out.println("Shader does not contain the matrix3f uniform \"" + name + "\"");
                return;
            }

            Integer location = uniformLocationMap.get(description);
            FloatBuffer buffer = stack.mallocFloat(9);
            glUniformMatrix3fv(location, false, value.get(buffer));
        }
    } 
    
    public void setUniformMat4(String name, Matrix4f value) {
        bind();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            UniformDescription description = new UniformDescription(name, ShaderPrimitiveType.MAT4);
            if(!uniformLocationMap.containsKey(description)) {
                System.out.println("Shader does not contain the matrix4f uniform \"" + name + "\"");
                return;
            }

            Integer location = uniformLocationMap.get(description);
            FloatBuffer buffer = stack.mallocFloat(16);
            glUniformMatrix4fv(location, false, value.get(buffer));
        }
    } 

    private int compileShader(int type, String src) {
        int shader = glCreateShader(type);
        glShaderSource(shader, src);
        glCompileShader(shader);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer success = stack.mallocInt(1);
            glGetShaderiv(shader, GL_COMPILE_STATUS, success);
            if(success.get() <= 0) {
                Logger.get().error("SHADER COMPILE ERROR: " + glGetShaderInfoLog(shader));
                return -1;
            } else {
                System.out.println("Shader compilation success");
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
                Logger.get().error("SHADER LINK ERROR: " + glGetProgramInfoLog(program));
                return false;
            } else {
                System.out.println("Shader linkage success");
                return true;
            }
        }
    }

    int m_ID;

    HashMap<UniformDescription, Integer> uniformLocationMap = new HashMap<>();
    HashMap<UniformBufferDescription, Integer> uniformBufferBlockIndexMap = new HashMap<>();
}


package mclone.gfx.OpenGL;

import org.lwjgl.system.MemoryStack;

import mclone.Logging.Logger;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33C.*;

public class Shader {
    public class ShaderSource {
        public ShaderSource(String vs, String fs) {
            m_vs = vs;
            m_fs = fs;
        }

        public String getVS() { return m_vs; }
        public String getFS() { return m_fs; }

        private String m_vs;
        private String m_fs;
    }


    public Shader(ShaderSource src) {
        m_ID = glCreateProgram();
        int vs = compileShader(GL_VERTEX_SHADER, src.getVS());
        int fs = compileShader(GL_FRAGMENT_SHADER, src.getFS());

        glAttachShader(m_ID, vs);
        glAttachShader(m_ID, fs);
        linkProgram(m_ID);

        glDeleteShader(vs);
        glDeleteShader(fs);
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
                return true;
            }
        }
    }

    int m_ID;
}

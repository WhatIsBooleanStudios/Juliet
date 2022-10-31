package mclone.gfx.OpenGL;

import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33C.*;

public class Shader {
    public class ShaderSource {
        public ShaderSource(String vs, String fs) {
            m_vs = vs;
            m_fs = fs;
        }

        String getVs() { return m_vs; }
        String getFs() { return m_fs; }

        String m_vs;
        String m_fs;
    }

    public int compileShader(int type, String src) {
        int shader = glCreateShader(type);
        glShaderSource(shader, src);
        glCompileShader(shader);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer success = stack.mallocInt(1);
            glGetShaderiv(shader, GL_COMPILE_STATUS, success);
            if(success.get() <= 0) {
                glGetShader
            }
        }
    }

    public Shader(ShaderSource src) {
        m_ID = glCreateProgram();
    }

    int m_ID;
}

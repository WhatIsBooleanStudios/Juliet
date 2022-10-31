package mclone;

import mclone.platform.Window;

import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import mclone.Logging.Logger;
import mclone.gfx.OpenGL.HardwareBuffer;
import mclone.gfx.OpenGL.Shader;
import mclone.gfx.OpenGL.VertexBuffer;
import mclone.gfx.OpenGL.VertexBufferLayout;
import mclone.gfx.OpenGL.ShaderPrimitiveUtil.ShaderPrimitiveType;
import mclone.gfx.OpenGL.VertexBufferLayout.VertexAttribute;

import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL33.*;

import java.lang.management.MemoryType;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class App {
    // The window handle
    private Window m_window;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        Logger logger = Logger.get();
        logger.error(this, "This is an example error message");
        logger.warn(this, "This is an example warn message");
        logger.trace(this, "This is an example trace message");
        logger.info(this, "This is an example info message");

        init();
        loop();

        m_window.dispose();
        Window.shutdownWindowSystem();
    }

    private void init() {
        Window.initializeWindowSystem();
        m_window = new Window("Window!", 720, 480, false);
        m_window.makeContextCurrent();
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        try(MemoryStack stack = MemoryStack.stackPush()) {
            GL.createCapabilities();
            Callback cb = GLUtil.setupDebugMessageCallback(System.out);

            // Set the clear color

            glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

            float[] vertices = {
                -0.5f, -0.5f, 0.0f,
                 0.5f, -0.5f, 0.0f,
                 0.0f,  0.5f, 0.0f
            };

            ArrayList<VertexAttribute> attributes = new ArrayList<>();
            attributes.add(new VertexAttribute(ShaderPrimitiveType.FLOAT32, 3));
            VertexBufferLayout vboLayout = new VertexBufferLayout(attributes);
            FloatBuffer fb = stack.mallocFloat(vertices.length);
            fb.put(vertices).flip();
            
            VertexBuffer vbo = new VertexBuffer(fb, vertices.length * 4, HardwareBuffer.UsageHints.USAGE_STATIC, vboLayout);

            String vertexShaderSource = "#version 330 core\n" +
                "layout (location = 0) in vec3 aPos;\n" +
                "void main()\n" +
                "{\n" +
                "   gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
                "}\0";
            String fragmentShaderSource = "#version 330 core\n" +
                "out vec4 FragColor;\n" +
                "void main()\n" +
                "{\n" +
                "   FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n" +
                "}\n";

            Shader shader = new Shader(new Shader.ShaderSource(vertexShaderSource, fragmentShaderSource));
            // Run the rendering loop until the user has attempted to close
            // the window or has pressed the ESCAPE key.
            while (!m_window.shouldClose() && !m_window.keyPressed(GLFW_KEY_ESCAPE)) {
                if (m_window.mouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
                    glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
                } else if (m_window.mouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT)) {
                    glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
                } else {
                    glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
                }
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
                m_window.setTitle("Window! Cursor pos: " + m_window.getMousePosition().get(0) + " "
                        + m_window.getMousePosition().get(1));

                shader.bind();
                vbo.bind();
                glDrawArrays(GL_TRIANGLES, 0, 3);

                // glfwSwapBuffers(window); // swap the color buffers
                m_window.swapBuffers();

                // Poll for window events. The key callback above will only be
                // invoked during this call.
                // glfwPollEvents();
                Window.windowSystemPollEvents();
            }

            vbo.dispose();
        }
    }

    public static void main(String[] args) {
        new App().run();
    }

}

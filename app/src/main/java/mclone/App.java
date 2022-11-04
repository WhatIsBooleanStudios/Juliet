package mclone;

import mclone.platform.Window;

import org.joml.Matrix4f;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import mclone.Logging.Logger;
import mclone.gfx.OpenGL.GraphicsAPI;
import mclone.gfx.OpenGL.HardwareBuffer;
import mclone.gfx.OpenGL.Shader;
import mclone.gfx.OpenGL.ShaderBuilder;
import mclone.gfx.OpenGL.ShaderPrimitiveUtil;
import mclone.gfx.OpenGL.Texture;
import mclone.gfx.OpenGL.VertexBuffer;
import mclone.gfx.OpenGL.IndexBuffer;
import mclone.gfx.OpenGL.VertexBufferLayout;
import mclone.gfx.OpenGL.ShaderPrimitiveUtil.ShaderPrimitiveType;
import mclone.gfx.OpenGL.VertexBufferLayout.VertexAttribute;

import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL33.*;

import java.lang.management.MemoryType;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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
                -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                 0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
                 0.5f,  0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                 -0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f
            };

            int[] indices = {
                0, 1, 2,
                2, 3, 0
            };

            ArrayList<VertexAttribute> attributes = new ArrayList<>();
            attributes.add(new VertexAttribute(ShaderPrimitiveType.FLOAT32, 3));
            attributes.add(new VertexAttribute(ShaderPrimitiveType.FLOAT32, 3));
            attributes.add(new VertexAttribute(ShaderPrimitiveType.FLOAT32, 2));
            VertexBufferLayout vboLayout = new VertexBufferLayout(attributes);
            FloatBuffer fb = stack.mallocFloat(vertices.length);
            fb.put(vertices).flip();

            IntBuffer ib = stack.mallocInt(6);
            ib.put(indices);
            ib.flip();
            
            VertexBuffer vbo = new VertexBuffer(fb, vertices.length * 4, HardwareBuffer.UsageHints.USAGE_STATIC, vboLayout);
            IndexBuffer  ibo = new IndexBuffer(ib, indices.length * 4, HardwareBuffer.UsageHints.USAGE_STATIC);

            String vertexShaderSource = "#version 330 core\n" +
                "layout (location = 0) in vec3 aPos;\n" +
                "layout (location = 1) in vec3 icolor;\n" +
                "layout (location = 2) in vec2 itexCoord;\n" +
                "out vec3 color;\n" +
                "out vec2 texCoord;\n" +
                "uniform mat4 transform;\n" +
                "void main()\n" +
                "{\n" +
                "   gl_Position = transform * vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
                "   color = icolor;\n" +
                "   texCoord = itexCoord;\n" +
                "}\0";
            String fragmentShaderSource = "#version 330 core\n" +
                "out vec4 FragColor;\n" +
                "in vec3 oColor;\n" +
                "in vec2 texCoord;\n" +
                "uniform sampler2D tex;\n" +
                "void main()\n" +
                "{\n" +
                "   FragColor = texture(tex, texCoord);\n" +
                "}\n";


            ShaderBuilder shaderBuilder = new ShaderBuilder();
            shaderBuilder.setShaderSource(vertexShaderSource, fragmentShaderSource);
            shaderBuilder.addUniform("transform", ShaderPrimitiveUtil.ShaderPrimitiveType.MAT4);
            Shader shader = shaderBuilder.get();

            Matrix4f transform = new Matrix4f()
                .identity()
                .ortho2D(-1.0f * ((float)m_window.getWidth() / m_window.getHeight()), 
                        1.0f * ((float)m_window.getWidth() / m_window.getHeight()),
                        -1.0f, 1.0f);
            transform.mul(new Matrix4f().identity().rotateZ(1.0f));
            shader.setUniformMat4("transform", transform);

            Texture texture = new Texture("textures/waves.jpeg");

            
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

                texture.bind(0);
                GraphicsAPI.drawIndexed(shader, vbo, ibo, 6);

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

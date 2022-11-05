package mclone;

import mclone.gfx.OpenGL.*;
import mclone.Platform.Window;

import org.joml.Matrix4f;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import mclone.Logging.Logger;
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

        init();
        loop();

        m_window.dispose();
        Window.shutdownWindowSystem();
        Logger.shutdown();
    }

    private void init() {
        Logger.initialize(true, "log.txt");
        Logger.info(this, "LWJGL VERSION " + Version.getVersion());
        Logger.error("App.init", this, "This is an example error message");
        Logger.warn("App.init", this, "This is an example warn message");
        Logger.trace("App.init", this, "This is an example trace message");
        Logger.info("App.init", this, "This is an example info message");
        Window.initializeWindowSystem();
        m_window = new Window("Window!", 720, 480, false);
        m_window.makeContextCurrent();
    }

    @Override
    public String toString() {
        return "mclone.App";
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
                "layout (std140) uniform Matrices {\n" +
                "    mat4 transform;\n" +
                "};\n" +
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
            shaderBuilder.addUniformBuffer("Matrices");
            Shader shader = shaderBuilder.get();

            Matrix4f transform = new Matrix4f()
                .identity()
                .ortho2D(-((float)m_window.getWidth() / m_window.getHeight()),
                        ((float)m_window.getWidth() / m_window.getHeight()),
                        -1.0f, 1.0f);

            UniformBuffer ubo = new UniformBuffer(null, 64, HardwareBuffer.UsageHints.USAGE_DYNAMIC);

            Texture texture = new Texture("textures/waves.jpeg");

            
            // Run the rendering loop until the user has attempted to close
            // the window or has pressed the ESCAPE key.
            while (!m_window.shouldClose() && !m_window.keyPressed(GLFW_KEY_ESCAPE)) {
                try(MemoryStack loopStack = MemoryStack.stackPush()) {
                    GraphicsAPI.setClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                    GraphicsAPI.clear();

                    m_window.setTitle("Window! Cursor pos: " + m_window.getMousePosition().get(0) + " "
                        + m_window.getMousePosition().get(1));

                    transform.mul(new Matrix4f().identity().rotateZ((float) (2 * Math.PI * (1 / 60.0))));

                    ubo.setData(transform.get(loopStack.mallocFloat(16)), 4 * 16);
                    ubo.setToBindingPoint(0);

                    texture.bind(0);
                    shader.setUniformBuffer("Matrices", 0);
                    GraphicsAPI.drawIndexed(shader, vbo, ibo, 6);

                    m_window.swapBuffers();

                    Window.windowSystemPollEvents();
                }
            }

            ibo.dispose();
            vbo.dispose();
            ubo.dispose();
            shader.dispose();
        }


    }

    public static void main(String[] args) {
        new App().run();
    }

}

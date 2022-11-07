package mclone;

import mclone.GFX.Renderer.Camera;
import mclone.GFX.OpenGL.*;
import mclone.Platform.Window;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import mclone.Logging.Logger;
import mclone.GFX.OpenGL.ShaderPrimitiveUtil.ShaderPrimitiveType;
import mclone.GFX.OpenGL.VertexBufferLayout.VertexAttribute;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class App {
    // The window handle
    private Window window;

    public void run() {

        init();
        loop();

        GraphicsAPI.shutdown();
        window.dispose();
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

        window = new Window("Window!", 1024, 768, false);
        window.makeContextCurrent();
        GraphicsAPI.initialize();
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

            // Set the clear color

            float[] vertices = {
                -0.5f, -0.5f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                 0.5f, -0.5f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
                 0.5f,  0.5f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                 -0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f
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
            shaderBuilder.setShaderSource("basicVS.glsl", vertexShaderSource, "basicFS.glsl", fragmentShaderSource);
            shaderBuilder.addUniformBuffer("Matrices");
            Shader shader = shaderBuilder.createShader("BasicShader");

            Camera camera = new Camera(4.0f / 3.0f, 45.0f);
            Matrix4f transform = camera.getProjectionXView();

            UniformBuffer ubo = new UniformBuffer(null, 64, HardwareBuffer.UsageHints.USAGE_DYNAMIC);

            Texture texture = new Texture("textures/waves.jpeg");

            Vector2f screenCenter = new Vector2f(window.getWidth() / 2.0f, window.getHeight() / 2.0f);
            window.setMousePosition(screenCenter);
            Vector2f previousMousePosition = window.getMousePosition();
            window.captureCursor(true);

            
            // Run the rendering loop until the user has attempted to close
            // the window or has pressed the ESCAPE key.
            while (!window.shouldClose() && !window.keyPressed(GLFW_KEY_ESCAPE)) {
                try(MemoryStack loopStack = MemoryStack.stackPush()) {
                    GraphicsAPI.setClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                    GraphicsAPI.clear();

                    window.setTitle("Window! Cursor pos: " + window.getMousePosition().get(0) + " "
                        + window.getMousePosition().get(1));

                    final float cameraSpeed = 0.05f; // adjust accordingly
                    if (window.keyPressed(GLFW_KEY_W)) {
                        //cameraPos += cameraSpeed * cameraFront;
                        camera.offsetCameraPosition(camera.getDirection().mul(-cameraSpeed));
                    }
                    if (window.keyPressed(GLFW_KEY_S)) {
                        //cameraPos -= cameraSpeed * cameraFront;
                        camera.offsetCameraPosition(camera.getDirection().mul(cameraSpeed));
                    }
                    if (window.keyPressed(GLFW_KEY_A)) {
                        //cameraPos -= glm::normalize(glm::cross(cameraFront, cameraUp)) * cameraSpeed;
                        camera.offsetCameraPosition(camera.getDirection().cross(new Vector3f(0.0f, 1.0f, 0.0f)).normalize().mul(cameraSpeed));
                    }
                    if (window.keyPressed(GLFW_KEY_D)) {
                        //cameraPos += glm::normalize(glm::cross(cameraFront, cameraUp)) * cameraSpeed;
                        camera.offsetCameraPosition(camera.getDirection().cross(new Vector3f(0.0f, 1.0f, 0.0f)).normalize().mul(-cameraSpeed));
                    }

                    Vector2f mousePosition = window.getMousePosition();
                    Vector2f mouseOffset = new Vector2f(mousePosition).sub(previousMousePosition);
                    previousMousePosition.set(mousePosition);
                    float sensitivity = 0.05f;
                    mouseOffset.mul(sensitivity);

                    camera.offsetYaw(mouseOffset.x);
                    camera.offsetPitch(mouseOffset.y);


                    transform = camera.getProjectionXView();
                    ubo.setData(transform.get(loopStack.mallocFloat(16)), 4 * 16);
                    ubo.setToBindingPoint(0);

                    texture.bind(0);
                    shader.setUniformBuffer("Matrices", 0);
                    GraphicsAPI.drawIndexed(shader, vbo, ibo, 6);

                    window.swapBuffers();

                    Window.windowSystemPollEvents();
                }
            }

            ibo.dispose();
            vbo.dispose();
            ubo.dispose();
            shader.dispose();
            texture.dispose();
        }


    }

    public static void main(String[] args) {
        new App().run();
    }

}

package mclone;

import mclone.GFX.Renderer.Camera;
import mclone.GFX.Renderer.FPSCameraController;
import mclone.GFX.Renderer.Model;
import mclone.GFX.OpenGL.*;
import mclone.GFX.Renderer.Renderer;
import mclone.Platform.Window;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.system.MemoryStack;

import mclone.Logging.Logger;
import mclone.GFX.OpenGL.ShaderPrimitiveUtil.ShaderPrimitiveType;
import mclone.GFX.OpenGL.VertexBufferLayout.VertexAttribute;

import static org.lwjgl.glfw.GLFW.*;

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
        try(MemoryStack stack = MemoryStack.stackPush()) {

            Model model = new Model("models/gizmo.glb");
            FPSCameraController fpsCameraController = new FPSCameraController(window, new Vector3f(0.0f, 0.0f, -1.0f), 0.0f, 0.0f);

            Vector2f screenCenter = new Vector2f(window.getWidth() / 2.0f, window.getHeight() / 2.0f);
            window.setMousePosition(screenCenter);
            window.captureCursor(true);

            Renderer renderer = new Renderer();

            // Run the rendering loop until the user has attempted to close
            // the window or has pressed the ESCAPE key.
            while (!window.shouldClose() && !window.keyPressed(GLFW_KEY_ESCAPE)) {
                try(MemoryStack loopStack = MemoryStack.stackPush()) {
                    GraphicsAPI.setClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                    GraphicsAPI.clear();

                    window.setTitle("Window! Cursor pos: " + window.getMousePosition().get(0) + " "
                        + window.getMousePosition().get(1));

                    fpsCameraController.update(window);

                    renderer.begin(fpsCameraController);
                    renderer.drawModel(model);
                    renderer.end();

                    window.swapBuffers();

                    Window.windowSystemPollEvents();
                }
            }
        }


    }

    public static void main(String[] args) {
        new App().run();
    }

}

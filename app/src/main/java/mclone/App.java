package mclone;

import mclone.GFX.Renderer.*;
import mclone.GFX.OpenGL.*;
import mclone.Platform.Window;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import mclone.Logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

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

            FPSCameraController fpsCameraController = new FPSCameraController(window, new Vector3f(0.0f, 0.0f, -1.0f), 0.0f, (float)Math.PI);

            Vector2f screenCenter = new Vector2f(window.getWidth() / 2.0f, window.getHeight() / 2.0f);
            window.setMousePosition(screenCenter);
            window.captureCursor(true);

            Renderer renderer = new Renderer();
            Model model = renderer.getModelLoader().load("models/salmonCube.glb");
            Model smallerModel = renderer.getModelLoader().load("models/roughWaveBall.glb");
            Model metalCube = renderer.getModelLoader().load("models/metalCube.glb");

            // Run the rendering loop until the user has attempted to close
            // the window or has pressed the ESCAPE key.
            PointLight pointLight0 = new PointLight(new Vector3f(0.0f, -0.5f, -1.0f), new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
            PointLight pointLight1 = new PointLight(new Vector3f(0.5f, 0.0f, -1.0f), new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
            PointLight pointLight2 = new PointLight(new Vector3f(-0.5f, 0.0f, -1.0f), new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
            PointLight pointLight3 = new PointLight(new Vector3f(0.0f, 0.5f, -1.0f), new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);

            while (!window.shouldClose() && !window.keyPressed(GLFW_KEY_ESCAPE)) {
                try(MemoryStack loopStack = MemoryStack.stackPush()) {
                    GraphicsAPI.setClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                    GraphicsAPI.clear();

                    window.setTitle("Window! Cursor pos: " + window.getMousePosition().get(0) + " "
                        + window.getMousePosition().get(1));

                    fpsCameraController.update(window);

                    renderer.begin(fpsCameraController);

                    renderer.beginLightConfiguration();
                    renderer.attachPointLight(pointLight0);
                    renderer.attachPointLight(pointLight1);
                    renderer.attachPointLight(pointLight2);
                    renderer.attachPointLight(pointLight3);
                    renderer.endLightConfiguration();

                    renderer.beginModelRendering();
                    renderer.drawModel(model, new Vector3f(0.0f, 0.0f, 1.0f));
                    Vector3f lightPos0 = new Vector3f(0.0f, 0.0f,  -1.6f);
                    renderer.drawModel(smallerModel, lightPos0);
                    renderer.drawModel(metalCube, new Vector3f(0.0f));
                    renderer.endModelRendering();

                    renderer.end();

                    window.swapBuffers();

                    Window.windowSystemPollEvents();
                }
            }

            renderer.shutdown();
        }


    }

    public static void main(String[] args) {
        new App().run();
    }

}

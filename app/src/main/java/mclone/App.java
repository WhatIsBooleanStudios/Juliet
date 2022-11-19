package mclone;

import imgui.ImGui;
import mclone.GFX.Renderer.*;
import mclone.GFX.OpenGL.*;
import mclone.Platform.Window;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.nuklear.*;
import static org.lwjgl.nuklear.Nuklear.*;
import org.lwjgl.system.MemoryStack;

import mclone.Logging.Logger;

import java.nio.IntBuffer;

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
            fpsCameraController.update(window);

            Vector2f screenCenter = new Vector2f(window.getWidth() / 2.0f, window.getHeight() / 2.0f);
            window.setMousePosition(screenCenter);

            Renderer renderer = new Renderer(window);
            Model model = renderer.getModelLoader().load("models/salmonCube.glb");
            Model smallerModel = renderer.getModelLoader().load("models/roughWaveBall.glb");
            Model metalCube = renderer.getModelLoader().load("models/metalCube.glb");

            // Run the rendering loop until the user has attempted to close
            // the window or has pressed the ESCAPE key.
            PointLight pointLight0 = new PointLight(new Vector3f(0.0f, -0.5f, -1.0f), new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
            PointLight pointLight1 = new PointLight(new Vector3f(0.5f, 0.0f, -1.0f), new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
            PointLight pointLight2 = new PointLight(new Vector3f(-0.5f, 0.0f, -1.0f), new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
            PointLight pointLight3 = new PointLight(new Vector3f(0.0f, 0.5f, -1.0f), new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);

            SpotLight spotLight = new SpotLight(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f), 1.0f, (float)Math.cos(Math.PI / 12.0));

            int EASY = 0;
            int HARD = 1;
            int op = EASY;
            IntBuffer compression = BufferUtils.createIntBuffer(1).put(0, 20);
            NkColorf background = NkColorf.create()
                .r(0.10f)
                .g(0.18f)
                .b(0.24f)
                .a(1.0f);

            boolean focusedOnEditor = false;

            GUIManager2 guiManager2 = new GUIManager2(window);
            guiManager2.init();

            while (!window.shouldClose() && !window.keyPressed(Window.KEY_ESCAPE)) {
                try(MemoryStack loopStack = MemoryStack.stackPush()) {
                    window.makeContextCurrent();
                    GraphicsAPI.setClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                    GraphicsAPI.clear();

                    window.setTitle("Window! Cursor pos: " + window.getMousePosition().get(0) + " "
                        + window.getMousePosition().get(1));

                    Window.windowSystemPollEvents();

                    if(window.keyPressed(Window.KEY_1)) {
                        focusedOnEditor = false;
                    } else if(window.keyPressed(Window.KEY_2)) {
                        focusedOnEditor = true;
                    }

                    if(focusedOnEditor) {
                        fpsCameraController.update(window);
                    }
                    window.captureCursor(focusedOnEditor);

                    renderer.getLightManager().addPointLight(pointLight0);
                    renderer.getLightManager().addPointLight(pointLight1);
                    renderer.getLightManager().addPointLight(pointLight2);
                    renderer.getLightManager().addPointLight(pointLight3);
                    spotLight.setPosition(fpsCameraController.getCameraPosition());
                    spotLight.setDirection(fpsCameraController.getCameraDirection());
                    renderer.getLightManager().addSpotLight(spotLight);

                    renderer.begin(fpsCameraController);


                    renderer.beginModelRendering();
                    renderer.drawModel(model, new Vector3f(0.0f, 0.0f, 1.0f));
                    Vector3f lightPos0 = new Vector3f(0.0f, 0.0f,  -1.6f);
                    renderer.drawModel(smallerModel, lightPos0);
                    renderer.drawModel(metalCube, new Vector3f(0.0f));
                    renderer.endModelRendering();


                    renderer.end();

                    renderer.getLightManager().clearLights();

                    guiManager2.newFrame();
                    ImGui.showDemoWindow();
                    guiManager2.endFrame();

                    window.swapBuffers();

                }
            }

            guiManager2.shutdown();
            renderer.shutdown();
        }


    }

    public static void main(String[] args) {
        new App().run();
    }

}

package mclone;

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

            while (!window.shouldClose() && !window.keyPressed(GLFW_KEY_ESCAPE)) {
                try(MemoryStack loopStack = MemoryStack.stackPush()) {
                    GraphicsAPI.setClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                    GraphicsAPI.clear();

                    window.setTitle("Window! Cursor pos: " + window.getMousePosition().get(0) + " "
                        + window.getMousePosition().get(1));

                    renderer.getGUIManager().beginInputProcessing();
                    Window.windowSystemPollEvents();
                    renderer.getGUIManager().endInputProcessing();

                    if(window.keyPressed(GLFW_KEY_1)) {
                        focusedOnEditor = false;
                    } else if(window.keyPressed(GLFW_KEY_2)) {
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

                    try(MemoryStack nkStack = MemoryStack.stackPush()) {
                        NkRect rect = NkRect.malloc(nkStack);

                        NkContext ctx = renderer.getGUIManager().getContext();
                        if (nk_begin(ctx,
                            "Demo",
                            nk_rect(2, 2, 230, 250, rect),
                            NK_WINDOW_BORDER | NK_WINDOW_MOVABLE | NK_WINDOW_SCALABLE | NK_WINDOW_MINIMIZABLE | NK_WINDOW_TITLE
                        )) {
                            nk_layout_row_static(ctx, 30, 80, 1);
                            if (nk_button_label(ctx, "button")) {
                                System.out.println("button pressed");
                            }

                            nk_layout_row_dynamic(ctx, 30, 2);
                            if (nk_option_label(ctx, "easy", op == EASY)) {
                                op = EASY;
                            }
                            if (nk_option_label(ctx, "hard", op == HARD)) {
                                op = HARD;
                            }

                            nk_layout_row_dynamic(ctx, 25, 1);
                            nk_property_int(ctx, "Compression:", 0, compression, 100, 10, 1);

                            nk_layout_row_dynamic(ctx, 20, 1);
                            nk_label(ctx, "background:", NK_TEXT_LEFT);
                            nk_layout_row_dynamic(ctx, 25, 1);
                            if (nk_combo_begin_color(ctx, nk_rgb_cf(background, NkColor.malloc(stack)), NkVec2.malloc(stack).set(nk_widget_width(ctx), 400))) {
                                nk_layout_row_dynamic(ctx, 120, 1);
                                nk_color_picker(ctx, background, NK_RGBA);
                                nk_layout_row_dynamic(ctx, 25, 1);
                                background
                                    .r(nk_propertyf(ctx, "#R:", 0, background.r(), 1.0f, 0.01f, 0.005f))
                                    .g(nk_propertyf(ctx, "#G:", 0, background.g(), 1.0f, 0.01f, 0.005f))
                                    .b(nk_propertyf(ctx, "#B:", 0, background.b(), 1.0f, 0.01f, 0.005f))
                                    .a(nk_propertyf(ctx, "#A:", 0, background.a(), 1.0f, 0.01f, 0.005f));
                                nk_combo_end(ctx);
                            }
                        }
                        nk_end(ctx);
                    }

                    renderer.end();

                    renderer.getLightManager().clearLights();


                    window.swapBuffers();

                }
            }

            renderer.shutdown();
        }


    }

    public static void main(String[] args) {
        new App().run();
    }

}

package mclone;

import imgui.ImGui;
import mclone.GFX.Renderer.*;
import mclone.GFX.OpenGL.*;
import mclone.Platform.Window;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import mclone.Logging.Logger;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
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

            window.setMousePosition(window.getScreenCenter());

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
            SpotLight topSpotLight = new SpotLight(new Vector3f(4.0f, 10.0f, 4.0f), new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f), 100.0f, (float)Math.cos(Math.PI / 12.0f));

            float[] instanceData;
            {
                int xLength = 16;
                int yLength = 16;
                int zLength = 16;
                instanceData = new float[16 * xLength * yLength * zLength];
                for(int x = 0; x < xLength; x++) {
                    for(int y = 0; y < yLength; y++) {
                        for(int z = 0; z < zLength; z++) {
                            Matrix4f transform = (new Matrix4f()).identity().translate(0.5f * x, 0.5f * y, 0.5f * z);
                            transform.get(instanceData, ((x * yLength * zLength + y * zLength + z) * 16));
                        }
                    }
                }
            }

            FloatBuffer instanceFloatBuffer = MemoryUtil.memAllocFloat(instanceData.length);
            instanceFloatBuffer.put(instanceData);
            instanceFloatBuffer.flip();
            InstanceBuffer instanceBuffer = new InstanceBuffer(instanceFloatBuffer, instanceData.length * 4L, HardwareBuffer.UsageHints.USAGE_DYNAMIC);

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
                        renderer.getGUIManager().setInputApplies(true);
                    } else if(window.keyPressed(Window.KEY_2)) {
                        focusedOnEditor = true;
                        renderer.getGUIManager().setInputApplies(false);
                    }

                    if(focusedOnEditor) {
                        fpsCameraController.update(window);
                        window.setMousePosition(new Vector2f(window.getScreenCenter()));
                    }
                    window.captureCursor(focusedOnEditor);

                    renderer.getLightManager().addPointLight(pointLight0);
                    renderer.getLightManager().addPointLight(pointLight1);
                    renderer.getLightManager().addPointLight(pointLight2);
                    renderer.getLightManager().addPointLight(pointLight3);
                    spotLight.setPosition(fpsCameraController.getCameraPosition());
                    spotLight.setDirection(fpsCameraController.getCameraDirection());
                    //renderer.getLightManager().addSpotLight(spotLight);
                    renderer.getLightManager().addSpotLight(topSpotLight);

                    renderer.begin();

                    renderer.beginModelRendering(fpsCameraController);
                    renderer.drawModel(model, new Vector3f(0.0f, 0.0f, 1.0f));
                    Vector3f lightPos0 = new Vector3f(0.0f, 0.0f,  -1.6f);
                    renderer.drawModel(smallerModel, lightPos0);
                    renderer.drawModel(metalCube, new Vector3f(0.0f));
                    renderer.drawModelInstanced(model, instanceBuffer, 16 * 16 * 16);
                    renderer.endModelRendering();

                    ImGui.showDemoWindow();
                    {
                        ImGui.begin("Camera");
                        ImGui.text("CameraPosition: " + fpsCameraController.getCameraPosition());
                        ImGui.text("CameraDirection: " + fpsCameraController.getCameraDirection());
                        ImGui.end();
                    }

                    renderer.end();

                    renderer.getLightManager().clearLights();


                    window.swapBuffers();

                }
            }

            MemoryUtil.memFree(instanceFloatBuffer);
            renderer.shutdown();
        }


    }

    public static void main(String[] args) {
        new App().run();
    }

}

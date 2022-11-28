package mclone.Editor;

import imgui.ImGui;
import imgui.ImVec2;
import mclone.ECS.Entity;
import mclone.ECS.EntityScriptComponent;
import mclone.ECS.NameComponent;
import mclone.ECS.Scene;
import mclone.GFX.OpenGL.FrameBuffer;
import mclone.GFX.OpenGL.GraphicsAPI;
import mclone.GFX.Renderer.CameraController;
import mclone.GFX.Renderer.FPSCameraController;
import mclone.GFX.Renderer.Model;
import mclone.GFX.Renderer.Renderer;
import mclone.Logging.Logger;
import mclone.Platform.Window;
import org.joml.Vector3f;

import java.awt.*;

public class Editor {
    public Editor() {}

    public void init() {
        mainWindow = new Window("mclone Editor", 900, 600, false);
        mainWindow.makeContextCurrent();
        mainWindow.setMousePosition(mainWindow.getScreenCenter());

        renderer = new Renderer(mainWindow);

        testModel = renderer.getModelLoader().load("models/salmonCube.glb");

        mainWindow.setMousePosition(mainWindow.getScreenCenter());
        cameraController = new FPSCameraController(mainWindow, new Vector3f(0.0f, 0.0f, 0.0f), (float)mainWindow.getWidth() / (float)mainWindow.getHeight(),0.0f, (float)Math.PI);

        tempScene = new Scene();
        tempScene.disableScriptSystem();
        Entity tempEntity = tempScene.createEntity();
        tempEntity.addComponent(new NameComponent("Entity_test"));
        tempEntity.addComponent(new EntityScriptComponent("mclone.TestScript"));
    }

    public void update(float delta) {
        Window.windowSystemPollEvents();
        mainWindow.makeContextCurrent();
        updateInput();

        GraphicsAPI.setClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GraphicsAPI.clear();

        {
            renderer.begin();
            {
                ImGui.beginMainMenuBar();
                ImGui.menuItem("menu item?");
                ImGui.endMainMenuBar();

                ImGui.dockSpaceOverViewport();
                ImGui.begin("Scene Tree");
                tempScene.process(delta);
                tempScene.tempSaveToFile();
                ImGui.end();
                ImGui.showDemoWindow();
                renderScene();
            }
            renderer.end();
        }

        mainWindow.swapBuffers();
    }

    private void displayEntityTree(Scene scene) {

    }

    private void renderScene() {
        beginSceneRendering();
        {
            GraphicsAPI.setClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GraphicsAPI.clear();

            renderer.beginModelRendering(cameraController);
            {
                renderer.drawModel(testModel, new Vector3f(0.0f, 0.0f, 1.0f));
            }
            renderer.endModelRendering();
        }
        endSceneRendering();
    }

    private void beginSceneRendering() {
        ImGui.begin("Scene");
        ImVec2 regionAvail = ImGui.getContentRegionAvail();
        int windowWidth = (int)regionAvail.x;
        int windowHeight = (int)regionAvail.y;

        if(sceneFramebuffer == null) {
            sceneFramebuffer = new FrameBuffer(windowWidth == 0 ? 1 : windowWidth, windowHeight == 0 ? 1 : windowHeight);
        } else if(sceneFramebuffer.getWidth() != windowWidth || sceneFramebuffer.getHeight() != windowHeight) {
            sceneFramebuffer.resize(windowWidth, windowHeight);
        }

        cameraController.updateProjection(regionAvail.x / regionAvail.y, (float) Math.PI / 4.0f);
        GraphicsAPI.updateViewport((int) regionAvail.x, (int) regionAvail.y);
        sceneFramebuffer.bindAsDrawAttachment();
    }

    private void endSceneRendering() {
        FrameBuffer.bindDefaultDrawAttachment();

        ImGui.image(sceneFramebuffer.getColorAttachmentID(), sceneFramebuffer.getWidth(), sceneFramebuffer.getHeight(), 0.0f, 1.0f, 1.0f , 0.0f);
        ImGui.end();
    }

    boolean focusedOnScene = false;
    private void updateInput() {
        if(mainWindow.keyPressed(Window.KEY_2)) {
            mainWindow.captureCursor(true);
            mainWindow.setMousePosition(mainWindow.getScreenCenter());
            focusedOnScene = true;
        } else if(mainWindow.keyPressed(Window.KEY_1)) {
            mainWindow.captureCursor(false);
            focusedOnScene = false;
        }
        if(focusedOnScene) {
            cameraController.update(mainWindow);
            mainWindow.setMousePosition(mainWindow.getScreenCenter());
        }
    }

    public void shutdown() {
        if(sceneFramebuffer != null) {
            sceneFramebuffer.dispose();
        }

        renderer.shutdown();
        mainWindow.dispose();
    }

    boolean _shouldExit = false;
    public boolean shouldExit() {
        return _shouldExit || mainWindow.shouldClose();
    }

    private Window mainWindow;
    private Renderer renderer;
    private FrameBuffer sceneFramebuffer = null;

    private Model testModel;
    private Scene tempScene;
    private FPSCameraController cameraController;
}

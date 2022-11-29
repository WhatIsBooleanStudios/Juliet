package mclone.Editor;

import com.artemis.utils.IntBag;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
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
import mclone.Platform.TimeStep;
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
        Entity tempEntity = tempScene.createEntity("ENTITY_TEST0");
        tempEntity.addComponent(new EntityScriptComponent("mclone.TestScript"));
        Entity tempEntity2 = tempScene.createEntity("ENTITY_TEST1");
        tempEntity2.addComponent(new EntityScriptComponent("mclone.TestScript"));
        tempScene.process(0.0f);
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
                ImGui.showDemoWindow();
                displayEntityTree();
                renderScene();
                displayPerformance(delta);
            }
            renderer.end();
        }

        mainWindow.swapBuffers();
    }

    private void displayPerformance(float deltaTime) {
        ImGui.begin("performance");
        ImGui.text("dt = " + String.format("%.2f", deltaTime) + "ms");
        ImGui.end();
    }

    Entity currentSelectedEntity = null;
    private void displayEntityTree() {
        tempScene.__compileEntitiesList();
        ImGui.begin("Scene Hierarchy");
        for(int i = 0; i < tempScene.__getNumEntities(); i++) {
            int nodeFlags = ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen;
            Entity entity = tempScene.__getEntityByInternalListIndex(i);

            if (currentSelectedEntity != null && currentSelectedEntity.equals(entity)) {
                nodeFlags |= ImGuiTreeNodeFlags.Selected;
            }

            if(ImGui.treeNodeEx(i, nodeFlags, entity.getName())) {
                if (ImGui.isItemClicked() || ImGui.isItemFocused()) {
                    currentSelectedEntity = entity;
                }
            }
        }
        ImGui.text(currentSelectedEntity != null ? currentSelectedEntity.toString() : "null");
        ImGui.end();

        displayEntityPropertyList();
    }

    private static class ImGuiEntityPropertiesFields {
        public ImGuiEntityPropertiesFields() {}
        public ImString scriptPath = new ImString();
    } ImGuiEntityPropertiesFields entityPropertiesFields = new ImGuiEntityPropertiesFields();

    private void displayEntityPropertyList() {
        ImGui.begin("Entity Properties");
        if(currentSelectedEntity != null) {
            if(currentSelectedEntity.hasComponent(EntityScriptComponent.class)) {
                if(ImGui.collapsingHeader("Script Component")) {
                    EntityScriptComponent scriptComponent = currentSelectedEntity.getComponent(EntityScriptComponent.class);

                    ImGui.indent();
                    ImGui.text(scriptComponent.classname);
                    ImGui.sameLine();

                    if(ImGui.button("edit")) {
                        ImGui.openPopup("Edit Script Path");
                        entityPropertiesFields.scriptPath.set(scriptComponent.classname);
                    }

                    if(displayTextEditDialogueModal(
                        entityPropertiesFields.scriptPath,
                        "Edit Script Path", "Script Path"
                    )) {
                        scriptComponent.classname = entityPropertiesFields.scriptPath.get();
                    }

                }
            }
        }
        ImGui.end();
    }

    private boolean displayTextEditDialogueModal(ImString field, String windowTitle, String fieldName) {
        boolean userShouldUpdate = false;

        int windowFlags = ImGuiWindowFlags.AlwaysAutoResize;
        ImGui.setNextWindowPos(ImGui.getWindowWidth() / 2.0f, ImGui.getWindowHeight() / 2.0f);
        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowPos(center.x, center.y, ImGuiCond.Appearing, 0.5f, 0.5f);

        if(ImGui.beginPopupModal(windowTitle, windowFlags)) {
            ImGui.inputText(fieldName, field, ImGuiInputTextFlags.CallbackResize);


            if(ImGui.button("save")) {
                ImGui.closeCurrentPopup();
                userShouldUpdate = true;
            }

            ImGui.sameLine();

            if(ImGui.button("cancel")) {
                ImGui.closeCurrentPopup();
            }

            ImGui.endPopup();
        }

        ImGui.unindent();
        return userShouldUpdate;
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
            sceneFramebuffer = new FrameBuffer(windowWidth, windowHeight);
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

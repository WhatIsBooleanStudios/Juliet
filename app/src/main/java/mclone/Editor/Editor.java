package mclone.Editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import mclone.ECS.Entity;
import mclone.ECS.EntityScriptComponent;
import mclone.ECS.Scene;
import mclone.ECS.TransformComponent;
import mclone.GFX.OpenGL.FrameBuffer;
import mclone.GFX.OpenGL.GraphicsAPI;
import mclone.GFX.Renderer.FPSCameraController;
import mclone.GFX.Renderer.Model;
import mclone.GFX.Renderer.Renderer;
import mclone.Platform.Window;
import org.joml.Vector3f;

import javax.xml.crypto.dsig.Transform;

public class Editor {
    public Editor() {}

    public void init() {
        mainWindow = new Window("mclone Editor", 900, 600, false);
        mainWindow.makeContextCurrent();
        mainWindow.setMousePosition(mainWindow.getScreenCenter());

        renderer = new Renderer(mainWindow);

        EditorIcons.load();

        testModel = renderer.getModelLoader().load("models/salmonCube.glb");

        mainWindow.setMousePosition(mainWindow.getScreenCenter());
        cameraController = new FPSCameraController(mainWindow, new Vector3f(0.0f, 0.0f, 0.0f), (float)mainWindow.getWidth() / (float)mainWindow.getHeight(),0.0f, (float)Math.PI);

        tempScene = new Scene();
        tempScene.disableScriptSystem();
    }

    public void update(float delta) {
        Window.windowSystemPollEvents();
        mainWindow.makeContextCurrent();
        updateInput();

        tempScene.process(0.0f);

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

        if(ImGui.imageButton(EditorIcons.addTexture.getNativeHandle(), (float)ImGui.getFont().getFontSize(), (float)ImGui.getFontSize())) {
            ImGui.openPopup("Create New Entity");
        }

        ImGui.sameLine();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.inputText("##EntitySearch", entityPropertiesFields.entitySearchBarContents, ImGuiInputTextFlags.CallbackResize);
        ImGui.separator();

        ImGui.spacing();

        if(displayTextEditDialogueModal(entityPropertiesFields.entityCreateEntityName, "Create New Entity", "Entity Name")) {
            Entity entity = tempScene.createEntity(entityPropertiesFields.entityCreateEntityName.get());
            entity.addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, 0.0f)));
        }


        //ImGui.spacing();


        for(int i = 0; i < tempScene.__getNumEntities(); i++) {
            int nodeFlags = ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen;
            Entity entity = tempScene.__getEntityByInternalListIndex(i);

            if(entity.getName().contains(entityPropertiesFields.entitySearchBarContents.get())) {

                if (currentSelectedEntity != null && currentSelectedEntity.equals(entity)) {
                    nodeFlags |= ImGuiTreeNodeFlags.Selected;
                }

                if (ImGui.treeNodeEx(i, nodeFlags, entity.getName())) {
                    if (ImGui.isItemClicked() || ImGui.isItemFocused()) {
                        currentSelectedEntity = entity;
                    }
                }

            }
        }
        ImGui.text(currentSelectedEntity != null ? currentSelectedEntity.toString() : "null");
        ImGui.end();

        displayEntityPropertyList();
    }

    private static class ImGuiEntityPropertiesFields {
        public ImGuiEntityPropertiesFields() {}

        public ImString entityCreateEntityName = new ImString();
        public ImString entitySearchBarContents = new ImString();
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
                    ImGui.unindent();

                    if(displayTextEditDialogueModal(
                        entityPropertiesFields.scriptPath,
                        "Edit Script Path", "Script Path"
                    )) {
                        scriptComponent.classname = entityPropertiesFields.scriptPath.get();
                    }

                }
            }

            if(currentSelectedEntity.hasComponent(TransformComponent.class)) {
                TransformComponent component = currentSelectedEntity.getComponent(TransformComponent.class);

                if(ImGui.collapsingHeader("Transform")) {
                    ImGui.indent();
                    float[] x = new float[]{component.position.x};
                    float[] y = new float[]{component.position.y};
                    float[] z = new float[]{component.position.z};

                    ImGui.text("Position");
                    ImGui.indent();

                    ImGui.text("X");
                    ImGui.sameLine();
                    ImGui.dragFloat("##X", x);

                    ImGui.text("Y");
                    ImGui.sameLine();
                    ImGui.dragFloat("##Y", y);

                    ImGui.text("Z");
                    ImGui.sameLine();
                    ImGui.dragFloat("##Z", z);

                    ImGui.unindent();

                    component.position.x = x[0];
                    component.position.y = y[0];
                    component.position.z = z[0];

                    ImGui.unindent();
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

        //ImGui.unindent();
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

        EditorIcons.dispose();

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

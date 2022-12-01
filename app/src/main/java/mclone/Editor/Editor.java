package mclone.Editor;

import imgui.ImColor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.internal.ImGuiWindow;
import imgui.type.ImString;
import mclone.ECS.*;
import mclone.GFX.OpenGL.FrameBuffer;
import mclone.GFX.OpenGL.GraphicsAPI;
import mclone.GFX.Renderer.FPSCameraController;
import mclone.GFX.Renderer.Model;
import mclone.GFX.Renderer.Renderer;
import mclone.Platform.Window;
import org.joml.Vector3f;

public class Editor {
    public Editor() {
    }

    public void init() {
        mainWindow = new Window("mclone Editor", 900, 600, false);
        mainWindow.makeContextCurrent();
        mainWindow.setMousePosition(mainWindow.getScreenCenter());

        renderer = new Renderer(mainWindow);

        EditorIcons.load();

        testModel = renderer.getModelLoader().load("models/salmonCube.glb");

        mainWindow.setMousePosition(mainWindow.getScreenCenter());
        cameraController = new FPSCameraController(mainWindow, new Vector3f(0.0f, 0.0f, 0.0f), (float) mainWindow.getWidth() / (float) mainWindow.getHeight(), 0.0f, (float) Math.PI);

        tempScene = new Scene(renderer);
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
        ImGui.begin("Scene Hierarchy", ImGuiWindowFlags.AlwaysUseWindowPadding);

        if (ImGui.imageButton(EditorIcons.addTexture.getNativeHandle(), (float) ImGui.getFont().getFontSize(), (float) ImGui.getFontSize())) {
            ImGui.openPopup("Create New Entity");
        }

        ImGui.sameLine();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.inputText("##EntitySearch", entityPropertiesFields.entitySearchBarContents, ImGuiInputTextFlags.CallbackResize);
        ImGui.separator();

        ImGui.spacing();

        if (displayTextEditDialogueModal(entityPropertiesFields.entityCreateEntityName, "Create New Entity", "Entity Name")) {
            if (entityPropertiesFields.entityCreateEntityName.isNotEmpty() && !tempScene.hasEntityByName(entityPropertiesFields.entityCreateEntityName.get())) {
                Entity entity = tempScene.createEntity(entityPropertiesFields.entityCreateEntityName.get());
                entity.addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, 0.0f)));
            }
        }


        for (int i = 0; i < tempScene.__getNumEntities(); i++) {
            int nodeFlags = ImGuiTreeNodeFlags.Leaf |
                ImGuiTreeNodeFlags.NoTreePushOnOpen |
                ImGuiTreeNodeFlags.FramePadding;
            Entity entity = tempScene.__getEntityByInternalListIndex(i);

            if (entity.getName().contains(entityPropertiesFields.entitySearchBarContents.get())) {

                if (currentSelectedEntity != null && currentSelectedEntity.equals(entity)) {
                    nodeFlags |= ImGuiTreeNodeFlags.Selected;
                }


                ImGui.pushID(i);
                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0.0f, 3.0f);
                if (ImGui.treeNodeEx(i, nodeFlags, entity.getName())) {
                    if (ImGui.isItemClicked() || ImGui.isItemFocused()) {
                        currentSelectedEntity = entity;
                    }
                }
                ImGui.separator();
                ImGui.popStyleVar();
                ImGui.popID();

            }
        }

        ImGui.text(currentSelectedEntity != null ? currentSelectedEntity.toString() : "null");
        ImGui.end();

        displayEntityPropertyList();
    }

    private static class ImGuiEntityPropertiesFields {
        public ImGuiEntityPropertiesFields() {
        }

        public ImString entityCreateEntityName = new ImString();
        public ImString entitySearchBarContents = new ImString();
        public ImString scriptPath = new ImString();
        public ImString componentFilterSearchBarContents = new ImString();

        public int currentPointLightIndex = -1;
    }

    ImGuiEntityPropertiesFields entityPropertiesFields = new ImGuiEntityPropertiesFields();

    private void displayEntityPropertyList() {
        ImGui.begin("Entity Properties");

        if (ImGui.imageButton(EditorIcons.addTexture.getNativeHandle(), (float) ImGui.getFont().getFontSize(), (float) ImGui.getFontSize())) {
            if (currentSelectedEntity != null) {
                ImGui.openPopup("Create New Entity");
            }
        }

        if (ImGui.beginPopup("Create New Entity", ImGuiWindowFlags.AlwaysAutoResize)) {
            if (ImGui.selectable("Transform") && !currentSelectedEntity.hasComponent(TransformComponent.class)) {
                currentSelectedEntity.addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, 0.0f)));
            }

            if (ImGui.selectable("Script Component") && !currentSelectedEntity.hasComponent(EntityScriptComponent.class)) {
                currentSelectedEntity.addComponent(new EntityScriptComponent("<replace>"));
            }

            if (ImGui.selectable("Point Light Array") && !currentSelectedEntity.hasComponent(PointLightsComponent.class)) {
                PointLightsComponent.PointLightData[] pointLights = new PointLightsComponent.PointLightData[1];
                pointLights[0] = new PointLightsComponent.PointLightData(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
                currentSelectedEntity.addComponent(new PointLightsComponent(pointLights));
            }

            ImGui.endPopup();
        }

        ImGui.sameLine();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.inputText("##ComponentSearch", entityPropertiesFields.componentFilterSearchBarContents, ImGuiInputTextFlags.CallbackResize);
        ImGui.separator();

        if (currentSelectedEntity != null) {
            if ("script component".contains(entityPropertiesFields.componentFilterSearchBarContents.get().toLowerCase()) &&
                currentSelectedEntity.hasComponent(EntityScriptComponent.class)) {
                if (ImGui.collapsingHeader("Script Component")) {
                    EntityScriptComponent scriptComponent = currentSelectedEntity.getComponent(EntityScriptComponent.class);

                    ImGui.indent();
                    ImGui.text(scriptComponent.classname);
                    ImGui.sameLine();

                    if (ImGui.button("edit")) {
                        ImGui.openPopup("Edit Script Path");
                        entityPropertiesFields.scriptPath.set(scriptComponent.classname);
                    }
                    ImGui.unindent();

                    if (displayTextEditDialogueModal(
                        entityPropertiesFields.scriptPath,
                        "Edit Script Path", "Script Path"
                    )) {
                        scriptComponent.classname = entityPropertiesFields.scriptPath.get();
                    }

                }
            }

            if ("transform".contains(entityPropertiesFields.componentFilterSearchBarContents.get().toLowerCase()) &&
                currentSelectedEntity.hasComponent(TransformComponent.class)) {
                TransformComponent component = currentSelectedEntity.getComponent(TransformComponent.class);

                if (ImGui.collapsingHeader("Transform")) {
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

            if ("point lights".contains(entityPropertiesFields.componentFilterSearchBarContents.get().toLowerCase()) &&
                currentSelectedEntity.hasComponent(PointLightsComponent.class)) {

                if (ImGui.collapsingHeader("Point Lights")) {
                    ImGui.indent();
                    PointLightsComponent component = currentSelectedEntity.getComponent(PointLightsComponent.class);

                    if (ImGui.beginCombo("##Point Light Combo", entityPropertiesFields.currentPointLightIndex == -1 ? "No Point Light" : "Point Light " + entityPropertiesFields.currentPointLightIndex)) {
                        for (int j = 0; j < component.pointLightData.length; j++) {
                            if (ImGui.selectable("Point Light " + j)) {
                                entityPropertiesFields.currentPointLightIndex = j;
                            }
                        }

                        if(ImGui.selectable("Create Point Light")) {
                            PointLightsComponent.PointLightData[] newData
                                = new PointLightsComponent.PointLightData[component.pointLightData.length + 1];

                            newData[component.pointLightData.length] =
                                new PointLightsComponent.PointLightData(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);

                            System.arraycopy(component.pointLightData, 0, newData, 0, component.pointLightData.length);

                            component.pointLightData = newData;
                        }

                        ImGui.endCombo();
                    }
                    int i = entityPropertiesFields.currentPointLightIndex;

                    ImGui.spacing();

                    if (i >= 0) {
                        float[] x = new float[]{component.pointLightData[i].position.x};
                        float[] y = new float[]{component.pointLightData[i].position.y};
                        float[] z = new float[]{component.pointLightData[i].position.z};

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

                        ImGui.spacing();


                        component.pointLightData[i].position.x = x[0];
                        component.pointLightData[i].position.y = y[0];
                        component.pointLightData[i].position.z = z[0];


                        float[] color = new float[3];
                        color[0] = component.pointLightData[i].color.x;
                        color[1] = component.pointLightData[i].color.y;
                        color[2] = component.pointLightData[i].color.z;

                        ImGui.text("Color");
                        ImGui.sameLine();
                        ImGui.colorEdit3("##ColorPicker", color, ImGuiColorEditFlags.Float);

                        component.pointLightData[i].color.x = color[0];
                        component.pointLightData[i].color.y = color[1];
                        component.pointLightData[i].color.z = color[2];

                        float[] intensity = new float[1];
                        intensity[0] = component.pointLightData[i].intensity;

                        ImGui.spacing();

                        ImGui.text("Intensity");
                        ImGui.sameLine();
                        ImGui.dragFloat("##Intensity", intensity);

                        component.pointLightData[i].intensity = intensity[0];

                        ImGui.separator();
                    }
                }

                ImGui.unindent();
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

        if (ImGui.beginPopupModal(windowTitle, windowFlags)) {
            ImGui.inputText(fieldName, field, ImGuiInputTextFlags.CallbackResize);

            if (ImGui.button("save")) {
                ImGui.closeCurrentPopup();
                userShouldUpdate = true;
            }

            ImGui.sameLine();

            if (ImGui.button("cancel")) {
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
        int windowWidth = (int) regionAvail.x;
        int windowHeight = (int) regionAvail.y;

        if (sceneFramebuffer == null) {
            sceneFramebuffer = new FrameBuffer(windowWidth == 0 ? 1 : windowWidth, windowHeight == 0 ? 1 : windowHeight);
        } else if (sceneFramebuffer.getWidth() != windowWidth || sceneFramebuffer.getHeight() != windowHeight) {
            sceneFramebuffer.resize(windowWidth, windowHeight);
        }

        cameraController.updateProjection(regionAvail.x / regionAvail.y, (float) Math.PI / 4.0f);
        GraphicsAPI.updateViewport((int) regionAvail.x, (int) regionAvail.y);
        sceneFramebuffer.bindAsDrawAttachment();
    }

    private void updateSceneLights() {

    }

    private void endSceneRendering() {
        FrameBuffer.bindDefaultDrawAttachment();

        ImGui.image(sceneFramebuffer.getColorAttachmentID(), sceneFramebuffer.getWidth(), sceneFramebuffer.getHeight(), 0.0f, 1.0f, 1.0f, 0.0f);
        ImGui.end();

        renderer.getLightManager().clearLights();
    }

    boolean focusedOnScene = false;

    private void updateInput() {
        if (mainWindow.keyPressed(Window.KEY_2)) {
            mainWindow.captureCursor(true);
            mainWindow.setMousePosition(mainWindow.getScreenCenter());
            focusedOnScene = true;
        } else if (mainWindow.keyPressed(Window.KEY_1)) {
            mainWindow.captureCursor(false);
            focusedOnScene = false;
        }
        if (focusedOnScene) {
            cameraController.update(mainWindow);
            mainWindow.setMousePosition(mainWindow.getScreenCenter());
        }
    }

    public void shutdown() {
        if (sceneFramebuffer != null) {
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

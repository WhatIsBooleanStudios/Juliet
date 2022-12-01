package mclone;

import imgui.ImGui;
import mclone.ECS.Entity;
import mclone.ECS.EntityScriptComponent;
import mclone.ECS.NameComponent;
import mclone.ECS.Scene;
import mclone.Editor.Editor;
import mclone.GFX.Renderer.*;
import mclone.GFX.OpenGL.*;
import mclone.Platform.TimeStep;
import mclone.Platform.Window;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import mclone.Logging.Logger;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class App {

    public void run(boolean isEditor) {
        Logger.initialize(true, "log.txt");
        Window.initializeWindowSystem();

        if(isEditor) {
            Editor editor = new Editor();
            editor.init();

            TimeStep timeStep = new TimeStep();
            while(!editor.shouldExit()) {
                float delta = timeStep.updateAndCalculateDelta();
                editor.update(delta);
            }

            editor.shutdown();
        }

        GraphicsAPI.shutdown();
        Window.shutdownWindowSystem();
        Logger.shutdown();
    }

    @Override
    public String toString() {
        return "mclone.App";
    }

    public static void main(String[] args) {
        new App().run(true);    }

}

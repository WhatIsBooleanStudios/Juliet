package mclone.GFX.Renderer;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.internal.ImGuiContext;
import mclone.Platform.Window;

import static org.lwjgl.glfw.GLFW.*;

/**
 * This class is a straightforward port of the
 * <a href="https://raw.githubusercontent.com/ocornut/imgui/256594575d95d56dda616c544c509740e74906b4/backends/imgui_impl_glfw.cpp">imgui_impl_glfw.cpp</a>.
 * <p>
 * It supports clipboard, gamepad, mouse and keyboard in the same way the original Dear ImGui code does. You can copy-paste this class in your codebase and
 * modify the rendering routine in the way you'd like.
 */
public class GUIManager {
    protected GUIManager(Window window) {
        this.window = window.getNativeHandle();
    }

    protected void init() {
        ctx = ImGui.createContext();

        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable | ImGuiConfigFlags.DockingEnable);
        io.setConfigViewportsNoTaskBarIcon(true);
        io.setConfigMacOSXBehaviors(true);

        glfwImpl.init(window, true);
        gl3Impl.init("#version 330");
    }

    protected void newFrame() {
        glfwImpl.newFrame();
        ImGui.newFrame();
    }

    protected void endFrame() {
        ImGui.render();
        gl3Impl.renderDrawData(ImGui.getDrawData());

        long context = glfwGetCurrentContext();
        ImGui.updatePlatformWindows();
        ImGui.renderPlatformWindowsDefault();
        glfwMakeContextCurrent(context);
    }

    protected void shutdown() {
        gl3Impl.dispose();
        glfwImpl.dispose();
        ImGui.destroyContext(ctx);
    }

    public void setInputApplies(boolean applies) {
        glfwImpl.setShouldApplyInput(applies);
    }

    ImGuiContext ctx;
    ImGuiImplGlfw glfwImpl = new ImGuiImplGlfw();
    ImGuiImplGl3 gl3Impl = new ImGuiImplGl3();
    final long window;
}

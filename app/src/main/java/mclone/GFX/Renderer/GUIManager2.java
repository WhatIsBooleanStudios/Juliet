package mclone.GFX.Renderer;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiPlatformIO;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.callback.ImPlatformFuncViewport;
import imgui.callback.ImPlatformFuncViewportFloat;
import imgui.callback.ImPlatformFuncViewportImVec2;
import imgui.callback.ImPlatformFuncViewportString;
import imgui.callback.ImPlatformFuncViewportSuppBoolean;
import imgui.callback.ImPlatformFuncViewportSuppImVec2;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiMouseCursor;
import imgui.flag.ImGuiNavInput;
import imgui.flag.ImGuiViewportFlags;
import imgui.gl3.ImGuiImplGl3;
import mclone.GFX.Renderer.ImGuiImplGlfw;
import imgui.internal.ImGuiContext;
import mclone.Platform.Window;
import org.lwjgl.PointerBuffer;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * This class is a straightforward port of the
 * <a href="https://raw.githubusercontent.com/ocornut/imgui/256594575d95d56dda616c544c509740e74906b4/backends/imgui_impl_glfw.cpp">imgui_impl_glfw.cpp</a>.
 * <p>
 * It supports clipboard, gamepad, mouse and keyboard in the same way the original Dear ImGui code does. You can copy-paste this class in your codebase and
 * modify the rendering routine in the way you'd like.
 */
public class GUIManager2 {
    public GUIManager2(Window window) {
        this.window = window.getNativeHandle();
    }

    public void init() {
        ctx = ImGui.createContext();

        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable | ImGuiConfigFlags.DockingEnable);
        io.setConfigViewportsNoTaskBarIcon(true);
        io.setConfigMacOSXBehaviors(true);

        glfwImpl.init(window, true);
        gl3Impl.init("#version 330");
    }

    public void newFrame() {
        glfwImpl.newFrame();
        ImGui.newFrame();
    }

    public void endFrame() {
        ImGui.render();
        gl3Impl.renderDrawData(ImGui.getDrawData());

        long context = glfwGetCurrentContext();
        ImGui.updatePlatformWindows();
        ImGui.renderPlatformWindowsDefault();
        glfwMakeContextCurrent(context);
    }

    public void shutdown() {
        gl3Impl.dispose();
        glfwImpl.dispose();
        ImGui.destroyContext(ctx);
    }

    ImGuiContext ctx;
    ImGuiImplGlfw glfwImpl = new ImGuiImplGlfw();
    ImGuiImplGl3 gl3Impl = new ImGuiImplGl3();
    final long window;
}

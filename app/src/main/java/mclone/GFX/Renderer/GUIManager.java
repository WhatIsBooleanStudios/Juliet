package mclone.GFX.Renderer;

import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.internal.ImGuiContext;
import mclone.Platform.Filesystem;
import mclone.Platform.Window;

import static org.lwjgl.glfw.GLFW.*;

/**
 * This class is a straightforward port of the
 * <a href="https://raw.githubusercontent.com/ocornut/imgui/256594575d95d56dda616c544c509740e74906b4/backends/imgui_impl_glfw.cpp">imgui_impl_glfw.cpp</a>.
 * <p>
 * It supports clipboard, gamepad, mouse and keyboard in the same way the original Dear ImGui code does. You can copy-paste this class in your codebase and
 * modify the rendering routine in the way you'd like.
 */

class _ImColor {
        public _ImColor(float R, float G, float B) {
                r = R;
                g = G;
                b = B;
        }
        public _ImColor(float R, float G, float B, float A) {
                r = R;
                g = G;
                b = B;
                a = A;
        }
        public _ImColor(ImVec4 color) {
                r = color.x * 255.0f;
                g = color.y * 255.0f;
                b = color.z * 255.0f;
                a = color.w * 255.0f;
        }
        public ImVec4 getColor() {
                return new ImVec4(r, g, b, a);
        }
        public float getRed() {
                return r;
        }
        public float getGreen() {
                return g;
        }
        public float getBlue() {
                return b;
        }
        public float getAlpha() {
                return a;
        }

        private float r, g, b, a = 255;// these values will be in RGB
}

public class GUIManager {
    protected GUIManager(Window window) {
        this.window = window.getNativeHandle();
    }
    
    private void setImGuiStyleColor(final int colorName, final _ImColor colorValue) {
        ImGui.getStyle().setColor(colorName,
        (float)colorValue.getRed() / 255.0f,
        (float)colorValue.getGreen() / 255.0f,
        (float)colorValue.getBlue()/255.0f,
        (float)colorValue.getAlpha()/255.0f);
    }

    protected void init() {
        ctx = ImGui.createContext();

        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable | ImGuiConfigFlags.DockingEnable);
        io.setConfigViewportsNoTaskBarIcon(true);
        io.setConfigMacOSXBehaviors(true);

        setImGuiStyleColor(ImGuiCol.Text, new _ImColor(255, 255, 255));
        setImGuiStyleColor(ImGuiCol.TextDisabled, new _ImColor(151, 151, 151));
        setImGuiStyleColor(ImGuiCol.TextSelectedBg, new _ImColor(0, 119, 200));
        setImGuiStyleColor(ImGuiCol.WindowBg, new _ImColor(37, 37, 38));
        setImGuiStyleColor(ImGuiCol.ChildBg, new _ImColor(37, 37, 38));
        setImGuiStyleColor(ImGuiCol.PopupBg, new _ImColor(37, 37, 38));
        setImGuiStyleColor(ImGuiCol.Border, new _ImColor(78,78,78));
        setImGuiStyleColor(ImGuiCol.BorderShadow, new _ImColor(78,78,78));
        setImGuiStyleColor(ImGuiCol.FrameBg, new _ImColor(51, 51, 55));
        setImGuiStyleColor(ImGuiCol.FrameBgHovered, new _ImColor(29, 151, 236));
        setImGuiStyleColor(ImGuiCol.FrameBgActive, new _ImColor(0,119,200));
        setImGuiStyleColor(ImGuiCol.TitleBg, new _ImColor(37,37,38));
        setImGuiStyleColor(ImGuiCol.TitleBgActive, new _ImColor(37,37,38));
        setImGuiStyleColor(ImGuiCol.TitleBgCollapsed, new _ImColor(37,37,38));
        setImGuiStyleColor(ImGuiCol.MenuBarBg, new _ImColor(51,51,55));
        setImGuiStyleColor(ImGuiCol.ScrollbarBg, new _ImColor(51,51,55));
        setImGuiStyleColor(ImGuiCol.ScrollbarGrab, new _ImColor(82,82,85));
        setImGuiStyleColor(ImGuiCol.ScrollbarGrabHovered, new _ImColor(90,90,95));
        setImGuiStyleColor(ImGuiCol.ScrollbarGrabActive, new _ImColor(90,90,95));
        setImGuiStyleColor(ImGuiCol.CheckMark, new _ImColor(0, 119, 200));
        setImGuiStyleColor(ImGuiCol.SliderGrab, new _ImColor(29, 151, 236));
        setImGuiStyleColor(ImGuiCol.SliderGrabActive, new _ImColor(0,119,200));
        setImGuiStyleColor(ImGuiCol.Button, new _ImColor(51, 51, 55));
        setImGuiStyleColor(ImGuiCol.ButtonHovered, new _ImColor(29, 151, 236));
        setImGuiStyleColor(ImGuiCol.ButtonActive, new _ImColor(29, 151, 236));
        setImGuiStyleColor(ImGuiCol.Header, new _ImColor(51, 51,55));
        setImGuiStyleColor(ImGuiCol.HeaderHovered, new _ImColor(29,151,236));
        setImGuiStyleColor(ImGuiCol.HeaderActive, new _ImColor(0,119,200));
        setImGuiStyleColor(ImGuiCol.Separator, new _ImColor(78,78,78));
        setImGuiStyleColor(ImGuiCol.SeparatorHovered, new _ImColor(78,78,78));
        setImGuiStyleColor(ImGuiCol.SeparatorActive, new _ImColor(78,78,78));
        setImGuiStyleColor(ImGuiCol.ResizeGrip, new _ImColor(37, 37, 38));
        setImGuiStyleColor(ImGuiCol.ResizeGripActive, new _ImColor(82,82,85));
        setImGuiStyleColor(ImGuiCol.ResizeGripHovered, new _ImColor(51, 51, 55));
        setImGuiStyleColor(ImGuiCol.PlotLines, new _ImColor(0,119,200));
        setImGuiStyleColor(ImGuiCol.PlotLinesHovered, new _ImColor(29,151,236));
        setImGuiStyleColor(ImGuiCol.PlotHistogram, new _ImColor(0,119,200));
        setImGuiStyleColor(ImGuiCol.PlotHistogramHovered, new _ImColor(29,151,236));
        setImGuiStyleColor(ImGuiCol.ModalWindowDimBg, new _ImColor(37, 37, 37));
        setImGuiStyleColor(ImGuiCol.DragDropTarget, new _ImColor(37, 37, 37));
        setImGuiStyleColor(ImGuiCol.NavHighlight, new _ImColor(37, 37, 37));
        setImGuiStyleColor(ImGuiCol.DockingPreview, new _ImColor(0,119,200));
        setImGuiStyleColor(ImGuiCol.Tab, new _ImColor(37, 37, 37));
        setImGuiStyleColor(ImGuiCol.TabActive, new _ImColor(0,119,200));
        setImGuiStyleColor(ImGuiCol.TabHovered, new _ImColor(37,37,37));
        setImGuiStyleColor(ImGuiCol.TabUnfocused, new _ImColor(0,119,200));
        setImGuiStyleColor(ImGuiCol.TabUnfocusedActive, new _ImColor(29, 151, 236));

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

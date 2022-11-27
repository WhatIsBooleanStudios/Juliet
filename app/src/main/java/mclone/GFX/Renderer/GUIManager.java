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
public class GUIManager {
    protected GUIManager(Window window) {
        this.window = window.getNativeHandle();
    }

    private float[] ColorFromBytes(float r, float g, float b) {
        return new float[]{r / 255.0f, g / 255.0f, b / 255.0f, 1.0f};
    }

    protected void init() {
        ctx = ImGui.createContext();

        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable | ImGuiConfigFlags.DockingEnable);
        io.setConfigViewportsNoTaskBarIcon(true);
        io.setConfigMacOSXBehaviors(true);

        ImGuiStyle style = ImGui.getStyle();

        final float[] bgColor           = ColorFromBytes(37, 37, 38);
        final float[] lightBgColor      = ColorFromBytes(82, 82, 85);
        final float[] veryLightBgColor  = ColorFromBytes(90, 90, 95);

        final float[] panelColor        = ColorFromBytes(51, 51, 55);
        final float[] panelHoverColor   = ColorFromBytes(29, 151, 236);
        final float[] panelActiveColor  = ColorFromBytes(0, 119, 200);

        final float[] textColor         = ColorFromBytes(255, 255, 255);
        final float[] textDisabledColor = ColorFromBytes(151, 151, 151);
        final float[] borderColor       = ColorFromBytes(78, 78, 78);

//        colors[ImGuiCol_Text]                 = textColor;
        style.setColor(ImGuiCol.Text, textColor[0], textColor[1], textColor[2], textColor[3]);

//        colors[ImGuiCol_TextDisabled]         = textDisabledColor;
        style.setColor(ImGuiCol.TextDisabled, textDisabledColor[0], textDisabledColor[1], textDisabledColor[2], textDisabledColor[3]);

//        colors[ImGuiCol_TextSelectedBg]       = panelActiveColor;
        style.setColor(ImGuiCol.TextSelectedBg, panelActiveColor[0], panelActiveColor[1], panelActiveColor[2], panelActiveColor[3]);

//        colors[ImGuiCol_WindowBg]             = bgColor;
        style.setColor(ImGuiCol.WindowBg, bgColor[0], bgColor[1], bgColor[2], bgColor[3]);

//        colors[ImGuiCol_ChildBg]              = bgColor;
        style.setColor(ImGuiCol.ChildBg, bgColor[0], bgColor[1], bgColor[2], bgColor[3]);

//        colors[ImGuiCol_PopupBg]              = bgColor;
        style.setColor(ImGuiCol.PopupBg, bgColor[0], bgColor[1], bgColor[2], bgColor[3]);

//        colors[ImGuiCol_Border]               = borderColor;
        style.setColor(ImGuiCol.Border, borderColor[0], borderColor[1], borderColor[2], borderColor[3]);

//        colors[ImGuiCol_BorderShadow]         = borderColor;
        style.setColor(ImGuiCol.BorderShadow, borderColor[0], borderColor[1], borderColor[2], borderColor[3]);

//        colors[ImGuiCol_FrameBg]              = panelColor;
        style.setColor(ImGuiCol.FrameBg, panelColor[0], panelColor[1], panelColor[2], panelColor[3]);

//        colors[ImGuiCol_FrameBgHovered]       = panelHoverColor;
        style.setColor(ImGuiCol.FrameBgHovered, panelHoverColor[0], panelHoverColor[1], panelHoverColor[2], panelHoverColor[3]);

//        colors[ImGuiCol_FrameBgActive]        = panelActiveColor;
        style.setColor(ImGuiCol.FrameBgActive, panelActiveColor[0], panelActiveColor[1], panelActiveColor[2], panelActiveColor[3]);

//        colors[ImGuiCol_TitleBg]              = bgColor;
        style.setColor(ImGuiCol.TitleBg, bgColor[0], bgColor[1], bgColor[2], bgColor[3]);

//        colors[ImGuiCol_TitleBgActive]        = bgColor;
        style.setColor(ImGuiCol.TitleBgActive, bgColor[0], bgColor[1], bgColor[2], bgColor[3]);

//        colors[ImGuiCol_TitleBgCollapsed]     = bgColor;
        style.setColor(ImGuiCol.TitleBgCollapsed, bgColor[0], bgColor[1], bgColor[2], bgColor[3]);

//        colors[ImGuiCol_MenuBarBg]            = panelColor;
        style.setColor(ImGuiCol.MenuBarBg, panelColor[0], panelColor[1], panelColor[2], panelColor[3]);

//        colors[ImGuiCol_ScrollbarBg]          = panelColor;
        style.setColor(ImGuiCol.MenuBarBg, panelColor[0], panelColor[1], panelColor[2], panelColor[3]);

//        colors[ImGuiCol_ScrollbarGrab]        = lightBgColor;
        style.setColor(ImGuiCol.ScrollbarGrab, lightBgColor[0], lightBgColor[1], lightBgColor[2], lightBgColor[3]);

//        colors[ImGuiCol_ScrollbarGrabHovered] = veryLightBgColor;
        style.setColor(ImGuiCol.ScrollbarGrabHovered, veryLightBgColor[0], veryLightBgColor[1], veryLightBgColor[2], veryLightBgColor[3]);

//        colors[ImGuiCol_ScrollbarGrabActive]  = veryLightBgColor;
        style.setColor(ImGuiCol.ScrollbarGrabActive, veryLightBgColor[0], veryLightBgColor[1], veryLightBgColor[2], veryLightBgColor[3]);

//        colors[ImGuiCol_CheckMark]            = panelActiveColor;
        style.setColor(ImGuiCol.CheckMark, panelActiveColor[0], panelActiveColor[1], panelActiveColor[2], panelActiveColor[3]);

//        colors[ImGuiCol_SliderGrab]           = panelHoverColor;
        style.setColor(ImGuiCol.SliderGrab, panelHoverColor[0], panelHoverColor[1], panelHoverColor[2], panelHoverColor[3]);

//        colors[ImGuiCol_SliderGrabActive]     = panelActiveColor;
        style.setColor(ImGuiCol.SliderGrabActive, panelActiveColor[0], panelActiveColor[1], panelActiveColor[2], panelActiveColor[3]);

//        colors[ImGuiCol_Button]               = panelColor;
        style.setColor(ImGuiCol.Button, panelColor[0], panelColor[1], panelColor[2], panelColor[3]);

//        colors[ImGuiCol_ButtonHovered]        = panelHoverColor;
        style.setColor(ImGuiCol.ButtonHovered, panelHoverColor[0], panelHoverColor[1], panelHoverColor[2], panelHoverColor[3]);

//        colors[ImGuiCol_ButtonActive]         = panelHoverColor;
        style.setColor(ImGuiCol.ButtonActive, panelHoverColor[0], panelHoverColor[1], panelHoverColor[2], panelHoverColor[3]);

//        colors[ImGuiCol_Header]               = panelColor;
        style.setColor(ImGuiCol.Header, panelColor[0], panelColor[1], panelColor[2], panelColor[3]);

//        colors[ImGuiCol_HeaderHovered]        = panelHoverColor;
        style.setColor(ImGuiCol.HeaderHovered, panelHoverColor[0], panelHoverColor[1], panelHoverColor[2], panelHoverColor[3]);

//        colors[ImGuiCol_HeaderActive]         = panelActiveColor;
        style.setColor(ImGuiCol.HeaderActive, panelActiveColor[0], panelActiveColor[1], panelActiveColor[2], panelActiveColor[3]);

//        colors[ImGuiCol_Separator]            = borderColor;
        style.setColor(ImGuiCol.Separator, borderColor[0], borderColor[1], borderColor[2], borderColor[3]);

//        colors[ImGuiCol_SeparatorHovered]     = borderColor;
        style.setColor(ImGuiCol.SeparatorHovered, borderColor[0], borderColor[1], borderColor[2], borderColor[3]);

//        colors[ImGuiCol_SeparatorActive]      = borderColor;
        style.setColor(ImGuiCol.SeparatorActive, borderColor[0], borderColor[1], borderColor[2], borderColor[3]);

//        colors[ImGuiCol_ResizeGrip]           = bgColor;
        style.setColor(ImGuiCol.ResizeGrip, bgColor[0], bgColor[1], bgColor[2], bgColor[3]);

//        colors[ImGuiCol_ResizeGripHovered]    = panelColor;
        style.setColor(ImGuiCol.ResizeGripHovered, panelColor[0], panelColor[1], panelColor[2], panelColor[3]);

//        colors[ImGuiCol_ResizeGripActive]     = lightBgColor;
        style.setColor(ImGuiCol.ResizeGripActive, lightBgColor[0], lightBgColor[1], lightBgColor[2], lightBgColor[3]);

//        colors[ImGuiCol_PlotLines]            = panelActiveColor;
        style.setColor(ImGuiCol.PlotLines, panelActiveColor[0], panelActiveColor[1], panelActiveColor[2], panelActiveColor[3]);

//        colors[ImGuiCol_PlotLinesHovered]     = panelHoverColor;
        style.setColor(ImGuiCol.PlotLinesHovered, panelHoverColor[0], panelHoverColor[1], panelHoverColor[2], panelHoverColor[3]);

//        colors[ImGuiCol_PlotHistogram]        = panelActiveColor;
        style.setColor(ImGuiCol.PlotHistogram, panelActiveColor[0], panelActiveColor[1], panelActiveColor[2], panelActiveColor[3]);

//        colors[ImGuiCol_PlotHistogramHovered] = panelHoverColor;
        style.setColor(ImGuiCol.PlotHistogramHovered, panelHoverColor[0], panelHoverColor[1], panelHoverColor[2], panelHoverColor[3]);

//        colors[ImGuiCol_ModalWindowDarkening] = bgColor;
        style.setColor(ImGuiCol.ModalWindowDimBg, bgColor[0], bgColor[1], bgColor[2], bgColor[3]);

//        colors[ImGuiCol_DragDropTarget]       = bgColor;
        style.setColor(ImGuiCol.DragDropTarget, bgColor[0], bgColor[1], bgColor[2], bgColor[3]);

//        colors[ImGuiCol_NavHighlight]         = bgColor;
        style.setColor(ImGuiCol.NavHighlight, bgColor[0], bgColor[1], bgColor[2], bgColor[3]);

//        colors[ImGuiCol_DockingPreview]       = panelActiveColor;
        style.setColor(ImGuiCol.DockingPreview, panelActiveColor[0], panelActiveColor[1], panelActiveColor[2], panelActiveColor[3]);

//        colors[ImGuiCol_Tab]                  = bgColor;
        style.setColor(ImGuiCol.Tab, bgColor[0], bgColor[1], bgColor[2], bgColor[3]);

//        colors[ImGuiCol_TabActive]            = panelActiveColor;
        style.setColor(ImGuiCol.TabActive, panelActiveColor[0], panelActiveColor[1], panelActiveColor[2], panelActiveColor[3]);

//        colors[ImGuiCol_TabUnfocused]         = bgColor;
        style.setColor(ImGuiCol.TabUnfocused, bgColor[0], bgColor[1], bgColor[2], bgColor[3]);

//        colors[ImGuiCol_TabUnfocusedActive]   = panelActiveColor;
        style.setColor(ImGuiCol.TabUnfocusedActive, panelActiveColor[0], panelActiveColor[1], panelActiveColor[2], panelActiveColor[3]);

//        colors[ImGuiCol_TabHovered]           = panelHoverColor;
        style.setColor(ImGuiCol.TabHovered, panelHoverColor[0], panelHoverColor[1], panelHoverColor[2], panelHoverColor[3]);
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

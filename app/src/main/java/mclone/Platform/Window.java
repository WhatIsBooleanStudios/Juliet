package mclone.Platform;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.opengl.GL33.*;

import mclone.Logging.Logger;
import org.lwjgl.glfw.*;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.DoubleBuffer;

public class Window {
    public Window(String title, int width, int height, boolean fullscreen) {
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_DEBUG, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        windowHandle = glfwCreateWindow(width, height, title, fullscreen ? glfwGetPrimaryMonitor() : 0, 0);
        if (windowHandle == 0) {
            System.out.println("Failed to create window \"" + title + "\"!");
        }
    }

    public static void initializeWindowSystem() {
        Logger.info("Window.initializeWindowSystem", "initializing window system!");
        if (!glfwInit())
            Logger.error("Window.initializeWindowSystem", "Failed to initialize window system!");

        GLFWErrorCallback cb = new GLFWErrorCallback() {
            @Override
            public void invoke(int error, long description) {
                Logger.error("GLFWErrorCallback.invoke", this, "error #" + error + ": " + memUTF8(description));
            }
        };
        glfwSetErrorCallback(cb);
    }

    public static void shutdownWindowSystem() {
        GLFWErrorCallback cb = glfwSetErrorCallback(null);
        if(cb != null) cb.free();
        glfwTerminate();
    }

    public static void windowSystemPollEvents() {
        glfwPollEvents();
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(windowHandle, title);
    }

    public void swapBuffers() {
        glfwSwapBuffers(windowHandle);
    }

    public void makeContextCurrent() {
        glfwMakeContextCurrent(windowHandle);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public int getWidth() {
        int[] width = {0};
        int[] height = {0};
        glfwGetWindowSize(windowHandle, width, height);
        return width[0];
    }

    public int getHeight() {
        int[] width = {0};
        int[] height = {0};
        glfwGetWindowSize(windowHandle, width, height);
        return height[0];
    }


    public boolean keyPressed(int key) {
        int status = glfwGetKey(windowHandle, key);
        return status == GLFW_PRESS || status == GLFW_REPEAT;
    }

    public boolean keyReleased(int key) {
        int status = glfwGetKey(windowHandle, key);
        return status == GLFW_RELEASE;
    }

    public boolean keyJustPressed(int key) {
        int status = glfwGetKey(windowHandle, key);
        return status == GLFW_PRESS;
    }

    public boolean mouseButtonPressed(int button) {
        int status = glfwGetMouseButton(windowHandle, button);
        return status == GLFW_PRESS || status == GLFW_REPEAT;
    }

    public boolean mouseButtonJustPressed(int button) {
        int status = glfwGetMouseButton(windowHandle, button);
        return status == GLFW_PRESS;
    }

    public boolean mouseButtonReleased(int button) {
        int status = glfwGetMouseButton(windowHandle, button);
        return status == GLFW_RELEASE;
    }

    public Vector2f getMousePosition() {
        Vector2f mousePosition = new Vector2f();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer posX = stack.mallocDouble(1);
            DoubleBuffer posY = stack.mallocDouble(1);

            glfwGetCursorPos(windowHandle, posX, posY);
            mousePosition.set(posX.get(), posY.get());
        }

        return mousePosition;
    }

    public void dispose() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
    }

    private long windowHandle;
}

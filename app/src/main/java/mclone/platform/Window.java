package mclone.platform;

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
    public static void initializeWindowSystem() {
        if (!glfwInit())
            Logger.get().error("Failed to initialize window system!");

        GLFWErrorCallback cb = new GLFWErrorCallback() {
            @Override
            public void invoke(int error, long description) {
                Logger.get().error(this, "GLFW ERROR " + error + ": " + memUTF8(description));
            }
        };
        glfwSetErrorCallback(cb);
    }

    public static void shutdownWindowSystem() {
        glfwTerminate();
    }

    public static void windowSystemPollEvents() {
        glfwPollEvents();
    }

    public Window(String title, int width, int height, boolean fullscreen) {
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        m_window = glfwCreateWindow(width, height, title, fullscreen ? glfwGetPrimaryMonitor() : 0, 0);
        if (m_window == 0) {
            System.out.println("Failed to create window \"" + title + "\"!");
        }
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(m_window, title);
    }

    public void swapBuffers() {
        glfwSwapBuffers(m_window);
    }

    public void makeContextCurrent() {
        glfwMakeContextCurrent(m_window);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(m_window);
    }

    public boolean keyPressed(int key) {
        int status = glfwGetKey(m_window, key);
        return status == GLFW_PRESS || status == GLFW_REPEAT;
    }

    public boolean keyReleased(int key) {
        int status = glfwGetKey(m_window, key);
        return status == GLFW_RELEASE;
    }

    public boolean keyJustPressed(int key) {
        int status = glfwGetKey(m_window, key);
        return status == GLFW_PRESS;
    }

    public boolean mouseButtonPressed(int button) {
        int status = glfwGetMouseButton(m_window, button);
        return status == GLFW_PRESS || status == GLFW_REPEAT;
    }

    public boolean mouseButtonJustPressed(int button) {
        int status = glfwGetMouseButton(m_window, button);
        return status == GLFW_PRESS;
    }

    public boolean mouseButtonReleased(int button) {
        int status = glfwGetMouseButton(m_window, button);
        return status == GLFW_RELEASE;
    }

    public Vector2f getMousePosition() {
        Vector2f mousePosition = new Vector2f();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer posX = stack.mallocDouble(1);
            DoubleBuffer posY = stack.mallocDouble(1);

            glfwGetCursorPos(m_window, posX, posY);
            mousePosition.set(posX.get(), posY.get());
        }

        return mousePosition;
    }

    public void dispose() {
        glfwFreeCallbacks(m_window);
        glfwDestroyWindow(m_window);
    }

    private long m_window;
}

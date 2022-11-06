package mclone;

import mclone.Platform.Window;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

import mclone.Logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL33.*;

public class App {
    // The window handle
    private Window m_window;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        Logger logger = Logger.get();
        logger.error(this, "This is an example error message");
        logger.warn(this, "This is an example warn message");
        logger.trace(this, "This is an example trace message");
        logger.info(this, "This is an example info message");

        init();
        loop();

        Window.shutdownWindowSystem();
    }

    private void init() {
        Window.initializeWindowSystem();
        m_window = new Window("Window!", 1024, 768, false);
        m_window.makeContextCurrent();
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color

        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!m_window.shouldClose() && !m_window.keyPressed(GLFW_KEY_ESCAPE)) {
            if (m_window.mouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
                glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
            } else if (m_window.mouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT)) {
                glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
            } else {
                glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
            }
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            m_window.setTitle("Window! Cursor pos: " + m_window.getMousePosition().get(0) + " "
                    + m_window.getMousePosition().get(1));

            // glfwSwapBuffers(window); // swap the color buffers
            m_window.swapBuffers();

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            // glfwPollEvents();
            Window.windowSystemPollEvents();
        }
    }

    public static void main(String[] args) {
        new App().run();
    }

}

package mclone.GFX.OpenGL;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;

import static org.lwjgl.opengl.GL33C.*;

import org.lwjgl.opengl.*;
import org.lwjgl.opengl.ARBDebugOutput;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GLCapabilities.*;

/**
 * An abstraction over the OpenGL API
 */
public class GraphicsAPI {

    /**
     * Initialize the wrapper
     */
    public static void initialize() {
        GL.createCapabilities();
        debugCb = GLUtil.setupDebugMessageCallback(System.out);
        setGLDebugMessageControl(GLDebugMessageSeverity.NOTIFICATION, false);
    }

    /**
     * Shutdown the wrapper
     */
    public static void shutdown() {
        if (debugCb != null) debugCb.free();
    }

    /**
     * Set the current clear color
     *
     * @param r r component of the clear
     * @param g g component of the clear
     * @param b b component of the clear
     * @param a a component of the clear
     */
    public static void setClearColor(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }

    /**
     * Clear the screen with the clearColor set in setClearColor()
     */
    public static void clear() {
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Issue a draw call
     *
     * @param shader      The shader to execute
     * @param vbo         The vertex buffer to draw
     * @param vertexCount The number of vertices to draw
     */
    public static void draw(Shader shader, VertexBuffer vbo, int vertexCount) {
        shader.bind();
        vbo.bind();
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
    }

    /**
     * Issue a draw call with an index buffer
     *
     * @param shader     The shader to execute
     * @param vbo        The vertex buffer to draw
     * @param ibo        The index buffer to use in the drawCall
     * @param indexCount The number of indices to draw
     */
    public static void drawIndexed(Shader shader, VertexBuffer vbo, IndexBuffer ibo, int indexCount) {
        shader.bind();
        vbo.bind();
        ibo.bind();
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }

    // Do not create an instance of this
    private GraphicsAPI() {
    }

    /**
     * The below source code is copied from LibGDX and used under the terms of the Apache 2.0 license
     *
     * Copyright 2011 Mario Zechner <badlogicgames@gmail.com> and Nathan Sweet <nathan.sweet@gmail.com>
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    public enum GLDebugMessageSeverity {
        HIGH(GL43.GL_DEBUG_SEVERITY_HIGH, KHRDebug.GL_DEBUG_SEVERITY_HIGH, ARBDebugOutput.GL_DEBUG_SEVERITY_HIGH_ARB,
            AMDDebugOutput.GL_DEBUG_SEVERITY_HIGH_AMD), MEDIUM(GL43.GL_DEBUG_SEVERITY_MEDIUM, KHRDebug.GL_DEBUG_SEVERITY_MEDIUM,
            ARBDebugOutput.GL_DEBUG_SEVERITY_MEDIUM_ARB, AMDDebugOutput.GL_DEBUG_SEVERITY_MEDIUM_AMD), LOW(
            GL43.GL_DEBUG_SEVERITY_LOW, KHRDebug.GL_DEBUG_SEVERITY_LOW, ARBDebugOutput.GL_DEBUG_SEVERITY_LOW_ARB,
            AMDDebugOutput.GL_DEBUG_SEVERITY_LOW_AMD), NOTIFICATION(GL43.GL_DEBUG_SEVERITY_NOTIFICATION,
            KHRDebug.GL_DEBUG_SEVERITY_NOTIFICATION, -1, -1);

        final int gl43, khr, arb, amd;

        GLDebugMessageSeverity(int gl43, int khr, int arb, int amd) {
            this.gl43 = gl43;
            this.khr = khr;
            this.arb = arb;
            this.amd = amd;
        }
    }

    public static boolean setGLDebugMessageControl(GLDebugMessageSeverity severity, boolean enabled) {
        GLCapabilities caps = GL.getCapabilities();
        final int GL_DONT_CARE = 0x1100; // not defined anywhere yet

        if (caps.OpenGL43) {
            GL43.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, severity.gl43, (IntBuffer) null, enabled);
            return true;
        }

        if (caps.GL_KHR_debug) {
            KHRDebug.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, severity.khr, (IntBuffer) null, enabled);
            return true;
        }

        if (caps.GL_ARB_debug_output && severity.arb != -1) {
            ARBDebugOutput.glDebugMessageControlARB(GL_DONT_CARE, GL_DONT_CARE, severity.arb, (IntBuffer) null, enabled);
            return true;
        }

        if (caps.GL_AMD_debug_output && severity.amd != -1) {
            AMDDebugOutput.glDebugMessageEnableAMD(GL_DONT_CARE, severity.amd, (IntBuffer) null, enabled);
            return true;
        }

        return false;
    }

    private static Callback debugCb = null;
}

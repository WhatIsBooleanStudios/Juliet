package juliet.GFX.OpenGL;

import juliet.Logging.Logger;
import org.lwjgl.system.Platform;

import static org.lwjgl.opengl.GL33C.*;

public class FrameBuffer {
    public FrameBuffer(int width, int height) {
        setupFramebuffer(width, height);

        this.width = width;
        this.height = height;
    }

    public void resize(int width, int height) {
        dispose();
        setupFramebuffer(width, height);

        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    private void createColorAttachment(int width, int height) {
        colorAttachmentID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, colorAttachmentID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void createDepthStencilRenderBuffer(int width, int height) {
        depthStencilRenderBufferID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthStencilRenderBufferID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        bind();
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, depthStencilRenderBufferID);
        unBind();
    }

    private void setupFramebuffer(int width, int height) {
        if(Platform.get() == Platform.MACOSX) {
        }

        id = glGenFramebuffers();
        createColorAttachment(width, height);
        createDepthStencilRenderBuffer(width, height);
        bind();
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorAttachmentID, 0);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            Logger.error("juliet.OpenGL.FrameBuffer" ,this,  "Framebuffer is not complete!");
        }

        unBind();
    }

    public int getColorAttachmentID() {
        return colorAttachmentID;
    }

    private void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    private void unBind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bindAsDrawAttachment() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, id);
    }

    public static void bindDefaultDrawAttachment() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    public void dispose() {
        glDeleteTextures(colorAttachmentID);
        glDeleteRenderbuffers(depthStencilRenderBufferID);
        glDeleteFramebuffers(id);
    }

    int width, height;
    int colorAttachmentID;
    int depthStencilRenderBufferID;
    int id;
}

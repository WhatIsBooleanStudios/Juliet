package mclone.gfx.OpenGL;

import mclone.Logging.Logger;

import java.nio.ByteBuffer;

import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org .lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

import static org.lwjgl.opengl.GL33C.*;

import static org.lwjgl.system.MemoryUtil.*;

/**
 * An abstraction over OpenGL Textures which are essentially GPU-images
 */
public class Texture {

    /**
     * Creates a texture from a file in the filesystem
     * @param path The path to the file
     */
    public Texture(String path) {
        this.name = path;
        int[] width = {0};
        int[] height = {0};
        int[] nrChannels = {0};
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer data = stbi_load(path, width, height, nrChannels, STBI_rgb_alpha);
        
        if(data == null || data.remaining() == 0) {
            Logger.error("Texture.new", this, "failed to load texture \"" + path + "\"");
            return;
        }

        createFromData(data, width, height);
        
        stbi_image_free(data);
    }

    /**
     * Creates a Texture from preloaded data.
     * @param name The name of the texture (user defined)
     * @param data The texture data to be stored in the object
     * @param width The width of the image (in pixels)
     * @param height The height of the image (in pixels)
     */
    public Texture(String name, ByteBuffer data, int[] width, int[] height) {
        this.name = name;
        createFromData(data, width, height);
    }

    /**
     * Bind the texture
     */
    private void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    /**
     * Bind the texture to the texture slot givenj
     * @param slot The texture slot 0 <= slot < hardwareMaximum
     */
    public void bind(int slot) {
        glActiveTexture(GL_TEXTURE0 + slot);
        bind();
    }

    /**
     * UnBinds the Texture
     */
    public void unBind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Frees the memory and objects associated with the texture
     */
    public void dispose() {
        glDeleteTextures(id);
        id = -1;
    }


    private void createFromData(ByteBuffer data, int[] width, int[] height) {
        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        
        int imageType = GL_RGBA;
        glTexImage2D(GL_TEXTURE_2D, 0, imageType, width[0], height[0], 0, imageType, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);
    }


    @Override
    public String toString() {
        return "Texture(id=" + id + " name=\"" + name + "\")";
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(id != -1) {
            Logger.warn("Texture.finalize", this, "Garbage collection called but object not freed!");
        }
    }


    private int id = -1;

    private String name;
}


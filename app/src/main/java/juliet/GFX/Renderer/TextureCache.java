package juliet.GFX.Renderer;

import juliet.GFX.OpenGL.Texture;
import juliet.Logging.Logger;

import java.util.HashMap;

public class TextureCache {
    protected TextureCache() {}

    public Texture load(String path) {
        if(cache.containsKey(path)) {
            Texture texture = cache.get(path);
            if(texture == null) {
                Logger.error("TextureCache.load", this, "The name \"" + path + "\" exists in texture cache but the corresponding texture is null!");
            }
            return texture;

        } else {
            Texture texture = new Texture(path);
            if(texture.loaded()) {
                cache.put(path, texture);
                return texture;
            } else {
                Logger.error("TextureCache.load", "Failed to create texture \"" + path + "\"");
                return null;
            }
        }
    }

    public Texture get(String path) {
        return cache.get(path);
    }

    protected void clear() {
        for(Texture texture : cache.values()) {
            if(texture != null && texture.loaded()) {
                texture.dispose();
            }
        }
        cache.clear();
    }
    private static final HashMap<String, Texture> cache = new HashMap<>();
}

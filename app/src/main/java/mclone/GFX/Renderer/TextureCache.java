package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.Texture;
import mclone.Logging.Logger;

import java.util.HashMap;

public class TextureCache {
    private TextureCache() {}

    public static Texture load(String path) {
        if(cache.containsKey(path)) {
            Texture texture = cache.get(path);
            if(texture == null) {
                Logger.error("TextureCache.load", "The name \"" + path + "\" exists in texture cache but the corresponding texture is null!");
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

    public static Texture get(String path) {
        return cache.get(path);
    }

    protected static void clear() {
        for(Texture texture : cache.values()) {
            if(texture != null && texture.loaded()) {
                texture.dispose();
            }
        }
        cache.clear();
    }

    private static final HashMap<String, Texture> cache = new HashMap<>();
}

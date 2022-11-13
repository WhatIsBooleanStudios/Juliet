package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.Texture;

public class Material {
    public Material(String name, Texture diffuse, Texture normal, Texture metallic, Texture roughness) {
        this.name = name;
        this.diffuse = diffuse;
        this.normal = normal;
        this.diffuse = diffuse;
        this.metallic = metallic;
        this.roughness = roughness;
    }

    final Texture getDiffuse() { return diffuse; }

    public String getName() {
        return name;
    }

    public final Texture getNormal() {
        return normal;
    }

    public final Texture getMetallic() {
        return metallic;
    }

    public final Texture getRoughness() {
        return roughness;
    }

    private final String name;
    private Texture diffuse;
    private Texture normal;
    private Texture metallic;
    private Texture roughness;
}

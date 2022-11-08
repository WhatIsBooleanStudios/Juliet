package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.Texture;

public class Material {
    public Material(String name, Texture diffuse) {
        this.name = name;
        this.diffuse = diffuse;
    }

    final Texture getDiffuse() { return diffuse; }

    private final String name;
    private Texture diffuse;
}

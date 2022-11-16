package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.Texture;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Material {
    public Material(String name, Texture diffuse, Vector3fc diffuseColor, float metallic, float roughness) {
        this.name = name;
        this.diffuseTexture = diffuse;
        this.diffuseColor = new Vector3f(diffuseColor);
        this.metallic = metallic;
        this.roughness = roughness;
    }

    public Material(String name, Vector3fc diffuseColor, float metallic, float roughness) {
        this.name = name;
        this.diffuseTexture = null;
        this.diffuseColor = new Vector3f(diffuseColor);
        this.metallic = metallic;
        this.roughness = roughness;
    }

    public Texture getDiffuseTexture() { return diffuseTexture; }
    public String getName() {
        return name;
    }

    public float getMetallic() {
        return metallic;
    }

    public float getRoughness() {
        return roughness;
    }

    public Vector3fc getDiffuseColor() {
        return diffuseColor;
    }

    private final String name;
    private Texture diffuseTexture = null;
    private Vector3f diffuseColor;
    private float metallic;
    private float roughness;
}

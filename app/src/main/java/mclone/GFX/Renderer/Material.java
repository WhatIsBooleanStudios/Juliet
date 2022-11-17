package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.Texture;
import mclone.Logging.Logger;
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

    @Override
    public int hashCode() {
        return ((diffuseTexture == null ? "" : diffuseTexture.getName()) + diffuseColor + "" + metallic + "" + roughness).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != this.getClass()) {
            return false;
        }

        return this.hashCode() == obj.hashCode();
    }

    @Override
    public String toString() {
        return "Material(name=\"" + getName() +
            "\", diffuseTexture=" + diffuseTexture +
            ", diffuseColor=" + diffuseColor +
            ", metallic=" + metallic +
            ", roughness=" + roughness +
            ")";
    }

    private final String name;
    private Texture diffuseTexture = null;
    private Vector3f diffuseColor;
    private float metallic;
    private float roughness;
}

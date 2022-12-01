package juliet.GFX.Renderer;

import static org.lwjgl.assimp.Assimp.*;

import juliet.GFX.OpenGL.Texture;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import juliet.Logging.Logger;

import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class Model {
    protected Model(String path, TextureCache textureCache, MaterialCache materialCache) {
        this.path = path;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIScene scene = aiImportFile(
                path,
                aiProcess_GenNormals | aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals |
                    aiProcess_RemoveRedundantMaterials
            );

            if (scene == null) {
                Logger.error("Model.new", this, "Failed to load model \"" + path + "\"");
                return;
            }

            createInternalTextures(scene);

            int numMaterials = scene.mNumMaterials();
            PointerBuffer aiMaterials = scene.mMaterials();
            for (int i = 0; i < numMaterials; i++) {
                AIMaterial material = AIMaterial.create(aiMaterials.get());
                processMaterial(material, materialCache, textureCache);
            }

            meshes = new Mesh[scene.mNumMeshes()];
            for (int i = 0; i < scene.mNumMeshes(); i++) {
                AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
                Material material = materials.get(mesh.mMaterialIndex());
                meshes[i] = new Mesh(path + "_mesh" + i, mesh, material);
            }

        }
    }

    public String getPath() {
        return path;
    }

    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return Objects.equals(path, model.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return "Model(path=\"" + path + "\", numMeshes=\"" + meshes.length + ", numMaterials=" + materials.size() + ", numInternalTextures=" + textures.size() + ")";
    }

    private void createInternalTextures(AIScene scene) {
        int numInternalTextures = scene.mNumTextures();
        if (numInternalTextures > 0) {
            for (int i = 0; i < numInternalTextures; i++) {
                AITexture aiTexture = AITexture.create(scene.mTextures().get(i));
                this.textures.add(
                    new Texture(
                        path + "_tex" + i,
                        aiTexture.pcDataCompressed(),
                        new int[]{aiTexture.mWidth()},
                        new int[]{aiTexture.mHeight()},
                        aiTexture.mHeight() == 0
                    )
                );
            }
        }
    }

    private void processMaterial(AIMaterial material, MaterialCache materialCache, TextureCache textureCache) {
        AIString name = AIString.calloc();
        Assimp.aiGetMaterialString(material, AI_MATKEY_NAME, 0, 0, name);

        Texture diffuseTexture = retrieveMaterialTexture(material, aiTextureType_DIFFUSE, textureCache);

        Vector3f diffuseColor = new Vector3f();
        try(AIColor4D color = AIColor4D.calloc()) {
            Assimp.aiGetMaterialColor(material, AI_MATKEY_COLOR_DIFFUSE, 0, 0, color);
            diffuseColor.setComponent(0, color.r());
            diffuseColor.setComponent(1, color.g());
            diffuseColor.setComponent(2, color.b());
        }

        float[] metallic = {Float.MIN_VALUE};
        Assimp.aiGetMaterialFloatArray(material, AI_MATKEY_METALLIC_FACTOR, 0, 0, metallic, new int[]{1});
        if(metallic[0] == Float.MIN_VALUE) {
            Logger.warn("Model.processMaterial", this,
                "Something went wrong with finding the metallic value of the material \"" + name.dataString() + "\". Defaulting to 1.0f");
            metallic[0] = 1.0f;
        }

        float[] roughness = {Float.MIN_VALUE};
        Assimp.aiGetMaterialFloatArray(material, AI_MATKEY_ROUGHNESS_FACTOR, 0, 0, roughness, new int[]{1});
        if(metallic[0] == Float.MIN_VALUE) {
            Logger.warn("Model.processMaterial", this,
                "Something went wrong with finding the roughness value of the material \"" + name.dataString() + "\". Defaulting to 1.0f");
            roughness[0] = 1.0f;
        }

        Material mcloneMaterial = new Material(name.dataString(), diffuseTexture, diffuseColor, metallic[0], roughness[0]);
        mcloneMaterial = materialCache.loadMaterial(mcloneMaterial);
        materials.add(mcloneMaterial);

        name.free();
    }

    private Texture retrieveMaterialTexture(AIMaterial material, int textureType, TextureCache textureCache) {
        AIString texturePath = AIString.calloc();
        aiGetMaterialTexture(material, textureType, 0, texturePath, (IntBuffer) null, null, null, null, null, null);

        Texture texture = null;

        if (texturePath.length() <= 0) {
            texturePath.free();
            return null;
        }

        if (texturePath.length() > 1 && texturePath.dataString().charAt(0) == '*') {
            try {
                int textureIndex = Integer.parseInt( texturePath.dataString().substring(1), 10);
                if (textureIndex >= this.textures.size()) {
                    Logger.error("Model.processMaterial", this, "Loaded material references nonexistent internal texture!");
                    return null;
                }
                texture = textures.get(textureIndex);
            } catch (NumberFormatException ignored) {
            }
        } else {
            texture = textureCache.load(Paths.get(path).getParent() + "/" + texturePath.dataString());
        }
        texturePath.free();

        return texture;
    }

    protected void dispose() {
        if(!disposed) {
            boolean disposed = true;
            for (Mesh mesh : meshes) {
                if (mesh != null) {
                    mesh.dispose();
                }
            }
        } else {
            Logger.warn("Model.dispose", this, "Model \"" + path + "\" is already disposed!");
        }
    }

    private boolean disposed = false;

    protected final Mesh[] getMeshes() {
        return meshes;
    }

    private Mesh[] meshes;
    private final ArrayList<Texture> textures = new ArrayList<>();
    private final ArrayList<Material> materials = new ArrayList<>();

    private final String path;
}

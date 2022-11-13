package mclone.GFX.Renderer;

import static org.lwjgl.assimp.Assimp.*;

import mclone.GFX.OpenGL.Texture;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import mclone.GFX.OpenGL.Shader;
import mclone.Logging.Logger;

import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Model {
    public Model(String path) {
        this.path = new String(path);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIScene scene = aiImportFile(
                path,
                aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals |
                    aiProcess_RemoveRedundantMaterials
            );

            if (scene == null) {
                Logger.error("Model.new", this, "Failed to load model \"" + path + "\"");
                return;
            }

            AIString sceneName = scene.mName();
            Logger.trace("Scene name: " + sceneName.dataString());

            createInternalTextures(scene, sceneName);

            int numMaterials = scene.mNumMaterials();
            Logger.trace("num materials: " + numMaterials);
            PointerBuffer aiMaterials = scene.mMaterials();
            for (int i = 0; i < numMaterials; i++) {
                AIMaterial material = AIMaterial.create(aiMaterials.get());
                processMaterial(material);
            }

            meshes = new Mesh[scene.mNumMeshes()];
            for (int i = 0; i < scene.mNumMeshes(); i++) {
                AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
                Material material = materials.get(mesh.mMaterialIndex());
                meshes[i] = new Mesh(mesh, material);
            }

        }
    }

    private void createInternalTextures(AIScene scene, AIString sceneName) {
        int numInternalTextures = scene.mNumTextures();
        Logger.trace("numInternalTextures: " + numInternalTextures);
        if (numInternalTextures > 0) {
            for (int i = 0; i < numInternalTextures; i++) {
                AITexture aiTexture = AITexture.create(scene.mTextures().get(i));
                this.textures.add(
                    new Texture(
                        sceneName.dataString() + "_tex" + i,
                        aiTexture.pcDataCompressed(),
                        new int[]{aiTexture.mWidth()},
                        new int[]{aiTexture.mHeight()},
                        aiTexture.mHeight() == 0
                    )
                );
            }
        }
    }

    private void processMaterial(AIMaterial material) {
        AIString name = AIString.calloc();
        Assimp.aiGetMaterialString(material, AI_MATKEY_NAME, 0, 0, name);

        Texture diffuseTexture = retrieveMaterialTexture(material, aiTextureType_DIFFUSE);
        if(diffuseTexture == null) {
            Logger.error("Model.processMaterial", this, "Failed to load diffuse texture!");
            return;
        }

        Texture normalTexture = retrieveMaterialTexture(material, aiTextureType_NORMALS);
        if(normalTexture == null) {
            Logger.error("Model.processMaterial", this, "Failed to load normal texture!");
            return;
        }

        Texture metallicTexture = retrieveMaterialTexture(material, aiTextureType_UNKNOWN);
        if(metallicTexture == null) {
            Logger.error("Model.processMaterial", this, "Failed to load metalness texture!");
            return;
        }

        Texture roughnessTexture = retrieveMaterialTexture(material, aiTextureType_UNKNOWN);
        if(roughnessTexture == null) {
            Logger.error("Model.processMaterial", this, "Failed to load roughness texture!");
            return;
        }

        materials.add(new Material(name.dataString(), diffuseTexture, normalTexture, metallicTexture, roughnessTexture));
//        {
//            AIString diffuseTexturePath = AIString.calloc();
//            Assimp.aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, diffuseTexturePath, (IntBuffer) null, null, null, null, null, null);
//
//            if (diffuseTexturePath.length() <= 0) {
//                Logger.error("Material has no diffuse texture!");
//                return;
//            }
//
//            if (diffuseTexturePath.length() > 1 && diffuseTexturePath.dataString().charAt(0) == '*') {
//                try {
//                    int textureIndex = Integer.parseInt(diffuseTexturePath.dataString().substring(1), 10);
//                    if (textureIndex >= this.textures.size()) {
//                        Logger.error("Model.processMaterial", this, "Loaded material references nonexistent internal texture!");
//                        return;
//                    }
//                    diffuseTexture = textures.get(textureIndex);
//                } catch (NumberFormatException ignored) {
//                }
//            } else {
//                diffuseTexture = TextureCache.load(diffuseTexturePath.dataString());
//            }
//            diffuseTexturePath.free();
//        }

        name.free();
    }

    private Texture retrieveMaterialTexture(AIMaterial material, int textureType) {
        AIString texturePath = AIString.calloc();
        aiGetMaterialTexture(material, textureType, 0, texturePath, (IntBuffer) null, null, null, null, null, null);

        Texture texture = null;

        if (texturePath.length() <= 0) {
            Logger.error("texturePath.length() <= 0");
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
            texture = TextureCache.load(Paths.get(path).getParent() + "/" + texturePath.dataString());
        }
        texturePath.free();

        return texture;
    }

    protected final Mesh[] getMeshes() {
        return meshes;
    }

    private Mesh[] meshes;
    private final ArrayList<Texture> textures = new ArrayList<>();
    private final ArrayList<Material> materials = new ArrayList<>();

    private final String path;
}


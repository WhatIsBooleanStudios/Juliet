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

        AIString diffuseTexturePath = AIString.calloc();
        Assimp.aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, diffuseTexturePath, (IntBuffer) null, null, null, null, null, null);

        AIColor4D color4D = AIColor4D.calloc();
        Assimp.aiGetMaterialColor(material, AI_MATKEY_COLOR_DIFFUSE, 0, 0, color4D);
        Logger.trace("Color: " + color4D.r() + " " + color4D.g() + " " + color4D.b());

        Logger.trace("Material name: " + name.dataString());
        Logger.trace("Material path: " + diffuseTexturePath.dataString());

        if (diffuseTexturePath.length() > 1 && diffuseTexturePath.dataString().charAt(0) == '*') {
            try {
                int textureIndex = Integer.parseInt(diffuseTexturePath.dataString().substring(1), 10);
                if (textureIndex >= this.textures.size()) {
                    Logger.error("Model.processMaterial", this, "Loaded material references nonexistent internal texture!");
                    return;
                }
                materials.add(new Material(name.dataString(), textures.get(textureIndex)));
            } catch (NumberFormatException ignored) {
            }
        } else {
            materials.add(new Material(diffuseTexturePath.dataString(), TextureCache.load(Paths.get(path).getParent() + "/" + diffuseTexturePath.dataString())));
        }

        color4D.free();
        name.free();
        diffuseTexturePath.free();
    }

    protected final Mesh[] getMeshes() {
        return meshes;
    }

    private Mesh[] meshes;
    private final ArrayList<Texture> textures = new ArrayList<>();
    private final ArrayList<Material> materials = new ArrayList<>();

    private final String path;
}


package mclone.GFX.Renderer;

import static org.lwjgl.assimp.Assimp.*;

import mclone.GFX.OpenGL.Texture;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import mclone.GFX.OpenGL.Shader;
import mclone.Logging.Logger;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class Model {
    public Model(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIScene scene = aiImportFile(
                path,
                aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals |
                    aiProcess_RemoveRedundantMaterials
            );
            if (scene == null) {
                Logger.error("Model.new", this, "Failed to load model \"" + path + "\"");
            }

            AIString sceneName = scene.mName();
            Logger.trace("Scene name: " + sceneName.dataString());

            int numMaterials = scene.mNumMaterials();
            Logger.trace("num materials: " + numMaterials);
            PointerBuffer aiMaterials = scene.mMaterials();

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

    private void processMaterial(AIMaterial material) {
        AIString name = AIString.calloc();
        Assimp.aiGetMaterialString(material, AI_MATKEY_NAME, 0, 0, name);

        AIString diffuseTexturePath = AIString.calloc();
        Assimp.aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, diffuseTexturePath, (IntBuffer) null, null, null, null, null, null);

        Logger.trace("Material name: " + name.dataString());
        Logger.trace("Material path: " + diffuseTexturePath.dataString());

        if(diffuseTexturePath.length() > 1 && diffuseTexturePath.dataString().charAt(0) == '*') {
            try {
                int textureIndex = Integer.parseInt(diffuseTexturePath.dataString().substring(1), 10);
                if(textureIndex >= this.textures.size()) {
                    Logger.error("Model.processMaterial", this, "Loaded material references nonexistent internal texture!");
                    return;
                }
                materials.add(new Material(name.dataString(), textures.get(textureIndex)));
            } catch(NumberFormatException ignored) {}
        } else {
            // TODO: check if the texture is already in the array. Perhaps we should create a textureCache database
            Texture newTexture = new Texture(diffuseTexturePath.dataString());
            textures.add(newTexture);
            materials.add(new Material(diffuseTexturePath.dataString(), newTexture));
        }

        name.free();
        diffuseTexturePath.free();
    }

    public void tempDraw(Shader shader) {
        for (int i = 0; i < meshes.length; i++) {
            meshes[i].tempDraw(shader);
        }
    }

    protected Mesh[] getMeshes() { return meshes; }
    private Mesh[] meshes;
    ArrayList<Texture> textures = new ArrayList<>();
    ArrayList<Material> materials = new ArrayList<>();
}


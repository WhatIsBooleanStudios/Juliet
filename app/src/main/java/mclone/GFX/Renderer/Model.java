package mclone.GFX.Renderer;

import static org.lwjgl.assimp.Assimp.*;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.system.MemoryStack;

import mclone.GFX.OpenGL.Shader;
import mclone.Logging.Logger;

public class Model {
    public Model(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIScene scene = aiImportFile(
                path,
                aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals
            );
            if (scene == null) {
                Logger.error("Model.new", this, "Failed to load model \"" + path + "\"");
            }

            int numMaterials = scene.mNumMaterials();
            PointerBuffer aiMaterials = scene.mMaterials();

            for (int i = 0; i < numMaterials; i++) {
                AIMaterial material = AIMaterial.create(aiMaterials.get());
                // process materials
            }

            meshes = new Mesh[scene.mNumMeshes()];
            for(int i = 0; i < scene.mNumMeshes(); i++) {
                AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
                meshes[i] = new Mesh(mesh);
            }

        }
    }

    public void tempDraw(Shader shader){
        for(int i = 0; i < meshes.length; i++) {
            meshes[i].tempDraw(shader);
        }
    }

    Mesh[] meshes;
}


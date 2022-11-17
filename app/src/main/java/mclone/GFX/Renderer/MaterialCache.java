package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.Texture;
import mclone.Logging.Logger;

import java.util.HashMap;

public class MaterialCache {
    private MaterialCache(){}

    public final static void loadMaterial(Material material) {
        if(materialMap.containsKey(material)) {
            if(!material.getName().equals(materialMap.get(material).getName())) {
                Logger.warn(
                    "MaterialCache.loadMaterial",
                    "Material \"" + material.getName() + "\" is identical to the material with name \"" + materialMap.get(material).getName() + "\""
                );
            }
            material = materialMap.get(material);
        } else {
            materialMap.put(
                material,
                new Material(
                    material.getName(),
                    material.getDiffuseTexture(),
                    material.getDiffuseColor(),
                    material.getMetallic(),
                    material.getRoughness()
                )
            );
        }
    }

    public void clear() {
        materialMap.clear();
    }

    private final static HashMap<Material, Material> materialMap = new HashMap<>();
}

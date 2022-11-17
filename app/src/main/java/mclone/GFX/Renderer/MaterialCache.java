package mclone.GFX.Renderer;

import mclone.Logging.Logger;

import java.util.HashMap;

public class MaterialCache {
    protected MaterialCache(){}

    public Material loadMaterial(Material material) {
        if(materialMap.containsKey(material)) {
            if(!material.getName().equals(materialMap.get(material).getName())) {
                Logger.warn(
                    "MaterialCache.loadMaterial",
                    "Material \"" + material.getName() + "\" is identical to the material with name \"" + materialMap.get(material).getName() + "\""
                );
            }
            return materialMap.get(material);
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

            return materialMap.get(material);
        }
    }

    public void clear() {
        materialMap.clear();
    }

    private HashMap<Material, Material> materialMap = new HashMap<>();
}

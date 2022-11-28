package mclone.GFX.Renderer;

import mclone.Logging.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ModelLoader {
    protected ModelLoader(Renderer renderer) {
        this.renderer = renderer;
    }

    public Model load(String path) {
        Model model = modelMap.get(path);
        if (model == null) {
            model = new Model(path, renderer.getTextureCache(), renderer.getMaterialCache());
            modelMap.put(path, model);
        }
        return model;
    }

    public void disposeModel(@NotNull Model model) {
        if(modelMap.get(model.getPath()) == null) {
            Logger.error("ModelLoader", this, "Failed to free model \"" + model.getPath() + "\"");
            return;
        }

        modelMap.remove(model.getPath());
        model.dispose();
    }

    protected void clear() {
        for(Model model : modelMap.values()) {
            if(model != null) {
                model.dispose();
            }
        }

        modelMap.clear();
    }
    private HashMap<String, Model> modelMap = new HashMap<>();
    private Renderer renderer;
}

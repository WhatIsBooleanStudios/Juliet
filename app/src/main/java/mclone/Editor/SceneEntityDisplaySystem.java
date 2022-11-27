package mclone.Editor;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import imgui.ImGui;
import mclone.ECS.NameComponent;

@All(NameComponent.class)
public class SceneEntityDisplaySystem extends IteratingSystem {
    ComponentMapper<NameComponent> nameComponentMapper;

    @Override
    protected void process(int entityId) {
        ImGui.text(nameComponentMapper.get(entityId).name);
    }
}

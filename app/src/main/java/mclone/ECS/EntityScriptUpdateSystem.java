package mclone.ECS;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

@All(EntityScriptComponent.class)
public class EntityScriptUpdateSystem extends IteratingSystem {
    public EntityScriptUpdateSystem() {}

    ComponentMapper<EntityScriptComponent> entityScriptComponentMapper;

    @Override
    protected void process(int entityId) {
        EntityScriptComponent scriptComponent = entityScriptComponentMapper.get(entityId);
        if(scriptComponent.entityScript.entity == null) {
            scriptComponent.entityScript.entity = new Entity(getWorld(), entityId);
            scriptComponent.entityScript.init();
        }
        scriptComponent.entityScript.update(getWorld().getDelta());
    }

}

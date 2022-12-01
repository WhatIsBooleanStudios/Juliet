package mclone.ECS;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import mclone.Logging.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

@All(EntityScriptComponent.class)
public class EntityScriptUpdateSystem extends IteratingSystem {
    public EntityScriptUpdateSystem() {}

    ComponentMapper<EntityScriptComponent> entityScriptComponentMapper;

    private HashMap<Integer, EntityScript> entityScriptMap;

    EntityScript loadScript(String classname) {
        EntityScript entityScript = null;
        try {
            Class<?> cls = ClassLoader.getSystemClassLoader().loadClass(classname);
            Constructor<?> constructor = cls.getDeclaredConstructor();
            if(cls.getSuperclass() != EntityScript.class) {
                Logger.error("EntityScriptComponent.EntityScriptComponent", "class \"" + classname + "\" must be a subclass of EntityScript!");
            }
            entityScript = (EntityScript)constructor.newInstance();
            entityScript.entity = null;

        } catch (ClassNotFoundException classNotFoundException) {
            Logger.error("EntityScriptComponent.EntityScriptComponent", "Failed to load class \"" + classname + "\"");
        } catch (NoSuchMethodException noSuchMethodException) {
            Logger.error("EntityScriptComponent.EntityScriptComponent", "class \"" + classname + "\" does not have a constructor with no arguments!");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return entityScript;
    }

    @Override
    protected void process(int entityId) {
        EntityScriptComponent scriptComponent = entityScriptComponentMapper.get(entityId);

        EntityScript entityScript = entityScriptMap.get(entityId);
        if(entityScript == null) {
            entityScript = loadScript(scriptComponent.classname);
            if(entityScript == null) {
                Logger.error("Failed to load script component \"" + scriptComponent.classname + "\"");
                return;
            }
            entityScriptMap.put(entityId, entityScript);
        }
        if(entityScript.entity == null) {
            entityScript.entity = new Entity(getWorld(), entityId);
            entityScript.init();
        }
        entityScript.update(getWorld().getDelta());
    }

    @Override
    protected void removed(int entityId) {
        entityScriptMap.remove(entityId);
        super.removed(entityId);
    }
}

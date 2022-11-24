package mclone.ECS;

import com.artemis.Component;
import mclone.Logging.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class EntityScriptComponent extends Component  {

    public EntityScriptComponent() {}
    public EntityScriptComponent(String classname) {
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
    }

    protected EntityScript entityScript;
}

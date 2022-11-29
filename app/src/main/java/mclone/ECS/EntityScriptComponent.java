package mclone.ECS;

import com.artemis.Component;
import mclone.Logging.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class EntityScriptComponent extends Component  {

    public EntityScriptComponent() {}
    public EntityScriptComponent(String classname) {
        this.classname = classname;
    }

    public String classname;
}

package juliet.ECS;

import com.artemis.Component;

public class EntityScriptComponent extends Component  {

    public EntityScriptComponent() {}
    public EntityScriptComponent(String classname) {
        this.classname = classname;
    }

    public String classname;
}

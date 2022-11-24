package mclone.ECS;

import com.artemis.Component;
import com.artemis.ComponentType;
import com.artemis.World;

public class Entity {
    protected Entity(World parent, int id) {
        this.id = id;
        this.world = parent;
    }

    public void addComponent(Component component) {
        world.edit(id)
            .add(component);
    }

    public <T extends Component> T getComponent(Class<T> type) {
        return world.getMapper(type).get(id);
    }

    public void removeComponent(Component component) {
        world.edit(id)
            .remove(component);
    }

    public void removeComponent(ComponentType componentType) {
        world.edit(id)
            .remove(componentType);
    }

    protected int id;
    protected World world;
}

package mclone.ECS;

import com.artemis.Component;
import com.artemis.ComponentType;
import com.artemis.World;
import com.artemis.managers.TagManager;

public class Entity {
    protected Entity(World parent, int id) {
        this.id = id;
        this.world = parent;
    }

    @Override
    public String toString() {
        return "mclone.Entity(id=" + id + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != Entity.class)
            return false;

        return (((Entity) obj).id) == id;
    }

    public void addComponent(Component component) {
        world.edit(id)
            .add(component);
    }

    public <T extends Component> T getComponent(Class<T> type) {
        return world.getMapper(type).get(id);
    }

    public <T extends Component> boolean hasComponent(Class<T> type) {
        return world.getMapper(type).has(id);
    }

    public void removeComponent(Component component) {
        world.edit(id)
            .remove(component);
    }

    public String getName() {
        return world.getSystem(TagManager.class).getTag(id);
    }

    public void removeComponent(ComponentType componentType) {
        world.edit(id)
            .remove(componentType);
    }



    protected int id;
    protected World world;
}

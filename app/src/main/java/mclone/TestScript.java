package mclone;

import com.artemis.ComponentType;
import mclone.ECS.EntityScript;
import mclone.ECS.NameComponent;

public class TestScript extends EntityScript {
    @Override
    protected void init() {
        System.out.println("entity init!");
        NameComponent nameComponent = getEntity().getComponent(NameComponent.class);
        if(nameComponent != null) {
            System.out.println("Entity name: " + nameComponent.name);
        }
    }

    @Override
    protected void update(float deltaTime) {
        System.out.println("Tick, dt = " + deltaTime);
    }
}

package mclone.ECS;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;

public class Scene {
    public Scene() {
        WorldConfigurationBuilder builder = new WorldConfigurationBuilder()
            .with(new EntityScriptUpdateSystem());

        world = new World(builder.build());
    }

    public void process(float deltaTime) {
        world.setDelta(deltaTime);
        world.process();
    }

    public Entity createEntity() {
        int entityID = world.create();
        return new Entity(world, entityID);
    }

    public void deleteEntity(Entity entity) {
        world.delete(entity.id);
    }

    private World world;
}

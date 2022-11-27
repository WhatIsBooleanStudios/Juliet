package mclone.ECS;

import com.artemis.Aspect;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import mclone.Editor.SceneEntityDisplaySystem;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Scene {
    public Scene() {
        WorldSerializationManager serializationManager = new WorldSerializationManager();
        WorldConfigurationBuilder builder = new WorldConfigurationBuilder()
            .with(serializationManager)
            .with(new EntityScriptUpdateSystem())
            .with(new SceneEntityDisplaySystem());

        world = new World(builder.build());
        serializationManager.setSerializer(new JsonArtemisSerializer(world));
    }

    public void tempSaveToFile() {
         try {
        Path path = Paths.get("level.json");
        FileOutputStream fos = new FileOutputStream(path.toFile(), false);
        // Collect the entities
        EntitySubscription entitySubscription = world.getAspectSubscriptionManager().get(Aspect.all());
        IntBag entities = entitySubscription.getEntities();

        world.getSystem(WorldSerializationManager.class).save(fos, new SaveFileFormat(entities));
            fos.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public void disableScriptSystem() {
        world.getSystem(EntityScriptUpdateSystem.class).setEnabled(false);
    }

    public void enableScriptSystem() {
        world.getSystem(EntityScriptUpdateSystem.class).setEnabled(true);
    }

    public void disableSceneEntityDisplaySystem() {
        world.getSystem(SceneEntityDisplaySystem.class).setEnabled(false);
    }

    public void enableSceneEntityDisplaySystem() {
        world.getSystem(SceneEntityDisplaySystem.class).setEnabled(true);
    }

    private World world;
}

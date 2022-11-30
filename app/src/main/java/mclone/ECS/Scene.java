package mclone.ECS;

import com.artemis.Aspect;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.TagManager;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Scene {
    public Scene() {
        WorldSerializationManager serializationManager = new WorldSerializationManager();
        WorldConfigurationBuilder builder = new WorldConfigurationBuilder()
            .with(serializationManager)
            .with(new TagManager())
            .with(new EntityScriptUpdateSystem());

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

    public Entity createEntity(String tag) {
        int entityID = world.create();
        world.getSystem(TagManager.class).register(tag, entityID);
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


    IntBag compiledEntitiesList;

    public void __compileEntitiesList() {
        compiledEntitiesList = world.getAspectSubscriptionManager().get(Aspect.all()).getEntities();
    }

    /**
     * @warn Call compileEntitiesList
     * @return
     */
    public int __getNumEntities() {
        return compiledEntitiesList.size();
    }

    public Entity __getEntityByInternalListIndex(int index) {
        return new Entity(world, compiledEntitiesList.get(index));
    }

    private World world;

    public boolean hasEntityByName(String s) {
        return world.getSystem(TagManager.class).getEntityId(s) >= 0;
    }
}

package juliet.ECS;

public abstract class EntityScript {
    public EntityScript() {}

    protected abstract void init();
    protected abstract void update(float deltaTime);

    Entity entity;
    protected Entity getEntity() { return entity; }
}

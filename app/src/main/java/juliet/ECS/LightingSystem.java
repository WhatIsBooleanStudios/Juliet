package juliet.ECS;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import juliet.GFX.Renderer.LightManager;
import juliet.GFX.Renderer.PointLight;

@All(PointLightsComponent.class)
public class LightingSystem extends IteratingSystem {
    public LightingSystem(LightManager lightManager) {
        this.lightManager = lightManager;
    }

    ComponentMapper<PointLightsComponent> pointLightComponentMapper;
    @Override
    protected void process(int entityId) {
        for(PointLightsComponent.PointLightData data : pointLightComponentMapper.get(entityId).pointLightData) {
            lightManager.addPointLight(new PointLight(data.position, data.color, data.intensity));
        }
    }

    LightManager lightManager;
}

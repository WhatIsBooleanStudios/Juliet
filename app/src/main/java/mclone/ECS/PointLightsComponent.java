package mclone.ECS;

import com.artemis.Component;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PointLightsComponent extends Component {
    public static class PointLightData {
        public PointLightData() {}
        public PointLightData(Vector3fc position, Vector3fc color, float intensity) {
            this.position.set(position);
            this.color.set(color);
            this.intensity = intensity;
        }

        public Vector3f position = new Vector3f();
        public Vector3f color = new Vector3f();
        public float intensity = 0.0f;
    }

    public PointLightsComponent() {}

    public PointLightsComponent(PointLightData[] data) {
        pointLightData = data;
    }

    public PointLightData[] pointLightData;

}

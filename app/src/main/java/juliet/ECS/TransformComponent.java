package juliet.ECS;

import com.artemis.Component;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class TransformComponent extends Component {
    public TransformComponent() {}

    public TransformComponent(Vector3fc position) {
        this.position.set(position);
    }

    public Vector3f position = new Vector3f();
}

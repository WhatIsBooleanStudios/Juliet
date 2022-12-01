package juliet.GFX.Renderer;

import org.joml.Vector3fc;

public abstract class CameraController {
    protected abstract void bindToBindingPoint(int index);
    protected abstract Vector3fc getCameraPosition();
}

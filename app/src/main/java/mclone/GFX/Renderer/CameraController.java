package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.UniformBuffer;
import org.joml.Vector3fc;

import java.util.Vector;

public abstract class CameraController {
    protected abstract void bindToBindingPoint(int index);
    protected abstract Vector3fc getCameraPosition();
}

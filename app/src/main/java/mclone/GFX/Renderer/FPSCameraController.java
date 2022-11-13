package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.HardwareBuffer;
import mclone.GFX.OpenGL.UniformBuffer;
import mclone.Platform.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.glfw.GLFW.*;

public class FPSCameraController extends CameraController {
    public FPSCameraController(Window window, Vector3f position, float pitch, float yaw) {
        this.camera = new Camera((float)window.getWidth() / (float) window.getHeight(), (float)Math.PI / 4.0f);
        this.camera.setCameraPosition(position);
        this.camera.setYaw(yaw);
        this.camera.setPitch(pitch);
    }

    private final Vector2f previousMousePosition = new Vector2f();
    boolean firstUpdate = true;

    public void update(Window window) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            if (firstUpdate) {
                previousMousePosition.set(window.getMousePosition());
            }
            final float cameraSpeed = 0.01f; // adjust accordingly
            boolean positionModified = false;
            if (window.keyPressed(GLFW_KEY_W)) {
                camera.offsetCameraPosition(camera.getDirection().mul(cameraSpeed));
                positionModified = true;
            }
            if (window.keyPressed(GLFW_KEY_S)) {
                camera.offsetCameraPosition(camera.getDirection().mul(-cameraSpeed));
                positionModified = true;
            }
            if (window.keyPressed(GLFW_KEY_A)) {
                camera.offsetCameraPosition(camera.getDirection().cross(new Vector3f(0.0f, 1.0f, 0.0f)).normalize().mul(-cameraSpeed));
                positionModified = true;
            }
            if (window.keyPressed(GLFW_KEY_D)) {
                camera.offsetCameraPosition(camera.getDirection().cross(new Vector3f(0.0f, 1.0f, 0.0f)).normalize().mul(cameraSpeed));
                positionModified = true;
            }

            Vector2f mousePosition = window.getMousePosition();
            Vector2f mouseOffset = new Vector2f(mousePosition).sub(previousMousePosition);
            previousMousePosition.set(mousePosition);
            float sensitivity = 0.001f;
            mouseOffset.mul(sensitivity);

            camera.offsetYaw(mouseOffset.x);
            camera.offsetPitch(-mouseOffset.y);

            if (firstUpdate || mouseOffset.x != 0 || mouseOffset.y != 0 || positionModified) {
                ubo.setData(camera.getProjectionXView().get(stack.mallocFloat(16)), 16 * 4);
            }
            firstUpdate = false;
        }
    }

    @Override
    protected void bindToBindingPoint(int binding) {
        ubo.setToBindingPoint(binding);
    }

    @Override
    protected Vector3fc getCameraPosition() {
        return camera.getPosition();
    }

    UniformBuffer ubo = new UniformBuffer(null, 64, HardwareBuffer.UsageHints.USAGE_DYNAMIC);

    private final Camera camera;
}

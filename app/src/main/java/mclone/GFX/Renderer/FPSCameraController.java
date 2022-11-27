package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.HardwareBuffer;
import mclone.GFX.OpenGL.UniformBuffer;
import mclone.Logging.Logger;
import mclone.Platform.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.glfw.GLFW.*;

public class FPSCameraController extends CameraController {
    public FPSCameraController(Window window, Vector3f position, float aspectRatio, float pitch, float yaw) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            this.camera = new Camera(aspectRatio, (float) Math.PI / 4.0f);
            this.camera.setCameraPosition(position);
            this.camera.setYaw(yaw);
            this.camera.setPitch(pitch);
            ubo.setData(camera.getProjectionXView().get(stack.mallocFloat(16)), 16 * 4);
        }
    }

    boolean firstUpdate = true;

    public void update(Window window) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            Vector2f initialMousePosition = window.getScreenCenter();
            final float cameraSpeed = 0.03f; // adjust accordingly
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
            Vector2f mouseOffset = new Vector2f(mousePosition).sub(initialMousePosition);
            //previousMousePosition.set(mousePosition);
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
    public Vector3fc getCameraPosition() {
        return camera.getPosition();
    }

    public void updateProjection(float ratio, float fovY) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            camera.updateProjection(ratio, fovY);
            ubo.setData(camera.getProjectionXView().get(stack.mallocFloat(16)), 16 * 4);
        }
    }

    public Vector3fc getCameraDirection() {
        return camera.getDirection();
    }

    public void dispose() {
        ubo.dispose();
    }

    UniformBuffer ubo = new UniformBuffer(null, 64, HardwareBuffer.UsageHints.USAGE_DYNAMIC);

    private final Camera camera;
}

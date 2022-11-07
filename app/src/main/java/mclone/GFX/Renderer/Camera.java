package mclone.GFX.Renderer;

import mclone.Logging.Logger;
import org.joml.Vector3f;
import org.joml.Matrix4f;

public class Camera {
    /**
     * Create a 3DCamera object
     * @param aspectRatio The aspect ratio of the window
     * @param fovY The vertical Y of the camera
     */
    public Camera(float aspectRatio, float fovY) {
        this.projection = new Matrix4f().perspective(fovY, aspectRatio, 0.1f, 100.0f);
        this.projectionXView = calculate();
    }

    /**
     * Calculates the ProjectionXView Mat4x4
     * @return Returns a Mat4x4 of the ProjectionXView
     */
    private Matrix4f calculate() {
        Vector3f direction = getDirection();
        direction.add(cameraPosition);
        Matrix4f tProjection = new Matrix4f(this.projection);
        return tProjection.mul(new Matrix4f().identity().lookAt(direction, cameraPosition, new Vector3f(0.0f,1.0f,0.0f)));
    }

    /**
     *
     */
    public Vector3f getDirection() {
        Vector3f direction = new Vector3f((float)Math.cos(this.yaw - (float)(Math.PI / 2.0f)) * (float)Math.cos(this.pitch),
                                             (float)Math.sin(this.pitch),
                                          (float)Math.sin(this.yaw - (float)(Math.PI / 2.0f)) * (float)Math.cos(this.pitch));
        return direction;
    }

    /**
     * @return Returns the ProjectionXView of the current scene
     */
    public Matrix4f getProjectionXView() { return this.projectionXView; }

    /**
     * Sets the pitch of the direction view
     * @param pitch The pitch to overwrite
     */
    public void setPitch(float pitch) { this.pitch = pitch; this.projectionXView = calculate(); }

    /**
     * Offsets the current pitch value
     * @param pitch The pitch to offset by
     */
    public void offsetPitch(float pitch) { this.pitch += pitch; this.projectionXView = calculate(); }

    /**
     * Sets the yaw of the direction view
     * @param yaw The yaw to overwrite
     */
    public void setYaw(float yaw) { this.yaw = yaw; this.projectionXView = calculate(); }

    /**
     * Offsets the current yaw value
     * @param yaw The yaw to offset by
     */
    public void offsetYaw(float yaw) { this.yaw += yaw; this.projectionXView = calculate(); }

    /**
     * Sets the position of the camera
     * @param cameraPosition The new position of the camera
     */
    public void setCameraPosition(Vector3f cameraPosition) { this.cameraPosition = cameraPosition; this.projectionXView = calculate(); }

    /**
     * Offsets the current camera position
     * @param cameraPosition The position to offset the current camera by
     */
    public void offsetCameraPosition(Vector3f cameraPosition) { this.cameraPosition.add(cameraPosition); this.projectionXView = calculate(); }

    private Vector3f cameraPosition = new Vector3f(0.0f,0.0f,0.0f);
    private float pitch = 0.0f;
    private float yaw = 0.0f;
    private static final float roll = 0.0f;
    final Matrix4f projection;
    Matrix4f projectionXView;
}

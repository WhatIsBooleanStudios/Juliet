package mclone.GFX.Renderer;

import org.joml.Vector3f;

import org.joml.Matrix4f;

public class Camera {
    public Camera(float aspectRatio, float fovY) {
        this.projection = new Matrix4f().perspective(fovY, aspectRatio, 0.1f, 100.0f);
        this.projectionXView = calculate();
    }

    private Matrix4f calculate() {
        Vector3f direction = new Vector3f((float)Math.cos(this.yaw) * (float)Math.cos(this.pitch),
                                             (float)Math.sin(this.pitch),
                                          (float)Math.sin(this.yaw) * (float)Math.cos(this.pitch));
        Matrix4f tProjection = new Matrix4f(this.projection);
        return tProjection.mul(new Matrix4f().lookAt(direction, cameraPosition, new Vector3f(0.0f,1.0f,0.0f)));
    }
    public Matrix4f getProjectionXView() { return this.projectionXView; }
    public void setPitch(float pitch) { this.pitch = pitch; this.projectionXView = calculate(); }
    public void offsetPitch(float pitch) { this.pitch += pitch; this.projectionXView = calculate(); }
    public void setYaw(float yaw) { this.pitch = yaw; this.projectionXView = calculate(); }
    public void offsetYaw(float yaw) { this.pitch += yaw; this.projectionXView = calculate(); }
    public void setCameraPosition(Vector3f cameraPosition) { this.cameraPosition = cameraPosition; this.projectionXView = calculate(); }
    public void offsetCameraPosition(Vector3f cameraPosition) { this.cameraPosition.add(cameraPosition); this.projectionXView = calculate(); }

    private Vector3f cameraPosition = new Vector3f(0.0f,0.0f,0.0f);
    private float pitch = 0.0f;
    private float yaw = 0.0f;
    private static final float roll = 0.0f;
    final Matrix4f projection;
    Matrix4f projectionXView;
}

package mclone.GFX.Renderer;

import org.joml.Vector3f;

import org.joml.Matrix4f;

public class Camera {
    public Camera(float aspecRatio, float fovy) {
        this.projection = new Matrix4f().perspective(fovy, aspecRatio, 0.1f, 100.0f);
        this.projectionXView = calculate();
    }

    public Matrix4f calculate() {
        Vector3f direction = new Vector3f((float)Math.cos(this.yaw) * (float)Math.cos(this.pitch),
                                             (float)Math.sin(this.pitch),
                                          (float)Math.sin(this.yaw) * (float)Math.cos(this.pitch));
        Matrix4f tProjection = new Matrix4f(this.projection);
        return tProjection.mul(new Matrix4f().lookAt(direction, cameraPosition, new Vector3f(0.0f,1.0f,0.0f)));
    }

    public void setPitch(float pitch) { this.pitch = pitch; }
    public void offsetPitch(float pitch) { this.pitch += pitch; }
    public void setYaw(float yaw) { this.pitch = yaw; }
    public void offsetYaw(float yaw) { this.pitch += yaw; }
    public void setCameraPosition(Vector3f cameraPosition) { this.cameraPosition = cameraPosition; }
    public void offsetCameraPosition(Vector3f cameraPosition) { this.cameraPosition.add(cameraPosition); }

    private Vector3f cameraPosition = new Vector3f(0.0f,0.0f,0.0f);
    private Vector3f cameraTarget = new Vector3f(0.0f,0.0f,0.0f);
    private float pitch = 0.0f;
    private float yaw = 0.0f;
    private static final float roll = 0.0f;
    final Matrix4f projection;
    Matrix4f projectionXView;
}

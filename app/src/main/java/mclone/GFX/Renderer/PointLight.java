package mclone.GFX.Renderer;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.nio.FloatBuffer;

public class PointLight {
    public PointLight(Vector3fc position, Vector3fc color, float intensity) {
        this.position = new Vector3f(position);
        this.color = new Vector3f(color);
        this.intensity = intensity;
        update();
    }

    private void update() {
        dataBuffer[0] = position.x();
        dataBuffer[1] = position.y();
        dataBuffer[2] = position.z();
        dataBuffer[3] = color.x() * intensity;
        dataBuffer[4] = color.y() * intensity;
        dataBuffer[5] = color.z() * intensity;
        dataBuffer[6] = 0.0f;
        dataBuffer[7] = 0.0f;
    }

    public Vector3fc getPosition() {
        return position;
    }

    public Vector3fc getColor() {
        return color;
    }

    public float getIntensity() {
        return intensity;
    }

    protected float[] getDataBuffer() {
        return dataBuffer;
    }

    public void setPosition(Vector3fc pos) {
        this.position.set(pos);
        update();
    }

    public void setColor(Vector3fc clr) {
        this.color.set(clr);
        update();
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
        update();
    }

    private final Vector3f position;
    private final Vector3f color;
    private float intensity;

    public static int dataBufferNumFloats() { return 8; }
    /**
     * struct PointLight {
     *     float x, y , z;
     *     float r * intensity, g * intensity, b * intensity;
     *     float[2] padding;
     * }
     */
    final float[] dataBuffer = new float[dataBufferNumFloats()];
}

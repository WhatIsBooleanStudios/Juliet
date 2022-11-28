package mclone.GFX.Renderer;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class SpotLight {
    public SpotLight(Vector3fc position, Vector3fc direction, Vector3fc color, float intensity, float cutoff) {
        this.position = new Vector3f(position);
        this.direction = new Vector3f(direction).normalize();
        this.color = new Vector3f(color);
        this.intensity = intensity;
        this.cutoff = cutoff;
        update();
    }

    public Vector3fc getPosition() {
        return position;
    }

    public void setPosition(Vector3fc pos) {
        this.position.set(pos);
        update();
    }

    public Vector3fc getDirection() {
        return direction;
    }

    public void setDirection(Vector3fc dir) {
        direction.set(dir);
        direction.normalize();
        update();
    }

    public Vector3fc getColor() {
        return color;
    }

    public void setColor(Vector3fc color) {
        this.color.set(color);
        update();
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
        update();
    }

    public float getCutoff() {
        return cutoff;
    }

    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
        update();
    }

    protected void update() {
        dataBuffer[0] = position.x();
        dataBuffer[1] = position.y();
        dataBuffer[2] = position.z();
        dataBuffer[3] = cutoff;
        dataBuffer[4] = direction.x();
        dataBuffer[5] = direction.y();
        dataBuffer[6] = direction.z();
        dataBuffer[7] = 0.0f;
        dataBuffer[8] = color.x() * intensity;
        dataBuffer[9] = color.y() * intensity;
        dataBuffer[10] = color.z() * intensity;
        dataBuffer[11] = 0.0f;
    }
    private final Vector3f position;
    private final Vector3f direction;
    private final Vector3f color;
    private float intensity;
    private float cutoff;

    public static int dataBufferNumFloats() {
        return 12;
    }

    /**
     * struct SpotLight {
     *     float x, y, z;
     *     float cutoff;
     *     float dirX, dirY, dirZ;
     *     float padding1;
     *     float r, g, b;
     *     float padding 2;
     * }
     */
    protected float[] dataBuffer = new float[dataBufferNumFloats()];
}

package mclone.GFX.Renderer;

import mclone.GFX.OpenGL.HardwareBuffer;
import mclone.GFX.OpenGL.UniformBuffer;
import mclone.Logging.Logger;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class LightManager {
    protected LightManager() {
        pointLightUBO = new UniformBuffer(null, 4L * PointLight.dataBufferNumFloats() * MAX_POINT_LIGHTS, HardwareBuffer.UsageHints.USAGE_DYNAMIC);
    }

    public void addPointLight(PointLight pointLight) {
        if(currentPointLightIndex < 128) {
            System.arraycopy(pointLight.dataBuffer, 0, pointLightsData, currentPointLightIndex * PointLight.dataBufferNumFloats(), PointLight.dataBufferNumFloats());
            currentPointLightIndex++;
            modifiedPointLights = true;
        } else {
            Logger.warn("LightManager.addPointLight", this, "Too many point lights! This number must not exceed " + MAX_POINT_LIGHTS);
        }
    }

    public int getNumPointLights() {
        return currentPointLightIndex;
    }

    protected void clearPointLights() {
        currentPointLightIndex = 0;
        modifiedPointLights = true;
    }

    public void clearLights() {
        clearPointLights();
    }

    protected void updatePointLights() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            if(modifiedPointLights) {
                FloatBuffer buffer = stack.mallocFloat(currentPointLightIndex * PointLight.dataBufferNumFloats());
                System.out.println("numFloats=" + (currentPointLightIndex * PointLight.dataBufferNumFloats()));
                buffer.put(pointLightsData, 0, currentPointLightIndex * PointLight.dataBufferNumFloats());
                buffer.flip();
                pointLightUBO.setData(buffer, (long) currentPointLightIndex * PointLight.dataBufferNumFloats() * 4L);
                modifiedPointLights = false;
            }
        }
    }

    protected void updateLights() {
        updatePointLights();
    }

    protected void dispose() {
        pointLightUBO.dispose();
    }

    public static final int MAX_POINT_LIGHTS = 128;
    boolean modifiedPointLights = false;
    private int currentPointLightIndex = 0;
    private final float[] pointLightsData = new float[MAX_POINT_LIGHTS * PointLight.dataBufferNumFloats()];
    protected final UniformBuffer pointLightUBO;
}

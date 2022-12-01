package juliet.GFX.Renderer;

import juliet.GFX.OpenGL.HardwareBuffer;
import juliet.GFX.OpenGL.UniformBuffer;
import juliet.Logging.Logger;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class LightManager {
    protected LightManager() {
        pointLightUBO = new UniformBuffer(null, 4L * PointLight.dataBufferNumFloats() * MAX_POINT_LIGHTS, HardwareBuffer.UsageHints.USAGE_DYNAMIC);
        spotLightUBO = new UniformBuffer(null, 4L * SpotLight.dataBufferNumFloats() * MAX_POINT_LIGHTS, HardwareBuffer.UsageHints.USAGE_DYNAMIC);
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

    public void addSpotLight(SpotLight spotLight) {
        if(currentSpotLightIndex < 128) {
            System.arraycopy(spotLight.dataBuffer, 0, spotLightsData, currentSpotLightIndex * SpotLight.dataBufferNumFloats(), SpotLight.dataBufferNumFloats());
            currentSpotLightIndex++;
            modifiedSpotLights = true;
        } else {
            Logger.warn("LightManager.addSpotLight", this, "Too many SpotLights! This number must not exceed " + MAX_SPOTLIGHTS);
        }
    }

    public int getNumSpotLights() {
        return currentSpotLightIndex;
    }

    protected void clearSpotLights() {
        currentSpotLightIndex = 0;
        modifiedSpotLights = true;
    }



    public void clearLights() {
        clearPointLights();
        clearSpotLights();
    }

    protected void updatePointLights() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            if(modifiedPointLights && currentPointLightIndex > 0) {
                FloatBuffer buffer = stack.mallocFloat(currentPointLightIndex * PointLight.dataBufferNumFloats());
                buffer.put(pointLightsData, 0, currentPointLightIndex * PointLight.dataBufferNumFloats());
                buffer.flip();
                pointLightUBO.setData(buffer, (long) currentPointLightIndex * PointLight.dataBufferNumFloats() * 4L);
                modifiedPointLights = false;
            }
        }
    }

    protected void updateSpotLights() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            if(modifiedSpotLights) {
                FloatBuffer buffer = stack.mallocFloat(currentSpotLightIndex * SpotLight.dataBufferNumFloats());
                buffer.put(spotLightsData, 0, currentSpotLightIndex * SpotLight.dataBufferNumFloats());
                buffer.flip();
                spotLightUBO.setData(buffer, (long) currentSpotLightIndex * SpotLight.dataBufferNumFloats() * 4L);
                modifiedSpotLights = false;
            }
        }
    }

    protected void updateLights() {
        updatePointLights();
        updateSpotLights();
    }

    protected void shutdown() {
        pointLightUBO.dispose();
        spotLightUBO.dispose();
    }

    public static final int MAX_POINT_LIGHTS = 128;
    boolean modifiedPointLights = false;
    private int currentPointLightIndex = 0;
    private final float[] pointLightsData = new float[MAX_POINT_LIGHTS * PointLight.dataBufferNumFloats()];
    protected final UniformBuffer pointLightUBO;

    public static final int MAX_SPOTLIGHTS = 128;
    boolean modifiedSpotLights = false;
    private int currentSpotLightIndex = 0;
    private final float[] spotLightsData = new float[MAX_SPOTLIGHTS * SpotLight.dataBufferNumFloats()];
    protected final UniformBuffer spotLightUBO;
}

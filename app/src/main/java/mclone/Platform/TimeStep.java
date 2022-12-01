package mclone.Platform;

public class TimeStep {
    public TimeStep(){
         previousTime = System.nanoTime();
    }

    public float updateAndCalculateDelta() {
        long currentTime = System.nanoTime();
        float dt = (float)(currentTime - previousTime) * 1e-6f;
        previousTime = currentTime;
        return dt;
    }

    private long previousTime;
}

package juliet;

import juliet.Editor.Editor;
import juliet.GFX.OpenGL.*;
import juliet.Platform.TimeStep;
import juliet.Platform.Window;

import juliet.Logging.Logger;

public class App {

    public void run(boolean isEditor) {
        Logger.initialize(true, "log.txt");
        Window.initializeWindowSystem();

        if(isEditor) {
            Editor editor = new Editor();
            editor.init();

            TimeStep timeStep = new TimeStep();
            while(!editor.shouldExit()) {
                float delta = timeStep.updateAndCalculateDelta();
                editor.update(delta);
            }

            editor.shutdown();
        }

        GraphicsAPI.shutdown();
        Window.shutdownWindowSystem();
        Logger.shutdown();
    }

    @Override
    public String toString() {
        return "juliet.App";
    }

    public static void main(String[] args) {
        new App().run(true);    }

}

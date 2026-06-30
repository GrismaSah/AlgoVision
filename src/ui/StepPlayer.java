package ui;

import model.AlgorithmStep;

import javax.swing.Timer;
import java.util.List;
import java.util.function.Consumer;

/**
 * Plays a list of AlgorithmSteps back on a Swing Timer (so all UI updates
 * happen safely on the Event Dispatch Thread). This single class powers the
 * pause / resume / step / reset / speed controls for every visualizer.
 */
public class StepPlayer {

    private List<AlgorithmStep> steps;
    private int index;
    private final Timer timer;
    private Consumer<AlgorithmStep> onStep;
    private Runnable onFinish;

    public StepPlayer(int delayMs) {
        timer = new Timer(delayMs, e -> tick());
    }

    public void configure(Consumer<AlgorithmStep> onStep, Runnable onFinish) {
        this.onStep = onStep;
        this.onFinish = onFinish;
    }

    public void load(List<AlgorithmStep> steps) {
        timer.stop();
        this.steps = steps;
        this.index = 0;
    }

    private void tick() {
        if (steps == null || index >= steps.size()) { timer.stop(); return; }
        AlgorithmStep s = steps.get(index++);
        if (onStep != null) onStep.accept(s);
        if (index >= steps.size()) {
            timer.stop();
            if (onFinish != null) onFinish.run();
        }
    }

    public void play()  { if (steps != null && index < steps.size()) timer.start(); }
    public void pause() { timer.stop(); }
    public void step()  { timer.stop(); tick(); }   // advance exactly one step
    public void reset() { timer.stop(); index = 0; }

    public boolean isPlaying()  { return timer.isRunning(); }
    public boolean isLoaded()   { return steps != null; }
    public boolean isFinished() { return steps == null || index >= steps.size(); }
    public void setDelay(int ms){ timer.setDelay(ms); }
}

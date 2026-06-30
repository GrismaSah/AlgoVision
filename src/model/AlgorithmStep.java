package model;

/**
 * One immutable step in an algorithm's execution. The meaning of {@code a},
 * {@code b} and {@code value} depends on {@link #type}. Factory methods below
 * make the services readable (e.g. AlgorithmStep.compare(...)).
 */
public class AlgorithmStep {

    public final StepType type;
    public final int a;        // primary index / node / range-low
    public final int b;        // secondary index / range-high
    public final int value;    // value for OVERWRITE
    public final int codeLine; // which pseudocode line is "active"
    public final String message;

    public AlgorithmStep(StepType type, int a, int b, int value, int codeLine, String message) {
        this.type = type;
        this.a = a;
        this.b = b;
        this.value = value;
        this.codeLine = codeLine;
        this.message = message;
    }

    public static AlgorithmStep compare(int a, int b, int line, String msg) {
        return new AlgorithmStep(StepType.COMPARE, a, b, 0, line, msg);
    }
    public static AlgorithmStep swap(int a, int b, int line, String msg) {
        return new AlgorithmStep(StepType.SWAP, a, b, 0, line, msg);
    }
    public static AlgorithmStep overwrite(int index, int value, int line, String msg) {
        return new AlgorithmStep(StepType.OVERWRITE, index, index, value, line, msg);
    }
    public static AlgorithmStep mark(int index, int line, String msg) {
        return new AlgorithmStep(StepType.MARK_SORTED, index, index, 0, line, msg);
    }
    public static AlgorithmStep pivot(int index, int line, String msg) {
        return new AlgorithmStep(StepType.PIVOT, index, index, 0, line, msg);
    }
    public static AlgorithmStep probe(int index, int line, String msg) {
        return new AlgorithmStep(StepType.PROBE, index, index, 0, line, msg);
    }
    public static AlgorithmStep range(int lo, int hi, int line, String msg) {
        return new AlgorithmStep(StepType.RANGE, lo, hi, 0, line, msg);
    }
    public static AlgorithmStep found(int index, int line, String msg) {
        return new AlgorithmStep(StepType.FOUND, index, index, 0, line, msg);
    }
    public static AlgorithmStep notFound(int line, String msg) {
        return new AlgorithmStep(StepType.NOT_FOUND, -1, -1, 0, line, msg);
    }
    public static AlgorithmStep node(int n, int line, String msg) {
        return new AlgorithmStep(StepType.VISIT_NODE, n, n, 0, line, msg);
    }
    public static AlgorithmStep frontier(int n, int line, String msg) {
        return new AlgorithmStep(StepType.FRONTIER, n, n, 0, line, msg);
    }
    public static AlgorithmStep done(String msg) {
        return new AlgorithmStep(StepType.DONE, -1, -1, 0, -1, msg);
    }
}

package model;

/**
 * Tiny shared state holder. Keeps the most recent run's stats (so the
 * Dashboard can show them) and the user's default preferences (set in Settings).
 * Intentionally simple static fields — no framework needed.
 */
public final class AppState {

    private AppState() {}

    // Last run stats (updated live by the visualizer panels).
    public static String lastModule = "\u2014";   // "Sorting" / "Searching" / "Graph"
    public static int    lastArraySize  = 0;
    public static int    lastComparisons = 0;
    public static int    lastSwaps      = 0;
    public static double lastExecMs     = 0;   // real algorithm compute time, ms (may be fractional)
    public static String lastAlgorithm  = "\u2014";   // em dash

    // User preferences (set in Settings).
    public static int defaultSize  = 45;   // array size
    public static int defaultDelay = 35;   // ms per animation step
    public static int scheme       = 0;    // 0 = Classic, 1 = Ocean, 2 = Sunset
}
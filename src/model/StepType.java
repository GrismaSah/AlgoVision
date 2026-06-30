package model;

/**
 * The kinds of events an algorithm can emit while running. The visualizer
 * replays a list of these to animate what the algorithm did.
 */
public enum StepType {
    COMPARE,      // two indices are being compared
    SWAP,         // two indices swapped values
    OVERWRITE,    // one index was overwritten with a value (used by merge sort)
    MARK_SORTED,  // an index is now in its final sorted position
    PIVOT,        // an index is acting as a pivot / current candidate
    PROBE,        // searching is inspecting an index
    RANGE,        // searching narrowed its active window [a, b]
    FOUND,        // target located at an index
    NOT_FOUND,    // target is absent
    VISIT_NODE,   // graph traversal visited a node
    FRONTIER,     // graph traversal discovered a node (queued/stacked)
    DONE          // the algorithm has finished
}

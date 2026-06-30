package service;

import model.AlgorithmStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Linear and binary search, recorded as steps for the visualizer.
 * Binary search expects a sorted (ascending) array.
 */
public class SearchingService {

    public static List<AlgorithmStep> run(String algorithm, int[] a, int target) {
        if ("Binary Search".equals(algorithm)) return binary(a, target);
        return linear(a, target);
    }

    public static List<AlgorithmStep> linear(int[] a, int target) {
        List<AlgorithmStep> s = new ArrayList<>();
        for (int i = 0; i < a.length; i++) {
            s.add(AlgorithmStep.probe(i, 0, "Checking index " + i + " (" + a[i] + ")"));
            s.add(AlgorithmStep.compare(i, i, 1, "Is " + a[i] + " == " + target + "?"));
            if (a[i] == target) {
                s.add(AlgorithmStep.found(i, 2, "Found " + target + " at index " + i + " \u2713"));
                return s;
            }
        }
        s.add(AlgorithmStep.notFound(3, target + " is not in the array"));
        return s;
    }

    public static List<AlgorithmStep> binary(int[] a, int target) {
        List<AlgorithmStep> s = new ArrayList<>();
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            s.add(AlgorithmStep.range(lo, hi, 1, "Searching in window [" + lo + ", " + hi + "]"));
            int mid = (lo + hi) / 2;
            s.add(AlgorithmStep.probe(mid, 2, "Middle is index " + mid + " (" + a[mid] + ")"));
            if (a[mid] == target) {
                s.add(AlgorithmStep.found(mid, 3, "Found " + target + " at index " + mid + " \u2713"));
                return s;
            } else if (a[mid] < target) {
                s.add(AlgorithmStep.compare(mid, mid, 4, a[mid] + " < " + target + " \u2192 search right"));
                lo = mid + 1;
            } else {
                s.add(AlgorithmStep.compare(mid, mid, 5, a[mid] + " > " + target + " \u2192 search left"));
                hi = mid - 1;
            }
        }
        s.add(AlgorithmStep.notFound(1, target + " is not in the array"));
        return s;
    }
}

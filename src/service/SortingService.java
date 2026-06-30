package service;

import model.AlgorithmStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Each method runs a sorting algorithm on the supplied array (mutating it)
 * and returns the full list of steps the visualizer will replay.
 * Caller should pass a copy if it needs to keep the original order.
 */
public class SortingService {

    public static List<AlgorithmStep> run(String algorithm, int[] a) {
        switch (algorithm) {
            case "Bubble Sort":    return bubble(a);
            case "Selection Sort": return selection(a);
            case "Insertion Sort": return insertion(a);
            case "Merge Sort":     return merge(a);
            case "Quick Sort":     return quick(a);
            case "Heap Sort":      return heap(a);
            default:               return bubble(a);
        }
    }

    private static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }

    private static void markAll(int[] a, List<AlgorithmStep> s) {
        for (int i = 0; i < a.length; i++) s.add(AlgorithmStep.mark(i, -1, "Array sorted"));
    }

    // ---------- Bubble ----------
    public static List<AlgorithmStep> bubble(int[] a) {
        List<AlgorithmStep> s = new ArrayList<>();
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                s.add(AlgorithmStep.compare(j, j + 1, 2, "Comparing " + a[j] + " and " + a[j + 1]));
                if (a[j] > a[j + 1]) {
                    s.add(AlgorithmStep.swap(j, j + 1, 3, "Swapping " + a[j] + " and " + a[j + 1]));
                    swap(a, j, j + 1);
                }
            }
            s.add(AlgorithmStep.mark(n - 1 - i, 4, "Position " + (n - 1 - i) + " locked"));
        }
        s.add(AlgorithmStep.mark(0, 4, "Array sorted"));
        s.add(AlgorithmStep.done("Array sorted \u2713"));
        return s;
    }

    // ---------- Selection ----------
    public static List<AlgorithmStep> selection(int[] a) {
        List<AlgorithmStep> s = new ArrayList<>();
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            s.add(AlgorithmStep.pivot(i, 1, "Current minimum: index " + i));
            for (int j = i + 1; j < n; j++) {
                s.add(AlgorithmStep.compare(j, min, 3, "Comparing " + a[j] + " with min " + a[min]));
                if (a[j] < a[min]) {
                    min = j;
                    s.add(AlgorithmStep.pivot(min, 3, "New minimum: " + a[min]));
                }
            }
            if (min != i) {
                s.add(AlgorithmStep.swap(i, min, 4, "Swapping " + a[i] + " and " + a[min]));
                swap(a, i, min);
            }
            s.add(AlgorithmStep.mark(i, 5, "Position " + i + " locked"));
        }
        s.add(AlgorithmStep.mark(n - 1, 5, "Array sorted"));
        s.add(AlgorithmStep.done("Array sorted \u2713"));
        return s;
    }

    // ---------- Insertion (adjacent-swap variant for clean animation) ----------
    public static List<AlgorithmStep> insertion(int[] a) {
        List<AlgorithmStep> s = new ArrayList<>();
        int n = a.length;
        for (int i = 1; i < n; i++) {
            int j = i;
            while (j > 0) {
                s.add(AlgorithmStep.compare(j - 1, j, 2, "Comparing " + a[j - 1] + " and " + a[j]));
                if (a[j - 1] > a[j]) {
                    s.add(AlgorithmStep.swap(j - 1, j, 3, "Shifting " + a[j] + " left"));
                    swap(a, j - 1, j);
                    j--;
                } else {
                    break;
                }
            }
        }
        markAll(a, s);
        s.add(AlgorithmStep.done("Array sorted \u2713"));
        return s;
    }

    // ---------- Merge ----------
    public static List<AlgorithmStep> merge(int[] a) {
        List<AlgorithmStep> s = new ArrayList<>();
        mergeSort(a, 0, a.length - 1, s);
        markAll(a, s);
        s.add(AlgorithmStep.done("Array sorted \u2713"));
        return s;
    }
    private static void mergeSort(int[] a, int l, int r, List<AlgorithmStep> s) {
        if (l >= r) return;
        int m = (l + r) / 2;
        mergeSort(a, l, m, s);
        mergeSort(a, m + 1, r, s);
        int[] L = Arrays.copyOfRange(a, l, m + 1);
        int[] R = Arrays.copyOfRange(a, m + 1, r + 1);
        int i = 0, j = 0, k = l;
        while (i < L.length && j < R.length) {
            s.add(AlgorithmStep.compare(k, k, 4, "Merging: comparing " + L[i] + " and " + R[j]));
            int val = (L[i] <= R[j]) ? L[i++] : R[j++];
            s.add(AlgorithmStep.overwrite(k, val, 4, "Placing " + val + " at index " + k));
            a[k++] = val;
        }
        while (i < L.length) {
            s.add(AlgorithmStep.overwrite(k, L[i], 5, "Placing " + L[i] + " at index " + k));
            a[k++] = L[i++];
        }
        while (j < R.length) {
            s.add(AlgorithmStep.overwrite(k, R[j], 5, "Placing " + R[j] + " at index " + k));
            a[k++] = R[j++];
        }
    }

    // ---------- Quick (Lomuto) ----------
    public static List<AlgorithmStep> quick(int[] a) {
        List<AlgorithmStep> s = new ArrayList<>();
        quickSort(a, 0, a.length - 1, s);
        s.add(AlgorithmStep.done("Array sorted \u2713"));
        return s;
    }
    private static void quickSort(int[] a, int lo, int hi, List<AlgorithmStep> s) {
        if (lo > hi) return;
        if (lo == hi) { s.add(AlgorithmStep.mark(lo, 0, "Single element sorted")); return; }
        int pivot = a[hi];
        s.add(AlgorithmStep.pivot(hi, 1, "Pivot = " + pivot));
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            s.add(AlgorithmStep.compare(j, hi, 3, "Comparing " + a[j] + " with pivot " + pivot));
            if (a[j] < pivot) {
                i++;
                if (i != j) {
                    s.add(AlgorithmStep.swap(i, j, 4, "Swapping " + a[i] + " and " + a[j]));
                    swap(a, i, j);
                }
            }
        }
        s.add(AlgorithmStep.swap(i + 1, hi, 5, "Placing pivot in position"));
        swap(a, i + 1, hi);
        int p = i + 1;
        s.add(AlgorithmStep.mark(p, 5, "Pivot " + a[p] + " locked at " + p));
        quickSort(a, lo, p - 1, s);
        quickSort(a, p + 1, hi, s);
    }

    // ---------- Heap ----------
    public static List<AlgorithmStep> heap(int[] a) {
        List<AlgorithmStep> s = new ArrayList<>();
        int n = a.length;
        for (int i = n / 2 - 1; i >= 0; i--) heapify(a, n, i, s);
        for (int end = n - 1; end > 0; end--) {
            s.add(AlgorithmStep.swap(0, end, 2, "Move max " + a[0] + " to the end"));
            swap(a, 0, end);
            s.add(AlgorithmStep.mark(end, 2, "Position " + end + " locked"));
            heapify(a, end, 0, s);
        }
        s.add(AlgorithmStep.mark(0, 2, "Array sorted"));
        s.add(AlgorithmStep.done("Array sorted \u2713"));
        return s;
    }
    private static void heapify(int[] a, int size, int i, List<AlgorithmStep> s) {
        int largest = i, l = 2 * i + 1, r = 2 * i + 2;
        if (l < size) {
            s.add(AlgorithmStep.compare(l, largest, 4, "Comparing child " + a[l] + " with " + a[largest]));
            if (a[l] > a[largest]) largest = l;
        }
        if (r < size) {
            s.add(AlgorithmStep.compare(r, largest, 4, "Comparing child " + a[r] + " with " + a[largest]));
            if (a[r] > a[largest]) largest = r;
        }
        if (largest != i) {
            s.add(AlgorithmStep.swap(i, largest, 3, "Swapping " + a[i] + " and " + a[largest]));
            swap(a, i, largest);
            heapify(a, size, largest, s);
        }
    }
}

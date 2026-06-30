package model;

/**
 * Static metadata for each algorithm: a definition, complexity figures and
 * the pseudocode shown (and line-highlighted) in the UI. Looked up by a key.
 */
public class AlgorithmInfo {

    public final String name;
    public final String definition;
    public final String timeBest;
    public final String timeAvg;
    public final String timeWorst;
    public final String space;
    public final String[] pseudocode;

    public AlgorithmInfo(String name, String definition, String best, String avg,
                         String worst, String space, String[] pseudocode) {
        this.name = name;
        this.definition = definition;
        this.timeBest = best;
        this.timeAvg = avg;
        this.timeWorst = worst;
        this.space = space;
        this.pseudocode = pseudocode;
    }

    public static AlgorithmInfo of(String key) {
        switch (key) {
            case "Bubble Sort":
                return new AlgorithmInfo("Bubble Sort",
                    "Repeatedly steps through the list, compares adjacent items and swaps them if they are in the wrong order, so large values \"bubble\" to the end.",
                    "O(n)", "O(n\u00B2)", "O(n\u00B2)", "O(1)",
                    new String[]{
                        "for i = 0 to n-1",
                        "  for j = 0 to n-i-2",
                        "    if a[j] > a[j+1]",
                        "      swap a[j], a[j+1]",
                        "mark pass complete"});
            case "Selection Sort":
                return new AlgorithmInfo("Selection Sort",
                    "Divides the list into sorted and unsorted parts, repeatedly selecting the smallest remaining element and moving it to the sorted part.",
                    "O(n\u00B2)", "O(n\u00B2)", "O(n\u00B2)", "O(1)",
                    new String[]{
                        "for i = 0 to n-1",
                        "  min = i",
                        "  for j = i+1 to n-1",
                        "    if a[j] < a[min]: min = j",
                        "  swap a[i], a[min]",
                        "  mark i sorted"});
            case "Insertion Sort":
                return new AlgorithmInfo("Insertion Sort",
                    "Builds the sorted list one item at a time by taking each element and inserting it into its correct position among the already-sorted elements.",
                    "O(n)", "O(n\u00B2)", "O(n\u00B2)", "O(1)",
                    new String[]{
                        "for i = 1 to n-1",
                        "  j = i",
                        "  while j > 0 and a[j-1] > a[j]",
                        "    swap a[j-1], a[j]",
                        "    j = j - 1"});
            case "Merge Sort":
                return new AlgorithmInfo("Merge Sort",
                    "A divide-and-conquer algorithm: splits the array in half, recursively sorts each half, then merges the two sorted halves back together.",
                    "O(n log n)", "O(n log n)", "O(n log n)", "O(n)",
                    new String[]{
                        "if left < right",
                        "  mid = (left+right)/2",
                        "  mergeSort(left, mid)",
                        "  mergeSort(mid+1, right)",
                        "  merge(left, mid, right)",
                        "  copy remaining elements"});
            case "Quick Sort":
                return new AlgorithmInfo("Quick Sort",
                    "Picks a pivot, partitions the array so smaller elements go left and larger go right, then recursively sorts each partition.",
                    "O(n log n)", "O(n log n)", "O(n\u00B2)", "O(log n)",
                    new String[]{
                        "if low < high",
                        "  pivot = a[high]",
                        "  i = low - 1",
                        "  for j = low to high-1",
                        "    if a[j] < pivot: swap a[++i], a[j]",
                        "  swap a[i+1], a[high]"});
            case "Heap Sort":
                return new AlgorithmInfo("Heap Sort",
                    "Builds a max-heap from the array, then repeatedly swaps the root (largest) with the last element and re-heapifies the shrinking heap.",
                    "O(n log n)", "O(n log n)", "O(n log n)", "O(1)",
                    new String[]{
                        "build max heap",
                        "for end = n-1 downto 1",
                        "  swap a[0], a[end]",
                        "  heapify(0, end)",
                        "  compare children"});
            case "Linear Search":
                return new AlgorithmInfo("Linear Search",
                    "Checks each element one by one from the start until the target is found or the list ends. Works on any array, sorted or not.",
                    "O(1)", "O(n)", "O(n)", "O(1)",
                    new String[]{
                        "for i = 0 to n-1",
                        "  if a[i] == target",
                        "    return i",
                        "return NOT FOUND"});
            case "Binary Search":
                return new AlgorithmInfo("Binary Search",
                    "On a sorted array, repeatedly halves the search range by comparing the target with the middle element. Very fast, but needs sorted data.",
                    "O(1)", "O(log n)", "O(log n)", "O(1)",
                    new String[]{
                        "lo = 0, hi = n-1",
                        "while lo <= hi",
                        "  mid = (lo+hi)/2",
                        "  if a[mid] == target: return mid",
                        "  else if a[mid] < target: lo = mid+1",
                        "  else: hi = mid-1"});
            case "BFS":
                return new AlgorithmInfo("Breadth First Search",
                    "Explores a graph level by level using a queue: visits all neighbours of a node before moving to the next level. Finds shortest paths in unweighted graphs.",
                    "O(V+E)", "O(V+E)", "O(V+E)", "O(V)",
                    new String[]{
                        "queue = [start]",
                        "while queue not empty",
                        "  node = queue.dequeue()",
                        "  visit(node)",
                        "  enqueue unvisited neighbours"});
            case "DFS":
                return new AlgorithmInfo("Depth First Search",
                    "Explores a graph by going as deep as possible along each branch before backtracking, using recursion (an implicit stack).",
                    "O(V+E)", "O(V+E)", "O(V+E)", "O(V)",
                    new String[]{
                        "visit(node)",
                        "mark node visited",
                        "for each neighbour",
                        "  if not visited",
                        "    DFS(neighbour)"});
            default:
                return new AlgorithmInfo(key, "No description available.",
                    "-", "-", "-", "-", new String[]{"-"});
        }
    }
}

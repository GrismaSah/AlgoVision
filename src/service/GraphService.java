package service;

import model.AlgorithmStep;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

/**
 * A small fixed graph plus BFS and DFS traversals recorded as steps.
 * Node positions (for drawing) are relative coordinates in 0..1.
 */
public class GraphService {

    public static final int NODES = 7;

    // Undirected edges as pairs of node ids.
    public static final int[][] EDGES = {
        {0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {2, 6}, {3, 4}, {5, 6}
    };

    // Relative positions [x, y] in 0..1 for each node.
    public static final double[][] POS = {
        {0.50, 0.12},   // 0
        {0.28, 0.40},   // 1
        {0.72, 0.40},   // 2
        {0.15, 0.72},   // 3
        {0.40, 0.72},   // 4
        {0.60, 0.72},   // 5
        {0.85, 0.72}    // 6
    };

    private static List<List<Integer>> adjacency() {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < NODES; i++) adj.add(new ArrayList<>());
        for (int[] e : EDGES) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        for (List<Integer> list : adj) list.sort(Integer::compareTo);
        return adj;
    }

    public static List<AlgorithmStep> run(String algorithm, int start) {
        return "DFS".equals(algorithm) ? dfs(start) : bfs(start);
    }

    public static List<AlgorithmStep> bfs(int start) {
        List<AlgorithmStep> s = new ArrayList<>();
        List<List<Integer>> adj = adjacency();
        boolean[] visited = new boolean[NODES];
        Queue<Integer> queue = new LinkedList<>();
        visited[start] = true;
        queue.add(start);
        s.add(AlgorithmStep.frontier(start, 0, "Enqueue start node " + start));
        while (!queue.isEmpty()) {
            int node = queue.poll();
            s.add(AlgorithmStep.node(node, 2, "Visit node " + node));
            for (int nb : adj.get(node)) {
                if (!visited[nb]) {
                    visited[nb] = true;
                    queue.add(nb);
                    s.add(AlgorithmStep.frontier(nb, 4, "Discover node " + nb + " (enqueue)"));
                }
            }
        }
        s.add(AlgorithmStep.done("BFS complete \u2713"));
        return s;
    }

    public static List<AlgorithmStep> dfs(int start) {
        List<AlgorithmStep> s = new ArrayList<>();
        List<List<Integer>> adj = adjacency();
        boolean[] visited = new boolean[NODES];
        dfsVisit(start, adj, visited, s);
        s.add(AlgorithmStep.done("DFS complete \u2713"));
        return s;
    }
    private static void dfsVisit(int node, List<List<Integer>> adj, boolean[] visited, List<AlgorithmStep> s) {
        visited[node] = true;
        s.add(AlgorithmStep.node(node, 0, "Visit node " + node));
        for (int nb : adj.get(node)) {
            if (!visited[nb]) {
                s.add(AlgorithmStep.frontier(nb, 3, "Go deeper to node " + nb));
                dfsVisit(nb, adj, visited, s);
            }
        }
    }
}

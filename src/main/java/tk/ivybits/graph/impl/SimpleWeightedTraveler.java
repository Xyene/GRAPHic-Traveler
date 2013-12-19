package tk.ivybits.graph.impl;

import tk.ivybits.graph.Graph;

import java.awt.*;
import java.util.*;

import static tk.ivybits.graph.Graph.NODE_NONE;
import static tk.ivybits.graph.impl.Node.pack;

public abstract class SimpleWeightedTraveler implements GraphExplorer {
    @Override
    public Node traverse(Graph graph, Point src, Point dst, boolean diagonal, GraphObserver observer) {
        double[][] D = new double[graph.width()][graph.height()];
        HashSet<Long> V = new HashSet<>();
        for (double[] row : D)
            Arrays.fill(row, Double.MAX_VALUE);

        Node sourceNode = new Node(pack(src.x, src.y), null);
        PriorityQueue<Node> Q = new PriorityQueue<>(100, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                double r = o1.weight - o2.weight;
                return r < 0 ? -1 : (r > 0 ? 1 : -1);
            }
        });
        Q.add(sourceNode);
        D[src.x][src.y] = 0;

        while (!Q.isEmpty()) {
            Node next = Q.remove();
            V.add(next.location);
            if (observer != null) observer.traversed(next);

            if (next.x == dst.x && next.y == dst.y) {
                return next;
            }

            int x = next.x, y = next.y;

            if (diagonal) {
                expand(graph, Q, V, D, dst, true, x + 1, y - 1, next, observer);
                expand(graph, Q, V, D, dst, true, x - 1, y + 1, next, observer);
                expand(graph, Q, V, D, dst, true, x - 1, y - 1, next, observer);
                expand(graph, Q, V, D, dst, true, x + 1, y + 1, next, observer);
            }
            expand(graph, Q, V, D, dst, false, x + 1, y, next, observer);
            expand(graph, Q, V, D, dst, false, x - 1, y, next, observer);
            expand(graph, Q, V, D, dst, false, x, y + 1, next, observer);
            expand(graph, Q, V, D, dst, false, x, y - 1, next, observer);
        }

        return null;
    }

    protected void expand(Graph g, PriorityQueue<Node> Q, HashSet<Long> V, double[][] D, Point end, boolean diagonal, int x, int y, Node last, GraphObserver observer) {
        long dst = pack(x, y);
        double cost = D[last.x][last.y] + cost(x, y, end, diagonal);
        if (g.inBounds(x, y)
                && !V.contains(dst)
                && g.asMatrix()[x][y] == NODE_NONE
                && cost < D[x][y]) {
            D[x][y] = cost;
            Node n = new Node(dst, last, cost);
            Q.add(n);
            if (observer != null) observer.prospected(n);
        }
    }

    protected abstract double cost(int x, int y, Point end, boolean diagonal);
}

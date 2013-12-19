package tk.ivybits.graph.impl;

import tk.ivybits.graph.Graph;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;

import static tk.ivybits.graph.Graph.NODE_NONE;
import static tk.ivybits.graph.impl.Node.pack;

/**
 * Base class for both DFS and BFS.
 */
public abstract class SimpleTraveler implements GraphExplorer {

    protected abstract Node take(LinkedList<Node> nodes);

    @Override
    public Node traverse(Graph graph, Point src, Point dst, boolean diagonal, GraphObserver observer) {
        LinkedList<Node> Q = new LinkedList<>();
        HashSet<Long> V = new HashSet<>();

        Node n = new Node(pack(src.x, src.y), null);
        Q.add(n);
        V.add(n.location);
        while (!Q.isEmpty()) {
            Node next = take(Q);

            if (observer != null) observer.traversed(next);

            if (next.x == dst.x && next.y == dst.y) {
                return next;
            }

            int x = next.x;
            int y = next.y;
            if (diagonal) {
                expand(graph, Q, V, x + 1, y - 1, next, observer);
                expand(graph, Q, V, x - 1, y + 1, next, observer);
                expand(graph, Q, V, x - 1, y - 1, next, observer);
                expand(graph, Q, V, x + 1, y + 1, next, observer);
            }
            expand(graph, Q, V, x + 1, y, next, observer);
            expand(graph, Q, V, x - 1, y, next, observer);
            expand(graph, Q, V, x, y + 1, next, observer);
            expand(graph, Q, V, x, y - 1, next, observer);
        }
        return null;
    }

    private void expand(Graph graph, LinkedList<Node> Q, HashSet<Long> V, int x, int y, Node last, GraphObserver observer) {
        if (graph.inBounds(x, y)) {
            long location = pack(x, y);
            if (V.contains(location)) {
                return;
            }
            if (graph.asMatrix()[x][y] == NODE_NONE) {
                Node n = new Node(location, last);
                Q.add(n);
                if (observer != null) observer.prospected(n);
                V.add(location);
            }
        }
    }
}

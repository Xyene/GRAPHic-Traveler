package tk.ivybits.graph;

import tk.ivybits.graph.impl.*;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;

public enum Explorer implements GraphExplorer {
    BFS("BFS", new SimpleTraveler() {
        @Override
        protected Node take(LinkedList<Node> nodes) {
            return nodes.remove();
        }
    }),
    DFS("DFS", new SimpleTraveler() {
        @Override
        protected Node take(LinkedList<Node> nodes) {
            return nodes.removeLast();
        }
    }),
    DIJKSTRA("Dijkstra's", new SimpleWeightedTraveler() {
        @Override
        protected double cost(int x, int y, Point end, boolean diagonal) {
            return diagonal ? 1.41421 : 1.0;
        }
    }),
    ASTAR("A*", new SimpleWeightedTraveler() {
        @Override
        protected double cost(int x, int y, Point end, boolean diagonal) {
            double dx = x - end.x;
            double dy = y - end.y;
            return (dx * dx) + (dy * dy);
        }
    });
    private final String display;
    private final GraphExplorer explorer;

    Explorer(String display, GraphExplorer explorer) {
        this.display = display;
        this.explorer = explorer;
    }

    public static Explorer byId(String id) {
        for (Explorer e : values()) {
            if (e.display.equals(id))
                return e;
        }
        return null;
    }

    public String id() {
        return display;
    }

    @Override
    public Node traverse(Graph graph, Point src, Point dst, boolean diagonal, GraphObserver observer) {
        return explorer.traverse(graph, src, dst, diagonal, observer);
    }
}

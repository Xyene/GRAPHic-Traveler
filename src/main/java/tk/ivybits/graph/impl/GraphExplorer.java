package tk.ivybits.graph.impl;

import tk.ivybits.graph.Graph;

import java.awt.*;

public interface GraphExplorer {
    Node traverse(Graph graph, Point src, Point dst, boolean diagonal, GraphObserver observer);
}

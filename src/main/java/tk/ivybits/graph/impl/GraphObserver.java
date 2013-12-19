package tk.ivybits.graph.impl;

public interface GraphObserver {
    void traversed(Node n);

    void prospected(Node n);
}

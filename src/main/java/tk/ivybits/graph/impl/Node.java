package tk.ivybits.graph.impl;

public class Node {
    final long location;
    public final int x, y;
    public final Node last;
    public double weight;

    public Node(long location, Node last) {
        this(location, last, 1D);
    }

    Node(long location, Node last, double weight) {
        this.location = location;
        x = x(location);
        y = y(location);
        this.last = last;
        this.weight = weight;
    }

    static int x(long packed) {
        return (int) (packed >> 32);
    }

    static int y(long packed) {
        return (int) packed;
    }

    static long pack(int x, int y) {
        return ((long) x << 32) | ((long) y & 0xFFFFFFFL);
    }

    @Override
    public String toString() {
        return "Node{" + x + ", " + y + " -> " + weight + "}";
    }
}

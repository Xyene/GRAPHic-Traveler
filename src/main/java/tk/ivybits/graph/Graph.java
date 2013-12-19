package tk.ivybits.graph;

public class Graph {
    public static final byte NODE_NONE = 0, NODE_FLAGGED = 1;
    private final int width;
    private final int height;
    private byte[][] matrix;

    public Graph(int width, int height) {
        this.width = width;
        this.height = height;
        reset();
    }

    public byte[][] asMatrix() {
        return matrix;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public void reset() {
        matrix = new byte[width][height];
    }
}

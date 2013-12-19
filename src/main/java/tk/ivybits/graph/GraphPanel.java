package tk.ivybits.graph;

import tk.ivybits.graph.impl.GraphObserver;
import tk.ivybits.graph.impl.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class GraphPanel extends JPanel {
    private Graph graph;
    private int CELL_SIZE = 15;
    private byte mode = MODE_WALL;
    private Point start, end;
    private Explorer algorithm;
    private Node backtrack;
    private final ArrayList<Node> travelled = new ArrayList<>(), prospect = new ArrayList<>();
    public static final byte MODE_WALL = 0, MODE_SOURCE = 1, MODE_DESTINATION = 2;
    private boolean diagonal;
    private TraversalAnimatorThread animator;

    private class TraversalAnimatorThread extends Thread {
        private int t = 0, p = 0;
        private final Timer refresh = new Timer(1000 / 60, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paintImmediately(getBounds());
            }
        });

        public TraversalAnimatorThread() {
            super("Graph Traversal Animator");
        }

        public void run() {
            travelled.clear();
            prospect.clear();
            repaint();
            if (animator != null) {
                animator.end();
            }
            animator = this;
            refresh.start();
            backtrack = algorithm.traverse(graph, start, end, diagonal, new GraphObserver() {
                @Override
                public void traversed(Node n) {
                    t++;
                    synchronized (prospect) {
                        prospect.remove(n);
                    }
                    synchronized (travelled) {
                        travelled.add(n);
                    }
                    if (t > 10) {
                        t = 0;
                        try {
                            Thread.sleep(35);
                        } catch (InterruptedException e) {
                        }
                    }
                }

                @Override
                public void prospected(Node n) {
                    p++;
                    synchronized (prospect) {
                        prospect.add(n);
                    }
                    if (p > 5) {
                        p = 0;
                    }
                }
            });
            end();
        }

        public void end() {
            refresh.stop();
            animator = null;
            paintImmediately(getBounds());
            try {
                stop();
            } catch (Exception ignored) {
            }
        }
    }

    public GraphPanel(int w, int h, Explorer e) {
        graph = new Graph(w, h);
        algorithm = e;
        Dimension size = new Dimension(w * CELL_SIZE, h * CELL_SIZE);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        MouseAdapter m = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                doCell(e);
            }

            private void doCell(MouseEvent e) {
                int x = e.getX() / CELL_SIZE;
                int y = e.getY() / CELL_SIZE;
                boolean dirty = false;
                if (graph.inBounds(x, y)) {
                    if (animator != null) {
                        animator.end();
                        repaint();
                    }
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        switch (mode) {
                            case MODE_WALL:
                                if (graph.asMatrix()[x][y] != Graph.NODE_FLAGGED
                                        && (end == null || (end.x != x || end.y != y))
                                        && (start == null || (start.x != x || start.y != y))) {
                                    graph.asMatrix()[x][y] = Graph.NODE_FLAGGED;
                                    dirty = true;
                                    walls = null;
                                }
                                break;
                            case MODE_SOURCE:
                                start = new Point(x, y);
                                dirty = true;
                                break;
                            case MODE_DESTINATION:
                                end = new Point(x, y);
                                dirty = true;
                                break;
                        }
                    } else {
                        graph.asMatrix()[x][y] = Graph.NODE_NONE;
                        if (end != null && end.x == x && end.y == y) end = null;
                        if (start != null && start.x == x && start.y == y) start = null;
                        dirty = true;
                    }
                }
                if (dirty) {
                    traverse(false);
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                doCell(e);
            }
        };
        addMouseListener(m);
        addMouseMotionListener(m);
    }

    public Graph graph() {
        return graph;
    }

    public void start(Point point) {
        this.start = point;
        graph.asMatrix()[point.x][point.y] = Graph.NODE_NONE;
    }

    public void end(Point point) {
        this.end = point;
        graph.asMatrix()[point.x][point.y] = Graph.NODE_NONE;
    }


    public void exploreWith(Explorer algorithm) {
        this.algorithm = algorithm;
    }

    public void traverse(boolean animated) {
        if (animator != null) animator.end();
        travelled.clear();
        prospect.clear();
        backtrack = null;
        if (start != null && end != null) {
            if (!animated) {
                backtrack = algorithm.traverse(graph, start, end, diagonal, null);
            } else {
                new TraversalAnimatorThread().start();
            }
        }
        repaint();
    }

    private void paintNodes(Graphics g, List<Node> nodes, int depth, Color color, int size) {
        g.setColor(color);
        ((Graphics2D) g).setStroke(new BasicStroke(size, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
        synchronized (nodes) {
            HashSet<Node> drawn = new HashSet<>(nodes.size());
            GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
            int c = CELL_SIZE / 2;

            for (Node t : nodes) {
                Node l = t;
                while ((l = l.last) != null) {
                    if (drawn.add(l) && l.last != null) {
                        path.moveTo(l.x * CELL_SIZE + c, l.y * CELL_SIZE + c);
                        path.lineTo(l.last.x * CELL_SIZE + c, l.last.y * CELL_SIZE + c);
                    }
                    depth--;
                    if (depth == 0) {
                        break;
                    }
                }
            }
            ((Graphics2D) g).draw(path);
        }
    }

    private BufferedImage walls;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = graph.width();
        int height = graph.height();

        g.setColor(Color.BLACK);
        byte[][] matrix = graph.asMatrix();

        for (int x = 0; x != width; x++)
            for (int y = 0; y != height; y++)
                if (matrix[x][y] == Graph.NODE_FLAGGED)
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        paintNodes(g, prospect, 1, new Color(255, 230, 0), 3);
        paintNodes(g, travelled, travelled.size(), new Color(145, 202, 255), 3);

        if (backtrack != null)
            paintNodes(g, Arrays.asList(backtrack), -1, new Color(0, 180, 255), 5);

        if (start != null) {
            g.setColor(Color.GREEN);
            g.fillRect(start.x * CELL_SIZE, start.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        if (end != null) {
            g.setColor(Color.RED);
            g.fillRect(end.x * CELL_SIZE, end.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        g.setColor(Color.LIGHT_GRAY);
        ((Graphics2D) g).setStroke(new BasicStroke(1));
        for (int y = 0; y != height; y++) {
            g.drawLine(0, y * CELL_SIZE, width * CELL_SIZE, y * CELL_SIZE);
        }
        for (int x = 0; x != width; x++) {
            g.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, height * CELL_SIZE);
        }
        g.setColor(Color.DARK_GRAY);
        ((Graphics2D) g).setStroke(new BasicStroke(4));
        g.drawRect(0, 0, width * CELL_SIZE, height * CELL_SIZE);
    }

    public int cellSize() {
        return CELL_SIZE;
    }

    public void mode(byte c) {
        mode = c;
    }

    public void reset() {
        if (animator != null) animator.end();
        start = end = null;
        backtrack = null;
        travelled.clear();
        prospect.clear();
        graph.reset();
        repaint();
    }

    public boolean isDiagonal() {
        return diagonal;
    }

    public void diagonalPaths(boolean b) {
        diagonal = b;
        traverse(false);
        repaint();
    }
}

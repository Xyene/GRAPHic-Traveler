package tk.ivybits.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Main {
    public static void main(String... argv) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        System.setProperty("sun.java2d.opengl", "True");
        int w = 70;
        int h = 40;
        final GraphPanel panel = new GraphPanel(w, h, Explorer.values()[0]);

        panel.setBackground(Color.WHITE);
        JPanel cellButtons = new JPanel(new FlowLayout());
        cellButtons.setBorder(BorderFactory.createTitledBorder("Cell Draw Mode"));
        JRadioButton wall = new JRadioButton("Wall", true);
        JRadioButton source = new JRadioButton("Source");
        JRadioButton target = new JRadioButton("Target");
        ButtonGroup bg = new ButtonGroup();
        bg.add(wall);
        bg.add(source);
        bg.add(target);
        ((JRadioButton) cellButtons.add(wall)).addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.mode(GraphPanel.MODE_WALL);
            }
        });
        ((JRadioButton) cellButtons.add(source)).addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.mode(GraphPanel.MODE_SOURCE);
            }
        });
        ((JRadioButton) cellButtons.add(target)).addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.mode(GraphPanel.MODE_DESTINATION);
            }
        });
        JPanel algorithmBox = new JPanel(new FlowLayout());
        final JComboBox<String> algorithms = new JComboBox<>();
        for (Explorer e : Explorer.values())
            algorithms.addItem(e.id());
        algorithms.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                panel.exploreWith(Explorer.byId((String) e.getItem()));
                panel.traverse(false);
            }
        });
        algorithmBox.add(new JLabel("Algorithm:"));
        algorithmBox.add(algorithms);
        algorithmBox.setBorder(BorderFactory.createTitledBorder("Graph"));

        ((JCheckBox) algorithmBox.add(new JCheckBox("Diagonal Paths"))).addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.diagonalPaths(!panel.isDiagonal());
            }
        });

        ((JButton) algorithmBox.add(new JButton("Animated Traversal"))).addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.traverse(true);
            }
        });
        ((JButton) algorithmBox.add(new JButton("Random"))).addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.reset();
                byte[][] matrix = panel.graph().asMatrix();
                for (int x = 0; x != matrix.length; x++)
                    for (int y = 0; y != matrix[x].length; y++)
                        if (Math.random() > .70)
                            matrix[x][y] = Graph.NODE_FLAGGED;

                Random rng = new Random();
                panel.start(new Point(rng.nextInt(matrix.length), rng.nextInt(matrix[0].length)));
                panel.end(new Point(rng.nextInt(matrix.length), rng.nextInt(matrix[0].length)));
                panel.traverse(false);
                panel.repaint();
            }
        });
        ((JButton) algorithmBox.add(new JButton("Clear"))).addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.reset();
            }
        });
        JPanel controls = new JPanel(new GridBagLayout());
        controls.add(cellButtons);
        controls.add(algorithmBox);

        JFrame jf = new JFrame("Graphs!");
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "to-clipboard");
        panel.getActionMap().put("to-clipboard", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                BufferedImage dst = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                panel.paintComponent(dst.createGraphics());
                Swing.toClipboard(dst);
            }
        });

        jf.setLayout(new BorderLayout());
        jf.add(BorderLayout.NORTH, new JScrollPane(panel));
        jf.add(BorderLayout.WEST, controls);
        jf.pack();

        jf.setLocationRelativeTo(null);
        jf.setResizable(false);
        jf.pack();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
}

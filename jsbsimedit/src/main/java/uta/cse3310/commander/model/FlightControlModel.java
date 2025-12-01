package uta.cse3310.commander.model;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class FlightControlModel {

    // ---- Node types shown in the palette ----
    public enum NodeType {
        SOURCE("Source", 0, 1),
        DESTINATION("Destination", 1, 0),
        SUMMER("Summer", 2, 1),
        PID("PID", 1, 1),
        GAIN("Gain", 1, 1),
        FILTER("Filter", 1, 1),
        DEAD_BAND("Dead Band", 1, 1),
        SWITCH("Switch", 2, 1),
        KINEMAT("Kinemat", 1, 1),
        FCSFUNCTION("FCSFunction", 1, 1);

        public final String label;
        public final int inPorts;
        public final int outPorts;

        NodeType(String label, int inPorts, int outPorts) {
            this.label = label;
            this.inPorts = inPorts;
            this.outPorts = outPorts;
        }

        @Override
        public String toString() {
            return label;
        }

        public static NodeType fromLabel(String s) {
            for (NodeType t : values()) {
                if (t.label.equalsIgnoreCase(s))
                    return t;
            }
            return GAIN;
        }
    }

    // ---- Graph primitives ----
    public static final class Node {
        public final int id;
        public NodeType type;
        public Rectangle bounds; // x,y,width,height
        public ImageIcon icon;

        public Node(int id, NodeType type, int x, int y) {
            this.id = id;
            this.type = type;
            this.bounds = new Rectangle(x, y, 80, 80);
        }

        // Multiple inputs on the left edge, single output on right edge
        public Point inputPort(int index) {
            int n = Math.max(1, type.inPorts); // always at least 1
            if (index < 0) {
                index = 0;
            } else if (index >= n) {
                index = n - 1;
            }

            // Distribute ports vertically along the node height
            double slot = (index + 1) / (double) (n + 1); // normalize inputs from 0 to 1 to calc % of node height
            int cy = (int) Math.round(bounds.y + slot * bounds.height);
            int cx = bounds.x;
            return new Point(cx, cy);
        }

        // // For single-input cases (old method)
        // public Point inputPort() {
        // return inputPort(0);
        // }

        public Point outputPort() {
            return new Point(bounds.x + bounds.width, bounds.y + bounds.height / 2);
        }

        // Hit rectangles for ports
        public Rectangle inputPortRect(int index, int size) {
            Point p = inputPort(index);
            return new Rectangle(p.x - size / 2, p.y - size / 2, size, size);
        }

        // For single-input cases (old method)
        public Rectangle inputPortRect(int size) {
            return inputPortRect(0, size);
        }

        public Rectangle outputPortRect(int size) {
            Point p = outputPort();
            return new Rectangle(p.x - size / 2, p.y - size / 2, size, size);
        }
    }

    public static final class Edge {
        public final Node from;
        public final Node to;
        public final int toInputIndex; // which input port on Node "to" this edge feeds

        public Point fromPoint;
        public Point toPoint;

        public Edge(Node from, Node to, int toInputIndex) {
            this.from = from;
            this.to = to;
            this.toInputIndex = toInputIndex;
            updatePoints();
        }

        // Recalculate attachment points if nodes move
        public void updatePoints() {
            Point fromAttach = from.outputPort();
            Point toAttach = to.inputPort(toInputIndex);

            if (fromPoint == null) {
                fromPoint = new Point(fromAttach);
            } else {
                fromPoint.setLocation(fromAttach);
            }

            if (toPoint == null) {
                toPoint = new Point(toAttach);
            } else {
                toPoint.setLocation(toAttach);
            }
        }
    }

    // ---- The graph model ----
    private int nextId = 1;
    public final List<Node> nodes = new ArrayList<>();
    public final List<Edge> edges = new ArrayList<>();

    public Node addNode(NodeType type, int x, int y) {
        Node n = new Node(nextId++, type, x, y);
        nodes.add(n);
        return n;
    }

    // Default to the first input port on the destination node
    public void addEdge(Node from, Node to) {
        addEdge(from, to, 0);
    }

    public void addEdge(Node from, Node to, int toInputIndex) {
        if (from != null && to != null && from != to) {
            edges.add(new Edge(from, to, toInputIndex));
        }
    }
}

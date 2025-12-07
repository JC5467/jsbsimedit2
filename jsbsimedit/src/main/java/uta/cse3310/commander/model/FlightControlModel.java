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
        @Override public String toString() { return label; }

        public static NodeType fromLabel(String s) {
            for (NodeType t : values()) {
                if (t.label.equalsIgnoreCase(s)) return t;
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

        public String displayName;
        public Object backingBlock;

        // How many distinct input ports this node currently uses.
        public int inputPortCount = 0;

        public Node(int id, NodeType type, int x, int y) {
            this.id = id;
            this.type = type;
            this.bounds = new Rectangle(x, y, 80, 80);
            this.displayName = type.label;
        }

        // Input port at a specific index (0-based).
        // Ports are evenly distributed along the left edge.
        public Point inputPort(int index) {
            int n = Math.max(1, inputPortCount);
            if (index < 0) index = 0;
            if (index >= n) index = n - 1;

            double slot = (index + 1) / (double) (n + 1); // 1/(n+1), 2/(n+1), ...
            int y = (int) Math.round(bounds.y + slot * bounds.height);
            int x = bounds.x;
            return new Point(x, y);
        }

        public Point outputPort() {
            return new Point(bounds.x + bounds.width, bounds.y + bounds.height / 2);
        }

        // Hit rectangles for ports
        public Rectangle inputPortRect(int index, int size) {
            Point p = inputPort(index);
            return new Rectangle(p.x - size / 2, p.y - size / 2, size, size);
        }

        public Rectangle inputPortRect(int size) {
            return inputPortRect(0, size);
        }

        public Rectangle outputPortRect(int size) {
            Point p = outputPort();
            return new Rectangle(p.x - size / 2, p.y - size / 2, size, size);
        }
    }

    public static final class Edge {
        public final Node from; // uses output port
        public final Node to;   // uses one of the input ports

        // Which input index of 'to' this edge is attached to (0-based)
        public final int toInputIndex;

        public Point fromPoint;
        public Point toPoint;

        public Edge(Node from, Node to, int toInputIndex) {
            this.from = from;
            this.to = to;
            this.toInputIndex = Math.max(0, toInputIndex);
            updatePoints();
        }

        public void updatePoints() {
            // Always recompute based on current node bounds & input count
            fromPoint = from.outputPort();

            int n = Math.max(1, to.inputPortCount);
            int idx = toInputIndex;
            if (idx < 0) idx = 0;
            if (idx >= n) idx = n - 1;

            toPoint = to.inputPort(idx);
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

    // Default edge - attach to the next free input index on the target
    public void addEdge(Node from, Node to) {
        if (from != null && to != null && from != to) {
            int inputIndex = 0;
            for (Edge e : edges) {
                if (e.to == to) {
                    inputIndex++;  // how many edges already go into this node
                }
            }
            addEdge(from, to, inputIndex);
        }
    }

    // Overload that lets callers choose the target input index
    public void addEdge(Node from, Node to, int toInputIndex) {
        if (from != null && to != null && from != to) {
            Edge e = new Edge(from, to, toInputIndex);
            edges.add(e);

            int needed = toInputIndex + 1;
            if (needed > to.inputPortCount) {
                to.inputPortCount = needed; // node updated with how many inputs it has
            }
        }
    }
}

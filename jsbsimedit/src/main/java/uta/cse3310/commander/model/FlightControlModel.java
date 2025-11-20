package uta.cse3310.commander.model;

import java.awt.Image;
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

        public Node(int id, NodeType type, int x, int y) {
            this.id = id;
            this.type = type;
            this.bounds = new Rectangle(x, y, 80, 80);
        }

        // Single input on left-center; single output on right-center (prototype simplicity)
        public Point inputPort() {
            return new Point(bounds.x, bounds.y + bounds.height / 2);
        }
        public Point outputPort() {
            return new Point(bounds.x + bounds.width, bounds.y + bounds.height / 2);
        }

        // Hit rectangles for ports
        public Rectangle inputPortRect(int size) {
            Point p = inputPort();
            return new Rectangle(p.x - size/2, p.y - size/2, size, size);
        }
        public Rectangle outputPortRect(int size) {
            Point p = outputPort();
            return new Rectangle(p.x - size/2, p.y - size/2, size, size);
        }
    }

    public static final class Edge {
        public final Node from; // uses output port
        public final Node to;   // uses input port

        private final int fromRelX, fromRelY;
        private final int toRelX, toRelY;

        public Point fromPoint;
        public Point toPoint;

        public Edge(Node from, Node to, Point fromAttach, Point toAttach) {
            this.from = from;
            this.to = to;
            
            this.fromRelX = fromAttach.x - from.bounds.x;
            this.fromRelY = fromAttach.y - from.bounds.y;
            this.toRelX = toAttach.x - to.bounds.x;
            this.toRelY = toAttach.y - to.bounds.y;

            this.fromPoint = new Point(fromAttach);
            this.toPoint = new Point(toAttach);

        }

        public void updatePoints() {
            fromPoint.setLocation(from.bounds.x + fromRelX, from.bounds.y + fromRelY);
            toPoint.setLocation(to.bounds.x + toRelX, to.bounds.y + toRelY);
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
    public void addEdge(Node from, Node to) {
        if (from != null && to != null && from != to) {
            Point fromAttach = from.outputPort();
            Point toAttach = to.inputPort();
            edges.add(new Edge(from, to, fromAttach, toAttach));
        }
    }

    
}

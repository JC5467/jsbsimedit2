package uta.cse3310.tab.concreteTabs.flightcontrol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import uta.cse3310.commander.controller.FlightControlController;
import uta.cse3310.commander.model.FlightControlModel;

public class FlightControlView extends JComponent {
    private final FlightControlModel model;

    // Visual constants
    public static final int PORT_SIZE = 12;
    public static final Stroke EDGE_STROKE = new BasicStroke(1f);
    public static final Stroke PREVIEW_STROKE = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
            new float[] { 8f, 8f }, 0f);

    // Connection preview (set by controller)
    private FlightControlModel.Node previewFrom;
    private Point previewTo;

    public FlightControlView(FlightControlModel model) {
        this.model = Objects.requireNonNull(model);
        setOpaque(true);
        setBackground(new Color(0xFFFFFF));
        setPreferredSize(new Dimension(2560, 1440));
        setDoubleBuffered(true);
    }

    // Controller calls these during a drag-connection interaction
    public void setConnectionPreview(FlightControlModel.Node from, Point to) {
        this.previewFrom = from;
        this.previewTo = to;
        repaint();
    }

    public void clearConnectionPreview() {
        this.previewFrom = null;
        this.previewTo = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // FORCE a full clear each repaint
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        paintEdges(g2);
        paintNodes(g2);
        paintPreview(g2);

        g2.dispose();
    }

    private void paintNodes(Graphics2D g2) {
        // Only show port rectangles while the user is dragging a new connection.
        boolean showPorts = (previewFrom != null);

        for (FlightControlModel.Node n : model.nodes) {
            // Get the icon for this node type
            ImageIcon icon = FlightControlController.ICONS.get(n.type);

            if (icon != null) {
                // Draw icon
                g2.drawImage(
                        icon.getImage(),
                        n.bounds.x,
                        n.bounds.y,
                        n.bounds.width,
                        n.bounds.height,
                        null);
            } else {
                // Fallback if no icon is found
                // Body
                g2.setColor(new Color(255, 255, 255));
                g2.fillRoundRect(n.bounds.x, n.bounds.y, n.bounds.width, n.bounds.height, 0, 0);
                g2.setColor(new Color(0, 0, 0));
                g2.drawRoundRect(n.bounds.x, n.bounds.y, n.bounds.width, n.bounds.height, 0, 0);

                // Title
                g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
                FontMetrics fm = g2.getFontMetrics();
                String title = n.type.label;
                int tx = n.bounds.x + 10;
                int ty = n.bounds.y + fm.getAscent() + 8;
                g2.drawString(title, tx, ty);
            }

            // Draw node name under the node
            if (n.displayName != null && !n.displayName.isEmpty()) {
                g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
                FontMetrics fm = g2.getFontMetrics();
                String label = n.displayName;

                int textWidth = fm.stringWidth(label);
                int tx = n.bounds.x + (n.bounds.width - textWidth) / 2;
                int ty = n.bounds.y + n.bounds.height + fm.getAscent() + 2;

                g2.setColor(Color.BLACK);
                g2.drawString(label, tx, ty);
            }

            if (showPorts) {
                // Draw ALL input ports for this node, based on NodeType.inPorts
                int inCount = n.type.inPorts;
                for (int i = 0; i < inCount; i++) {
                    Rectangle inR = n.inputPortRect(i, PORT_SIZE);
                    g2.setColor(new Color(90, 180, 255)); // blue inputs
                    g2.fillRect(inR.x, inR.y, inR.width, inR.height);
                    g2.setColor(Color.BLACK);
                    g2.drawRect(inR.x, inR.y, inR.width, inR.height);
                }

                if (n.type.outPorts > 0) {
                    Rectangle outR = n.outputPortRect(PORT_SIZE);
                    g2.setColor(new Color(255, 120, 120)); // red outputs
                    g2.fillRect(outR.x, outR.y, outR.width, outR.height);
                    g2.setColor(Color.BLACK);
                    g2.drawRect(outR.x, outR.y, outR.width, outR.height);
                }
            }
        }
    }

    private void paintEdges(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0));
        g2.setStroke(EDGE_STROKE);
        for (FlightControlModel.Edge e : model.edges) {
            drawOrth(g2, e.fromPoint, e.toPoint);
            drawArrowHead(g2, e.toPoint);
        }
    }

    private void paintPreview(Graphics2D g2) {
        if (previewFrom != null && previewTo != null) {
            g2.setColor(new Color(255, 180, 0));
            g2.setStroke(PREVIEW_STROKE);
            drawOrth(g2, previewFrom.outputPort(), previewTo);
            drawArrowHead(g2, previewTo);
        }
    }

    // Orthogonal polyline using midpoint rule
    private void drawOrth(Graphics2D g2, Point a, Point b) {
        double xmid = a.x + (b.x - a.x) / 2;
        double ymid = a.y + (b.y - a.y) / 2;
        int buffer = 20;
        double x1, x2, y1;
        x1 = a.x + buffer;

        // Edge routing logic - units are in pixels
        if ((a.x < b.x + 80) && (a.x > b.x - 20)) {
            x2 = a.x - 100;
        } else {
            x2 = b.x - buffer;
        }
        if (a.y > b.y) {
            y1 = a.y + (buffer * 3);
            if (a.y > b.y + 100) {
                y1 = ymid;
                if (a.x > b.x - 20) {
                    x2 = b.x - buffer;
                }
            }
        } else {
            y1 = a.y - (buffer * 3);
            if (a.y < b.y - 100) {
                y1 = ymid;
                if (a.x > b.x - 20) {
                    x2 = b.x - buffer;
                }
            }
        }

        // Draw the path behind the nodes if they are too close
        Path2D path = new Path2D.Double();
        path.moveTo(a.x, a.y);
        // Only reroute for area around and behind fromPoint node
        if ((a.x > b.x - 20) && !((a.x < b.x + 85) && ((a.y < (b.y + 42)) && (a.y > (b.y - 42))))) {
            path.lineTo(x1, a.y);
            path.lineTo(x1, y1);
            path.lineTo(x2, y1);
            path.lineTo(x2, b.y);
            path.lineTo(b.x, b.y);
        } else {
            path.lineTo(xmid, a.y);
            path.lineTo(xmid, b.y);
            path.lineTo(b.x, b.y);
        }
        g2.draw(path);
    }

    // Small helpers used by the controller
    public FlightControlModel.Node nodeAt(Point p) {
        for (int i = model.nodes.size() - 1; i >= 0; --i) { // top-most first
            FlightControlModel.Node n = model.nodes.get(i);
            if (n.bounds.contains(p))
                return n;
        }
        return null;
    }

    public FlightControlModel.Node nodeWithOutputAt(Point p) {
        for (int i = model.nodes.size() - 1; i >= 0; --i) {
            FlightControlModel.Node n = model.nodes.get(i);
            if (n.type.outPorts <= 0)
                continue;
            if (n.outputPortRect(PORT_SIZE * 2).contains(p))
                return n; // *2 to PORT_SIZE for easier selecting
        }
        return null;
    }

    // Hit-test for a specific input port, not just the node.
    public static final class InputHit {
        public final FlightControlModel.Node node;
        public final int portIndex;

        public InputHit(FlightControlModel.Node node, int portIndex) {
            this.node = node;
            this.portIndex = portIndex;
        }
    }

    public InputHit inputPortAt(Point p) {
        // Iterate front-to-back so we hit the visually top-most node first
        for (int ni = model.nodes.size() - 1; ni >= 0; --ni) {
            FlightControlModel.Node n = model.nodes.get(ni);
            int inCount = n.type.inPorts;
            for (int pi = 0; pi < inCount; ++pi) {
                if (n.inputPortRect(pi, PORT_SIZE).contains(p)) {
                    return new InputHit(n, pi);
                }
            }
        }
        return null;
    }

    private void drawArrowHead(Graphics2D g2, Point to) {
        double x, y;

        x = to.x - 10;
        y = to.y - 6;
        g2.drawLine(to.x, to.y, (int) x, (int) y);

        x = to.x - 10;
        y = to.y + 6;
        g2.drawLine(to.x, to.y, (int) x, (int) y);
    }
}

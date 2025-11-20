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
    public static final Stroke PREVIEW_STROKE =
        new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{8f, 8f}, 0f);

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
        for (FlightControlModel.Node n : model.nodes) {
            // Get the icon for this node type
            ImageIcon icon = FlightControlController.ICONS.get(n.type);

            if(icon != null) {
                // Draw icon
                g2.drawImage(
                    icon.getImage(),
                    n.bounds.x,
                    n.bounds.y,
                    n.bounds.width,
                    n.bounds.height,
                    null
                );
            }
            else{
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

            // Ports
            Rectangle inR = n.inputPortRect(PORT_SIZE);
            Rectangle outR = n.outputPortRect(PORT_SIZE);

            g2.setColor(new Color(90, 180, 255));
            g2.fillRect(inR.x, inR.y, inR.width, inR.height);
            g2.setColor(new Color(255, 120, 120));
            g2.fillRect(outR.x, outR.y, outR.width, outR.height);

            g2.setColor(new Color(255, 255, 255, 160));
            g2.drawRect(inR.x, inR.y, inR.width, inR.height);
            g2.drawRect(outR.x, outR.y, outR.width, outR.height);
        }
    }

    private void paintEdges(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0));
        g2.setStroke(EDGE_STROKE);
        for (FlightControlModel.Edge e : model.edges) {
            drawOrth(g2, e.fromPoint, e.toPoint);
            drawArrowHead(g2, e.fromPoint, e.toPoint);
        }
    }

    private void paintPreview(Graphics2D g2) {
        if (previewFrom != null && previewTo != null) {
            g2.setColor(new Color(255, 220, 120));
            g2.setStroke(PREVIEW_STROKE);
            drawOrth(g2, previewFrom.outputPort(), previewTo);
            drawArrowHead(g2, previewFrom.outputPort(), previewTo);
        }
    }

    // Orthogonal polyline using midpoint rule
    private void drawOrth(Graphics2D g2, Point a, Point b) {
        g2.drawLine(a.x, a.y, b.x, b.y);
    }

    // ---- Small helpers used by the controller ----
    public FlightControlModel.Node nodeAt(Point p) {
        for (int i = model.nodes.size() - 1; i >= 0; --i) { // top-most first
            FlightControlModel.Node n = model.nodes.get(i);
            if (n.bounds.contains(p)) return n;
        }
        return null;
    }
    public FlightControlModel.Node nodeWithOutputAt(Point p) {
        for (int i = model.nodes.size() - 1; i >= 0; --i) {
            FlightControlModel.Node n = model.nodes.get(i);
            if (n.outputPortRect(PORT_SIZE).contains(p)) return n;
        }
        return null;
    }
    public FlightControlModel.Node nodeWithInputAt(Point p) {
        for (int i = model.nodes.size() - 1; i >= 0; --i) {
            FlightControlModel.Node n = model.nodes.get(i);
            if (n.inputPortRect(PORT_SIZE).contains(p)) return n;
        }
        return null;
    }

    private void drawArrowHead(Graphics2D g2, Point from, Point to){
        double phi = Math.toRadians(20);
        int barb = 10;

        double dy = to.y - from.y;
        double dx = to.x - from.x;
        double theta = Math.atan2(dy, dx);

        double x, y, rho;

        rho = theta + phi;
        x = to.x - barb * Math.cos(rho);
        y = to.y - barb * Math.sin(rho);
        g2.drawLine(to.x, to.y, (int)x, (int)y);

        rho = theta - phi;
        x = to.x - barb * Math.cos(rho);
        y = to.y - barb * Math.sin(rho);
        g2.drawLine(to.x, to.y, (int)x, (int)y);

    }
}

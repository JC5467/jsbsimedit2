package uta.cse3310.commander.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.io.IOException; // New import for I/O Exception handling
import java.net.URL; // Required for getResource

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO; // NEW: For reliable image loading
import java.awt.image.BufferedImage; // NEW: To hold the image data

import javax.swing.JTable;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;


import uta.cse3310.commander.model.FlightControlModel;
import uta.cse3310.commander.model.FlightControlModel.NodeType;
import uta.cse3310.tab.concreteTabs.flightcontrol.FlightControlView;

public final class FlightControlController {
    public static void start(JPanel host) {
        FlightControlModel model = new FlightControlModel();
        FlightControlView view = new FlightControlView(model);

        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);

        JPanel palette = buildPalette(view);

        // Build into the *host* panel that belongs to the tab:
        host.setLayout(new BorderLayout());
        host.add(palette, BorderLayout.NORTH);

        attachMouseControllers(view, model);
        view.setTransferHandler(new CanvasDropHandler(model, view));
        host.add(scrollPane, BorderLayout.CENTER);
    }

    // --- NEW: Helper method for reliable image loading ---
    private static ImageIcon loadReliableImageIcon(URL url, String iconFile) {
        if (url == null) {
            // This happens if the file isn't found on the classpath
            System.err.println("Warning: Could not find classpath resource for icon: /assets/componentImg/" + iconFile);
            return null;
        }
        try {
            // Use ImageIO.read() to read the image into a buffer
            BufferedImage bufferedImage = ImageIO.read(url);
            if (bufferedImage != null) {
                // Return the ImageIcon created from the successfully loaded buffer
                return new ImageIcon(bufferedImage);
            }
        } catch (IOException e) {
            System.err.println("Error reading image file with ImageIO: " + url.toExternalForm());
            // This BMP format is unsupported
            e.printStackTrace();
        } 
        return null;
    }


    // ---------------- Palette ----------------
    private static JPanel buildPalette(FlightControlView view) {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
    p.setBackground(new Color(245, 245, 245));
    p.setBorder(new EmptyBorder(6, 8, 6, 8));

    // Map names to icon files
    String[][] items = {
        {"Source", "source.bmp"},
        {"Destination", "destination.bmp"},
        {"Summer", "summer.bmp"},
        {"PID", "pid.bmp"},
        {"Gain", "gain.bmp"},
        {"Filter", "filter.bmp"},
        {"Dead Band", "deadband.bmp"},
        {"Switch", "switch.bmp"},
        {"Kinemat", "kinemat.bmp"},
        {"FCSFunction", "func.bmp"}
    };

    for (String[] pair : items) {

        String labelName = pair[0];
        String iconFile = pair[1];

        ImageIcon icon = null;

        // 1. Get the resource URL for the icon
        URL resourceUrl = FlightControlController.class.getResource("/assets/componentImg/" + iconFile);
        
        // 2. Load the icon 
        icon = loadReliableImageIcon(resourceUrl, iconFile);
        
        // 3. set up the JLabel
        JLabel tag = new JLabel(labelName, icon, JLabel.LEFT);
        tag.setForeground(new Color(0, 0, 0));
        tag.setOpaque(true);
        tag.setBackground(new Color(245, 245, 245));
        tag.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        tag.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        tag.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));


        // Draggable behavior
        tag.setTransferHandler(new TransferHandler("text") {
            @Override
            protected Transferable createTransferable(JComponent c) {
                return new StringSelection(((JLabel) c).getText());
            }
            @Override
            public int getSourceActions(JComponent c) {
                return COPY;
            }
        });

        tag.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JComponent c = (JComponent) e.getSource();
                c.getTransferHandler().exportAsDrag(c, e, TransferHandler.COPY);
            }
        });

        p.add(tag);
    }

    return p;
}


    // ---------------- Canvas mouse/controller logic ----------------
    private static void attachMouseControllers(FlightControlView view, FlightControlModel model) {
        MouseAdapter ma = new MouseAdapter() {
            private FlightControlModel.Node draggingNode = null;
            private Point dragOffset = null;

            private FlightControlModel.Node connectFrom = null; // node whose OUTPUT we grabbed

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Point p = e.getPoint();
                    FlightControlModel.Node node = view.nodeAt(p);
                    if (node != null) {
                        openNodePopup(node);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();

                // Start connection if press on an output port
                FlightControlModel.Node onOut = view.nodeWithOutputAt(p);
                if (onOut != null) {
                    connectFrom = onOut;
                    view.setConnectionPreview(connectFrom, p);
                    return;
                }

                // Otherwise maybe start dragging a node
                FlightControlModel.Node n = view.nodeAt(p);
                if (n != null) {
                    draggingNode = n;
                    dragOffset = new Point(p.x - n.bounds.x, p.y - n.bounds.y);
                    // Bring node to front (simple z-order)
                    model.nodes.remove(n);
                    model.nodes.add(n);
                    view.repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = e.getPoint();

                if (connectFrom != null) {
                    view.setConnectionPreview(connectFrom, p);
                    return;
                }

                if (draggingNode != null && dragOffset != null) {
                    draggingNode.bounds.x = p.x - dragOffset.x;
                    draggingNode.bounds.y = p.y - dragOffset.y;

                    for (FlightControlModel.Edge edge : model.edges) {
                        if(edge.from == draggingNode || edge.to == draggingNode) {
                            edge.updatePoints();
                        }
                    }

                    view.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();

                if (connectFrom != null) {
                    // Can we connect to an INPUT port?
                    FlightControlModel.Node releaseNode = view.nodeAt(p);
                    if (releaseNode != null && releaseNode != connectFrom) {
                        FlightControlModel.Node src = connectFrom;
                        FlightControlModel.Node dst = releaseNode;

                        if (src.type == FlightControlModel.NodeType.DESTINATION &&
                            dst.type == FlightControlModel.NodeType.SOURCE) {
                            FlightControlModel.Node tmp = src;
                            src = dst;
                            dst = tmp;
                        }
                        // Add checks before edges are created
                        if (!isValidConnection(src.type, dst.type)) {
                            JOptionPane.showMessageDialog(null,
                            "Invalid connection: " +
                            src.type.label + " → " + dst.type.label,
                            "Connection Not Allowed",
                            JOptionPane.WARNING_MESSAGE
                            );
                            connectFrom = null;
                            view.clearConnectionPreview();
                            return;
                            
                        }

                        Point fromAttach = getAttachedPoint(src, dst, true);
                        Point toAttach = getAttachedPoint(dst, src, false);
                        model.edges.add(new FlightControlModel.Edge(src, dst, fromAttach, toAttach));
                    }
                    connectFrom = null;
                    view.clearConnectionPreview();
                    return;
                }

                draggingNode = null;
                dragOffset = null;
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                // Allows the cursor to update when hovering over a node's output port or the node itself
                if (view.nodeWithOutputAt(e.getPoint()) != null) {
                    view.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                } else if (view.nodeAt(e.getPoint()) != null) {
                    view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    view.setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        view.addMouseListener(ma);
        view.addMouseMotionListener(ma);
    }

    private static Point getAttachedPoint(FlightControlModel.Node a, FlightControlModel.Node b, boolean isFrom) {
        Rectangle portRect;
        if (isFrom) {
            portRect = a.outputPortRect(FlightControlView.PORT_SIZE);
        } else {
            portRect = a.inputPortRect(FlightControlView.PORT_SIZE);
        }

        int cx = portRect.x + portRect.width / 2;
        int cy = portRect.y + portRect.height / 2;
        return new Point(cx, cy);
    }

    // ---------------- DnD: drop nodes onto canvas ----------------
    private static class CanvasDropHandler extends TransferHandler {
        private final FlightControlModel model;
        private final FlightControlView view;

        CanvasDropHandler(FlightControlModel model, FlightControlView view) {
            this.model = model;
            this.view = view;
        }

        @Override
        public boolean canImport(TransferSupport s) {
            return s.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport s) {
            if (!canImport(s)) return false;
            try {
                String label = (String) s.getTransferable().getTransferData(DataFlavor.stringFlavor);
                FlightControlModel.NodeType type = FlightControlModel.NodeType.fromLabel(label);

                // Where was it dropped?
                TransferHandler.DropLocation dl = (TransferHandler.DropLocation) s.getDropLocation();
                Point drop = dl.getDropPoint();

                // Create node with its top-left near the drop point
                int x = drop.x - 70;
                int y = drop.y - 35;
                model.addNode(type, x, y);
                view.repaint();
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    private static void openNodePopup(FlightControlModel.Node node) {
        try {
            String[][] data = {
                {"Block ID", String.valueOf(node.id)},
                {"Block Type", node.type.label}
            };

            String[] cols = {"Field", "Value"};

            JTable table = new JTable(data, cols);
            JScrollPane sp = new JScrollPane(table);

            JDialog d = new JDialog();
            d.setTitle("Block Configuration");
            d.setSize(400, 200);
            d.setLocationRelativeTo(null);
            d.add(sp);
            d.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Error displaying block configuration.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
     // Connection validation logic
     public static boolean isValidConnection(FlightControlModel.NodeType src, FlightControlModel.NodeType dst) {
    // Prevent connecting a block to itself
    if (src == dst) return false;

    // Destination cannot output to anything
    if (src == FlightControlModel.NodeType.DESTINATION) return false;

    // Source should not receive inputs
    if (dst == FlightControlModel.NodeType.SOURCE) return false;

    // Example: Prevent Filter connecting directly to Destination
    if (src == FlightControlModel.NodeType.FILTER && dst == FlightControlModel.NodeType.DESTINATION)
        return false;

    // Default: allow connection
    return true;
}

}
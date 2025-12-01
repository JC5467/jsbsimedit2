package uta.cse3310.commander.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;

import uta.cse3310.commander.model.FlightControlModel;
import uta.cse3310.tab.concreteTabs.flightcontrol.FlightControlView;

public final class FlightControlController {
    public static final Map<FlightControlModel.NodeType, ImageIcon> ICONS = new HashMap<>();

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
        Object[][] items = {
                { FlightControlModel.NodeType.SOURCE, "source.bmp" },
                { FlightControlModel.NodeType.DESTINATION, "destination.bmp" },
                { FlightControlModel.NodeType.SUMMER, "summer.bmp" },
                { FlightControlModel.NodeType.PID, "pid.bmp" },
                { FlightControlModel.NodeType.GAIN, "gain.bmp" },
                { FlightControlModel.NodeType.FILTER, "filter.bmp" },
                { FlightControlModel.NodeType.DEAD_BAND, "deadband.bmp" },
                { FlightControlModel.NodeType.SWITCH, "switch.bmp" },
                { FlightControlModel.NodeType.KINEMAT, "kinemat.bmp" },
                { FlightControlModel.NodeType.FCSFUNCTION, "func.bmp" }
        };

        for (Object[] pair : items) {
            FlightControlModel.NodeType type = (FlightControlModel.NodeType) pair[0];
            String iconFile = (String) pair[1];
            String labelName = type.name();

            ImageIcon icon = null;

            // 1. Locate the image using the classpath
            URL resourceUrl = FlightControlController.class.getResource("/assets/componentImg/" + iconFile);

            // 2. Try to load the icon reliably using ImageIO if the resource is found
            icon = loadReliableImageIcon(resourceUrl, iconFile);

            // Store in map so dropped nodes can access it
            ICONS.put(type, icon);

            // If icon is still null at this point, we will gracefully fall back to
            // text-only
            JLabel tag;
            if (icon != null) {
                tag = new JLabel(labelName, icon, JLabel.CENTER);
            } else {
                System.err.println("Falling back to text-only label for: " + labelName);
                tag = new JLabel(labelName);
            }

            tag.setOpaque(true);
            tag.setBackground(new Color(250, 250, 250));
            tag.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    new EmptyBorder(4, 6, 4, 6)));
            tag.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // TransferHandler for drag-and-drop
            tag.setTransferHandler(
                    new TransferHandler("text") {
                        @Override
                        protected Transferable createTransferable(JComponent c) {
                            return new StringSelection(type.label);
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
        final int RESIZE_MARGIN = 10;
        final int MIN_SIZE = 40;

        MouseAdapter ma = new MouseAdapter() {
            private FlightControlModel.Node draggingNode = null;
            private Point dragOffset = null;

            private FlightControlModel.Node connectFrom = null; // node whose OUTPUT we grabbed

            private FlightControlModel.Node resizingNode = null;
            private Point resizeAnchor = null;

            private boolean isInResizeZone(FlightControlModel.Node n, Point p) {
                Rectangle b = n.bounds;
                return p.x >= b.x + b.width - RESIZE_MARGIN &&
                        p.x <= b.x + b.width &&
                        p.y >= b.y + b.height - RESIZE_MARGIN &&
                        p.y <= b.y + b.height;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Point p = e.getPoint();
                    FlightControlModel.Node node = view.nodeAt(p);
                    if (node != null) {
                        if (node.type == FlightControlModel.NodeType.SUMMER) {
                            createDestinationForSummer(node, model, view);
                            return;
                        } else if (node.type == FlightControlModel.NodeType.GAIN) {
                            openGainPopup(node);
                        } else
                            openNodePopup(node);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();

                FlightControlModel.Node nForResize = view.nodeAt(p);
                if (nForResize != null && isInResizeZone(nForResize, p)) {
                    resizingNode = nForResize;
                    resizeAnchor = new Point(nForResize.bounds.x, nForResize.bounds.y);
                    model.nodes.remove(nForResize);
                    model.nodes.add(nForResize);
                    view.repaint();
                    return;
                }

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
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();

                if (resizingNode != null) {
                    resizingNode = null;
                    resizeAnchor = null;
                    return;
                }

                if (connectFrom != null) {
                    // Check if we can connect to a specific INPUT port
                    FlightControlView.InputHit hit = view.inputPortAt(p);

                    if (hit != null && hit.node != connectFrom) {
                        FlightControlModel.Node src = connectFrom;
                        FlightControlModel.Node dst = hit.node;
                        int inputIndex = hit.portIndex;

                        // Node type validation first
                        if (!isValidConnection(src.type, dst.type)) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Invalid connection: " + src.type.label + " -> " + dst.type.label,
                                    "Connection Error",
                                    JOptionPane.ERROR_MESSAGE);
                            connectFrom = null;
                            view.clearConnectionPreview();
                            return;
                        }

                        // Allow at most one edge per input port
                        for (FlightControlModel.Edge d : model.edges) {
                            if (d.to == dst && d.toInputIndex == inputIndex) {
                                JOptionPane.showMessageDialog(
                                        null,
                                        "That input port is already connected.",
                                        "Connection Error",
                                        JOptionPane.ERROR_MESSAGE);
                                connectFrom = null;
                                view.clearConnectionPreview();
                                return;
                            }
                        }

                        // Create the edge
                        model.edges.add(new FlightControlModel.Edge(src, dst, inputIndex));
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
                // Allows the cursor to update when hovering over a node's output port or the
                // node itself
                Point p = e.getPoint();
                FlightControlModel.Node n = view.nodeAt(p);
                if (n != null && isInResizeZone(n, p)) {
                    view.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                } else if (view.nodeWithOutputAt(e.getPoint()) != null) {
                    view.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                } else if (n != null) {
                    view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    view.setCursor(Cursor.getDefaultCursor());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = e.getPoint();

                if (resizingNode != null && resizeAnchor != null) {
                    int newW = Math.max(MIN_SIZE, p.x - resizeAnchor.x);
                    int newH = Math.max(MIN_SIZE, p.y - resizeAnchor.y);
                    resizingNode.bounds.width = newW;
                    resizingNode.bounds.height = newH;

                    for (FlightControlModel.Edge edge : model.edges) {
                        if (edge.from == resizingNode || edge.to == resizingNode) {
                            edge.updatePoints();
                        }
                    }

                    view.repaint();
                    return;
                }

                if (connectFrom != null) {
                    // Update the preview of a potential connection
                    view.setConnectionPreview(connectFrom, p);
                    return;
                }

                if (draggingNode != null && dragOffset != null) {
                    draggingNode.bounds.x = p.x - dragOffset.x;
                    draggingNode.bounds.y = p.y - dragOffset.y;

                    for (FlightControlModel.Edge edge : model.edges) {
                        if (edge.from == draggingNode || edge.to == draggingNode) {
                            edge.updatePoints();
                        }
                    }

                    view.repaint();
                }
            }
        };

        view.addMouseListener(ma);
        view.addMouseMotionListener(ma);
    }

    private static void createDestinationForSummer(FlightControlModel.Node summerNode, FlightControlModel model,
            FlightControlView view) {
        int destX = summerNode.bounds.x + summerNode.bounds.width + 40;
        int destY = summerNode.bounds.y;
        FlightControlModel.Node dest = model.addNode(FlightControlModel.NodeType.DESTINATION, destX, destY);
        model.addEdge(summerNode, dest);
        view.repaint();
    }

    public static Point getAttachedPoint(FlightControlModel.Node a, FlightControlModel.Node b, boolean isFrom) {
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
            if (!canImport(s))
                return false;

            try {
                Transferable t = s.getTransferable();
                String label = (String) t.getTransferData(DataFlavor.stringFlavor);

                FlightControlModel.NodeType type = null;
                for (FlightControlModel.NodeType nt : FlightControlModel.NodeType.values()) {
                    if (nt.label.equals(label)) {
                        type = nt;
                        break;
                    }
                }
                if (type == null) {
                    System.err.println("Unknown node type label: " + label);
                    return false;
                }

                Point dropPoint = s.getDropLocation().getDropPoint();
                FlightControlModel.Node newNode = model.addNode(type, dropPoint.x, dropPoint.y);

                view.repaint();
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    private static void openNodePopup(FlightControlModel.Node node) {
        String[][] data = {
                { "Block ID", String.valueOf(node.id) },
                { "Block Type", node.type.label }
        };

        String[] cols = { "Field", "Value" };

        JTable table = new JTable(data, cols);
        JScrollPane sp = new JScrollPane(table);

        JDialog d = new JDialog();
        d.setTitle("Block Configuration");
        d.setSize(400, 200);
        d.setLocationRelativeTo(null);
        d.add(sp);
        d.setVisible(true);
    }

    // Row creation helper
    private static JPanel makeRow(String label, JComponent input) {
        JPanel row = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        row.add(new JLabel(label), c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        row.add(input, c);

        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        return row;
    }

    private static void openGainPopup(FlightControlModel.Node node) {

        JDialog d = new JDialog();
        d.setTitle("Gain Component");
        d.setSize(400, 500);
        d.setLocationRelativeTo(null);
        d.setModal(true);
        d.setLayout(new BorderLayout());

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        JTextField nameField = new JTextField("g load norm");
        panel.add(makeRow("Name:", nameField));
        panel.add(Box.createVerticalStrut(8));

        // Type
        JComboBox<String> typeBox = new JComboBox<>(new String[] { "pure_gain" });
        panel.add(makeRow("Type:", typeBox));
        panel.add(Box.createVerticalStrut(8));

        // Order
        JTextField orderField = new JTextField("110");
        panel.add(makeRow("Order:", orderField));
        panel.add(Box.createVerticalStrut(10));

        // Cliper
        panel.add(Box.createVerticalStrut(10));
        JPanel clipperPanel = new JPanel();
        clipperPanel.setBorder(BorderFactory.createTitledBorder("cliper"));
        clipperPanel.setLayout(new BoxLayout(clipperPanel, BoxLayout.Y_AXIS));

        JCheckBox clippable = new JCheckBox("clippable");
        JTextField maxField = new JTextField();
        JTextField minField = new JTextField();

        maxField.setEnabled(false);
        minField.setEnabled(false);

        clippable.addActionListener(e -> {
            boolean enabled = clippable.isSelected();
            maxField.setEnabled(enabled);
            minField.setEnabled(enabled);
        });

        clipperPanel.add(clippable);
        clipperPanel.add(makeRow("Max:", maxField));
        clipperPanel.add(makeRow("Min:", minField));

        panel.add(clipperPanel);

        // Gain
        panel.add(Box.createVerticalStrut(10));
        JTextField gainField = new JTextField("0.125");
        panel.add(makeRow("Gain:", gainField));

        // Inputs
        panel.add(Box.createVerticalStrut(10));
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createTitledBorder("inputs"));
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JLabel inputLabel = new JLabel("input1");
        JRadioButton positive = new JRadioButton("positive");
        JRadioButton negative = new JRadioButton("negative", true);

        ButtonGroup group = new ButtonGroup();
        group.add(positive);
        group.add(negative);

        negative.setSelected(true);

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputRow.add(positive);
        inputRow.add(negative);

        inputPanel.add(inputLabel);
        inputPanel.add(inputRow);

        panel.add(inputPanel);

        d.add(panel, BorderLayout.CENTER);

        // Ok/Cancel buttons
        JPanel buttonPanel = new JPanel();
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        cancelBtn.addActionListener(e -> d.dispose());

        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);

        d.add(buttonPanel, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    // Connection validation logic
    public static boolean isValidConnection(FlightControlModel.NodeType src, FlightControlModel.NodeType dst) {
        // Prevent connecting a block to itself
        if (src == dst)
            return false;

        // Destination cannot output to anything
        if (src == FlightControlModel.NodeType.DESTINATION)
            return false;

        // Source should not receive inputs
        if (dst == FlightControlModel.NodeType.SOURCE)
            return false;

        // Example: Prevent Filter connecting directly to Destination
        if (src == FlightControlModel.NodeType.FILTER && dst == FlightControlModel.NodeType.DESTINATION) {
            return false;
        }

        // Default: allow connection
        return true;
    }

    public static void attachToPanel(javax.swing.JPanel host, uta.cse3310.commander.model.FlightControlModel model,
            uta.cse3310.tab.concreteTabs.flightcontrol.FlightControlView view) {
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(view);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);

        javax.swing.JPanel palette = buildPalette(view);

        host.setLayout(new java.awt.BorderLayout());
        host.add(palette, java.awt.BorderLayout.NORTH);
        host.add(scrollPane, java.awt.BorderLayout.CENTER);

        attachMouseControllers(view, model);
        view.setTransferHandler(new CanvasDropHandler(model, view));
    }
}

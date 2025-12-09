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
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.xml.namespace.QName;

import generated.ANDOR;
import generated.Integrator;
import generated.Kinematic;
import generated.LeadLagFilter;
import generated.PureGain;
import generated.Summer;
import generated.Switch;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
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

    // Helper method for reliable image loading
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

    // --- Palette ---
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

            // If icon is still null at this point, we will gracefully fall back to text-only
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

    // --- Canvas mouse/controller logic ---
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
                        if (node.type == FlightControlModel.NodeType.GAIN) {
                            openGainPopup(node, view);
                        } else if (node.type == FlightControlModel.NodeType.FILTER) {
                            openFilterPopup(node, view);
                        } else if (node.type == FlightControlModel.NodeType.SUMMER) {
                            openSummerPopup(node, view, model);
                        } else if (node.type == FlightControlModel.NodeType.PID) {
                            openGenericPopup(node, view, "PID Component", "PID", "100");
                        } else if (node.type == FlightControlModel.NodeType.SOURCE) {
                            openGenericPopup(node, view, "Source Component", "SOURCE", "0");
                        } else if (node.type == FlightControlModel.NodeType.DESTINATION) {
                            openGenericPopup(node, view, "Destination Component", "DESTINATION", "0");
                        } else if (node.type == FlightControlModel.NodeType.DEAD_BAND) {
                            openGenericPopup(node, view, "Dead Band Component", "DEAD_BAND", "100");
                        } else if (node.type == FlightControlModel.NodeType.SWITCH) {
                            openSwitchPopup(node, view, model);
                        } else if (node.type == FlightControlModel.NodeType.KINEMAT) {
                            openGenericPopup(node, view, "Kinemat Component", "KINEMAT", "100");
                        } else if (node.type == FlightControlModel.NodeType.FCSFUNCTION) {
                            openGenericPopup(node, view, "FCS Function Component", "FCSFUNCTION", "100");
                        } else {
                            openNodePopup(node);
                        }
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
                        model.addEdge(src, dst, inputIndex);

                        // keep the backing JAXB <input> list in sync with the graph (if any)
                        syncNodeInputsFromEdges(dst, model);
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

    // Map a palette node type to a concrete JAXB class to instantiate
    private static Object createBlockForNodeType(FlightControlModel.NodeType type) {
        if (type == null) return null;
        switch (type) {
            case SUMMER:
                return new Summer();
            case GAIN:
                return new PureGain();
            case PID:
                // Represent PID with an <integrator> block by default
                return new Integrator();
            case FILTER:
                return new LeadLagFilter();
            case KINEMAT:
                return new Kinematic();
            case SWITCH: {
                Switch s = new Switch();
                Switch.Default def = new Switch.Default();
                def.setValue("0");
                s.setDefault(def);
                return s;
            }
            default:
                // SOURCE / DESTINATION / FCSFUNCTION don't have a direct
                // <flight_control> block representation
                return null;
        }
    }

    // Create and attach a new JAXB backing block for a freshly created node,
    // and insert it into this channel's block list so it will be saved.
    private static void attachBackingBlockForNewNode(FlightControlModel.Node node, FlightControlModel model) {
        if (node == null) return;

        // Already bound to an XML block - so nothing to do.
        if (node.backingBlock != null) return;

        // Only nodes that correspond to real <flight_control> blocks get backing JAXB objects
        Object block = createBlockForNodeType(node.type);
        if (block == null) {
            // This node type is purely graphical
            return;
        }

        // Attach to the channel's block list so it will be marshalled on save.
        if (model.channelBlocks != null) {
            model.channelBlocks.add(block);
        } else {
            System.err.println("FlightControlModel has no channelBlocks; new node will not be written to XML.");
        }

        // Give the block an initial name matching the node label, but make it unique.
        String name = node.displayName;
        if (name == null || name.isBlank() || name.equals(node.type.label)) {
            name = node.type.label + " " + node.id;
            node.displayName = name;  // keep the canvas label in sync
        }
        try {
            Method setName = block.getClass().getMethod("setName", String.class);
            setName.invoke(block, name);
        } catch (NoSuchMethodException ignore) {
            // no name
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        node.backingBlock = block;
    }

    // --- DnD: drop nodes onto canvas ---
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
                FlightControlModel.Node node = model.addNode(type, dropPoint.x, dropPoint.y);

                // Bind this new node to a JAXB block so edits and connections
                // are reflected back into the XML and will be saved.
                attachBackingBlockForNewNode(node, model);

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

    // --- Helpers for Gain popup <-> JAXB block binding ---
    private static String readNameFromBackingBlock(FlightControlModel.Node node) {
        if (node == null || node.backingBlock == null)
            return "";
        try {
            Method getName = node.backingBlock.getClass().getMethod("getName");
            Object v = getName.invoke(node.backingBlock);
            return v != null ? v.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }

    // Read the gain value from the backing JAXB block in a robust way
    private static String readGainFromBackingBlock(FlightControlModel.Node node) {
        if (node == null || node.backingBlock == null)
            return "";
        Object block = node.backingBlock;
        try {
            Method m = block.getClass().getMethod("getGain");
            Object v = m.invoke(block);
            if (v == null)
                return "";

            // Direct numeric types
            if (v instanceof BigDecimal) {
                return ((BigDecimal) v).toPlainString();
            }
            if (v instanceof Number) {
                return v.toString();
            }

            // Some JAXB types wrap the numeric value inside getValue()
            try {
                Method getVal = v.getClass().getMethod("getValue");
                Object inner = getVal.invoke(v);
                if (inner instanceof BigDecimal) {
                    return ((BigDecimal) inner).toPlainString();
                }
                if (inner != null) {
                    return inner.toString();
                }
            } catch (NoSuchMethodException ignore) {
                // no getValue(); fall through
            }

            // Last resort
            return v.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    // Write the name back to the JAXB backing block (and always update the node label)
    private static void writeNameToBackingBlock(FlightControlModel.Node node, String text) {
        if (node == null)
            return;

        text = (text != null) ? text.trim() : "";
        if (text.isEmpty())
            return;

        // Always update the node's displayName so the canvas label changes
        node.displayName = text;

        // If there is no backing JAXB block, we're done (purely visual node)
        if (node.backingBlock == null)
            return;

        try {
            Method setName = node.backingBlock.getClass().getMethod("setName", String.class);
            setName.invoke(node.backingBlock, text);
        } catch (Exception e) {
            // Not fatal - the graph still keeps the name even if the block can't
            e.printStackTrace();
        }
    }

    // Write the gain back to the backing JAXB block
    private static void updateGainOnBackingBlock(FlightControlModel.Node node, String gainText) {
        if (node == null || node.backingBlock == null)
            return;

        gainText = (gainText != null) ? gainText.trim() : "";
        if (gainText.isEmpty())
            return;

        Object block = node.backingBlock;
        Class<?> cls = block.getClass();

        BigDecimal value;
        try {
            value = new BigDecimal(gainText);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(
                    null,
                    "Gain must be a valid number: \"" + gainText + "\"",
                    "Invalid Gain",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        double asDouble = value.doubleValue();
        boolean updated = false;

        try {
            // 1) Try setGain(...) on the block itself with whatever parameter type it uses
            for (Method m : cls.getMethods()) {
                if (!m.getName().equals("setGain") || m.getParameterCount() != 1)
                    continue;
                Class<?> pt = m.getParameterTypes()[0];

                try {
                    if (pt == BigDecimal.class) {
                        m.invoke(block, value);
                        updated = true;
                        break;
                    } else if (pt == double.class || pt == Double.class) {
                        m.invoke(block, asDouble);
                        updated = true;
                        break;
                    } else if (Number.class.isAssignableFrom(pt)) {
                        // e.g. some custom numeric wrapper type with String ctor
                        m.invoke(block, pt.getConstructor(String.class).newInstance(gainText));
                        updated = true;
                        break;
                    } else if (pt == String.class) {
                        m.invoke(block, gainText);
                        updated = true;
                        break;
                    }
                } catch (Exception ignore) {
                    // try next overload
                }
            }

            // 2) If there is no usable setGain, try mutating an inner "gain" object via
            // setValue(...)
            if (!updated) {
                try {
                    Method getGain = cls.getMethod("getGain");
                    Object gainObj = getGain.invoke(block);
                    if (gainObj != null) {
                        Class<?> gCls = gainObj.getClass();
                        for (Method gm : gCls.getMethods()) {
                            if (!gm.getName().equals("setValue") || gm.getParameterCount() != 1)
                                continue;
                            Class<?> pt = gm.getParameterTypes()[0];

                            try {
                                if (pt == BigDecimal.class) {
                                    gm.invoke(gainObj, value);
                                    updated = true;
                                    break;
                                } else if (pt == double.class || pt == Double.class) {
                                    gm.invoke(gainObj, asDouble);
                                    updated = true;
                                    break;
                                } else if (Number.class.isAssignableFrom(pt)) {
                                    gm.invoke(gainObj, pt.getConstructor(String.class).newInstance(gainText));
                                    updated = true;
                                    break;
                                } else if (pt == String.class) {
                                    gm.invoke(gainObj, gainText);
                                    updated = true;
                                    break;
                                }
                            } catch (Exception ignore2) {
                                // try next overload on inner object
                            }
                        }
                    }
                } catch (NoSuchMethodException ignored) {
                    // no getGain(); nothing more we can do
                }
            }

            if (!updated) {
                System.err.println("No usable gain setter on " + cls.getName());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    // --- Gain popup ---
    private static void openGainPopup(FlightControlModel.Node node, FlightControlView view) {
        JDialog d = new JDialog();
        d.setTitle("Gain Component");
        d.setSize(400, 500);
        d.setLocationRelativeTo(null);
        d.setModal(true);
        d.setLayout(new BorderLayout());

        JTabbedPane topTabs = new JTabbedPane();

        JPanel basicTab = buildBasicTab(node); // Gain-specific basic tab
        topTabs.addTab("Basic", basicTab);
        topTabs.addTab("AeroSurface", buildAeroSurfaceTab(node));
        topTabs.addTab("Scheduled", buildScheduledTab(node));

        d.add(topTabs, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        okBtn.addActionListener(e -> {
            JPanel basicPanel = (JPanel) topTabs.getComponentAt(0);

            JTextField nameField = (JTextField) basicPanel.getClientProperty("nameField");
            JTextField gainField = (JTextField) basicPanel.getClientProperty("gainField");

            if (nameField != null) {
                writeNameToBackingBlock(node, nameField.getText());
            }
            if (gainField != null) {
                updateGainOnBackingBlock(node, gainField.getText());
            }

            view.repaint();
            d.dispose();
        });

        cancelBtn.addActionListener(e -> d.dispose());

        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        d.add(buttonPanel, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    // Gain basic tab (existing)
    private static JPanel buildBasicTab(FlightControlModel.Node node) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        // Name
        String nameText = (node.displayName != null && !node.displayName.isBlank())
                ? node.displayName
                : readNameFromBackingBlock(node);
        if (nameText == null)
            nameText = "";

        JTextField nameField = new JTextField(nameText);
        panel.add(makeRow("Name:", nameField));
        panel.add(Box.createVerticalStrut(8));
        panel.putClientProperty("nameField", nameField);

        // Type
        JComboBox<String> typeBox = new JComboBox<>(new String[] { "pure_gain" });
        panel.add(makeRow("Type:", typeBox));
        panel.add(Box.createVerticalStrut(8));

        // Order
        JTextField orderField = new JTextField("110");
        panel.add(makeRow("Order:", orderField));
        panel.add(Box.createVerticalStrut(10));

        // Clipper
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
        String gainText = readGainFromBackingBlock(node);
        if (gainText == null || gainText.isBlank()) {
            gainText = "0.0";
        }
        JTextField gainField = new JTextField(gainText);
        panel.add(makeRow("Gain:", gainField));
        panel.putClientProperty("gainField", gainField);

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

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputRow.add(positive);
        inputRow.add(negative);

        inputPanel.add(inputLabel);
        inputPanel.add(inputRow);

        panel.add(inputPanel);

        return panel;
    }

    private static JPanel buildAeroSurfaceTab(FlightControlModel.Node node) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(80, 20, 10, 10));

        JTextField maxField = new JTextField("0.0");
        panel.add(makeRow("Max:", maxField));
        panel.add(Box.createVerticalStrut(8));

        JTextField minField = new JTextField("0.0");
        panel.add(makeRow("Min:", minField));
        panel.add(Box.createVerticalStrut(8));

        return panel;
    }

    private static JPanel buildScheduledTab(FlightControlModel.Node node) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        // Top row
        JPanel topRow = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        topRow.add(new JLabel("independentVar:"), c);

        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JComboBox<String> independentVarBox = new JComboBox<>(
                new String[] { "", "alpha", "beta", "mach", "altitude" });

        topRow.add(independentVarBox, c);

        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.add(topRow);
        panel.add(Box.createVerticalStrut(10));

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEtchedBorder());

        String[] columnNames = { "independentVar", "Value" };

        Object[][] data = new Object[100][2];
        for (int i = 0; i < 100; i++) {
            data[i][0] = (i + 1); // left column = row number
            data[i][1] = ""; // right column editable
        }

        JTable table = new JTable(data, columnNames);

        table.setRowHeight(22);
        table.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(350, 300));

        tablePanel.add(scroll, BorderLayout.CENTER);

        panel.add(tablePanel);

        return panel;
    }

    // --- Filter popup ---
    private static void openFilterPopup(FlightControlModel.Node node, FlightControlView view) {
        JDialog d = new JDialog();
        d.setTitle("Filter Component");
        d.setSize(400, 500);
        d.setLocationRelativeTo(null);
        d.setModal(true);
        d.setLayout(new BorderLayout());

        JTabbedPane topTabs = new JTabbedPane();

        JPanel basicTab = buildFilterBasicTab(node);
        JPanel filterTab = buildFilterCoefficientsTab(node);

        topTabs.addTab("Basic", basicTab);
        topTabs.addTab("Filter", filterTab);

        d.add(topTabs, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        okBtn.addActionListener(e -> {
            JPanel basicPanel = (JPanel) topTabs.getComponentAt(0);
            JTextField nameField = (JTextField) basicPanel.getClientProperty("nameField");
            if (nameField != null) {
                writeNameToBackingBlock(node, nameField.getText());
            }
            view.repaint();
            d.dispose();
        });

        cancelBtn.addActionListener(e -> d.dispose());

        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        d.add(buttonPanel, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    private static JPanel buildFilterBasicTab(FlightControlModel.Node node) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        // Name
        String nameText = (node.displayName != null && !node.displayName.isBlank())
                ? node.displayName
                : readNameFromBackingBlock(node);
        if (nameText == null)
            nameText = "";

        JTextField nameField = new JTextField(nameText);
        panel.add(makeRow("Name:", nameField));
        panel.add(Box.createVerticalStrut(8));
        panel.putClientProperty("nameField", nameField);

        // Type
        JComboBox<String> typeBox = new JComboBox<>(new String[] { "integrator" });
        panel.add(makeRow("Type:", typeBox));
        panel.add(Box.createVerticalStrut(8));

        // Order
        JTextField orderField = new JTextField("150");
        panel.add(makeRow("Order:", orderField));
        panel.add(Box.createVerticalStrut(10));

        // Clipper
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

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputRow.add(positive);
        inputRow.add(negative);

        inputPanel.add(inputLabel);
        inputPanel.add(inputRow);

        panel.add(inputPanel);

        return panel;
    }

    private static JPanel buildFilterCoefficientsTab(FlightControlModel.Node node) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        // Simple visual for c1/s
        JPanel formulaPanel = new JPanel(new BorderLayout());
        formulaPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel formulaLabel = new JLabel("<html><center>c1<br>────<br>s</center></html>", JLabel.CENTER);
        formulaPanel.add(formulaLabel, BorderLayout.CENTER);
        formulaPanel.setPreferredSize(new Dimension(200, 80));
        panel.add(formulaPanel);
        panel.add(Box.createVerticalStrut(10));

        JTextField c1Field = new JTextField("0.005");
        JTextField c2Field = new JTextField();
        JTextField c3Field = new JTextField();
        JTextField c4Field = new JTextField();
        JTextField c5Field = new JTextField();
        JTextField c6Field = new JTextField();
        JTextField triggerField = new JTextField();

        panel.add(makeRow("c1:", c1Field));
        panel.add(Box.createVerticalStrut(4));
        panel.add(makeRow("c2:", c2Field));
        panel.add(Box.createVerticalStrut(4));
        panel.add(makeRow("c3:", c3Field));
        panel.add(Box.createVerticalStrut(4));
        panel.add(makeRow("c4:", c4Field));
        panel.add(Box.createVerticalStrut(4));
        panel.add(makeRow("c5:", c5Field));
        panel.add(Box.createVerticalStrut(4));
        panel.add(makeRow("c6:", c6Field));
        panel.add(Box.createVerticalStrut(4));
        panel.add(makeRow("trigger:", triggerField));

        return panel;
    }

    // --- Summer popup ---
    private static void openSummerPopup(FlightControlModel.Node node, FlightControlView view, FlightControlModel model) {
        JDialog d = new JDialog();
        d.setTitle("Summer Component");
        d.setSize(400, 400);
        d.setLocationRelativeTo(null);
        d.setModal(true);
        d.setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        JPanel basicTab = buildSummerBasicTab(node);
        JPanel inputsTab = buildInputsTab(node, model, view);

        tabs.addTab("Basic", basicTab);
        tabs.addTab("Inputs", inputsTab);

        d.add(tabs, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        okBtn.addActionListener(e -> {
            JPanel basicPanel = (JPanel) tabs.getComponentAt(0);
            JTextField nameField = (JTextField) basicPanel.getClientProperty("nameField");
            if (nameField != null) {
                writeNameToBackingBlock(node, nameField.getText());
            }
            view.repaint();
            d.dispose();
        });

        cancelBtn.addActionListener(e -> d.dispose());

        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        d.add(buttonPanel, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    private static void openSwitchPopup(FlightControlModel.Node node, FlightControlView view, FlightControlModel model) {
        JDialog d = new JDialog();
        d.setTitle("Switch Component");
        d.setSize(400, 400);
        d.setLocationRelativeTo(null);
        d.setModal(true);
        d.setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        // Basic tab: reuse the simple basic tab helper
        JPanel basicTab = buildSimpleBasicTab(node, "SWITCH", "100");
        JPanel inputsTab = buildInputsTab(node, model, view);

        tabs.addTab("Basic", basicTab);
        tabs.addTab("Inputs", inputsTab);

        d.add(tabs, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        okBtn.addActionListener(e -> {
            JPanel basicPanel = (JPanel) tabs.getComponentAt(0);
            JTextField nameField = (JTextField) basicPanel.getClientProperty("nameField");
            if (nameField != null) {
                writeNameToBackingBlock(node, nameField.getText());
            }
            view.repaint();
            d.dispose();
        });

        cancelBtn.addActionListener(e -> d.dispose());

        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        d.add(buttonPanel, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    private static JPanel buildSummerBasicTab(FlightControlModel.Node node) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        // Name
        String nameText = (node.displayName != null && !node.displayName.isBlank())
                ? node.displayName
                : readNameFromBackingBlock(node);
        if (nameText == null)
            nameText = "";

        JTextField nameField = new JTextField(nameText);
        panel.add(makeRow("Name:", nameField));
        panel.add(Box.createVerticalStrut(8));
        panel.putClientProperty("nameField", nameField);

        // Type (fixed SUMMER)
        JTextField typeField = new JTextField("SUMMER");
        typeField.setEditable(false);
        panel.add(makeRow("Type:", typeField));
        panel.add(Box.createVerticalStrut(8));

        // Order
        JTextField orderField = new JTextField("100");
        panel.add(makeRow("Order:", orderField));
        panel.add(Box.createVerticalStrut(10));

        // Clipper
        panel.add(Box.createVerticalStrut(10));
        JPanel clipperPanel = new JPanel();
        clipperPanel.setBorder(BorderFactory.createTitledBorder("cliper"));
        clipperPanel.setLayout(new BoxLayout(clipperPanel, BoxLayout.Y_AXIS));

        JCheckBox clippable = new JCheckBox("clippable");
        JTextField maxField = new JTextField();
        JTextField minField = new JTextField();
        JTextField biasField = new JTextField();

        maxField.setEnabled(false);
        minField.setEnabled(false);
        biasField.setEnabled(false);

        clippable.addActionListener(e -> {
            boolean enabled = clippable.isSelected();
            maxField.setEnabled(enabled);
            minField.setEnabled(enabled);
            biasField.setEnabled(enabled);
        });

        clipperPanel.add(clippable);
        clipperPanel.add(makeRow("Max:", maxField));
        clipperPanel.add(makeRow("Min:", minField));
        clipperPanel.add(makeRow("Bias:", biasField));

        panel.add(clipperPanel);

        // Empty "inputs" group (actual list lives on Inputs tab)
        JPanel inputsPanel = new JPanel();
        inputsPanel.setBorder(BorderFactory.createTitledBorder("inputs"));
        inputsPanel.setPreferredSize(new Dimension(200, 60));
        panel.add(inputsPanel);

        return panel;
    }

    // Generic "Inputs" tab for any node whose backing block exposes
    // getInput() or getInputs() and where each <input> is a string
    // like "fcs/g-load-norm".
    private static JPanel buildInputsTab(FlightControlModel.Node node, FlightControlModel model, FlightControlView view) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Model holds canonical XML strings like "fcs/g-load-norm"
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);

        // Renderer: show a cleaned label (no fcs/, no dashes) to the user
        list.setCellRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> lst, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                String xmlPath = (value instanceof String) ? (String) value : "";
                String display = cleanPropertyLabel(xmlPath); // e.g. "fcs/g-load-norm" -> "g load norm"
                return super.getListCellRendererComponent(lst, display, index, isSelected, cellHasFocus);
            }
        });

        JScrollPane scroll = new JScrollPane(list);
        panel.add(scroll, BorderLayout.CENTER);

        // Prefer canonical <input> strings from the JAXB backing block when available.
        java.util.List<String> xmlInputs = readInputStringsFromBackingBlock(node);

        if (!xmlInputs.isEmpty()) {
            for (String s : xmlInputs) {
                listModel.addElement(s);
            }
        } else {
            // Fallback: derive entries from incoming edges to this node
            java.util.List<FlightControlModel.Edge> incoming = new ArrayList<>();
            for (FlightControlModel.Edge e : model.edges) {
                if (e.to == node) {
                    incoming.add(e);
                }
            }
            incoming.sort((a, b) -> Integer.compare(a.toInputIndex, b.toInputIndex));

            for (FlightControlModel.Edge e : incoming) {
                FlightControlModel.Node src = e.from;
                String prop = readPrimaryOutputProperty(src);

                // Fallbacks if we can't get an output property
                if (prop == null || prop.isBlank()) {
                    prop = (src.displayName != null && !src.displayName.isBlank())
                            ? src.displayName
                            : readNameFromBackingBlock(src);
                }
                if (prop == null || prop.isBlank()) {
                    prop = src.type.label + " " + src.id;
                }

                listModel.addElement(prop);
            }
        }

        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel signPanel = new JPanel();
        signPanel.setBorder(BorderFactory.createTitledBorder("sign"));
        JRadioButton positive = new JRadioButton("Positive", true);
        JRadioButton negative = new JRadioButton("Negative");
        ButtonGroup group = new ButtonGroup();
        group.add(positive);
        group.add(negative);
        signPanel.add(positive);
        signPanel.add(negative);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton removeBtn = new JButton("Remove");

        // user gives either a property path or a block Name
        addBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(
                    panel,
                    "Input property path (e.g. fcs/g-load-norm) or block Name:",
                    "Add Input",
                    JOptionPane.PLAIN_MESSAGE
            );
            if (input == null) return; // cancelled
            input = input.trim();
            if (input.isEmpty()) return;

            String xmlPath;
            FlightControlModel.Node srcForEdge = null;

            if (input.contains("/") || input.startsWith("fcs/")) {
                // Treat as full property path
                xmlPath = input;
                // Try to find a matching source node to draw an edge from
                srcForEdge = findSourceNodeByOutputProperty(node, model, xmlPath);
            } else {
                // Treat as a block Name
                FlightControlModel.Node srcByName = null;
                for (FlightControlModel.Node n : model.nodes) {
                    if (n == node) continue;
                    String nName = (n.displayName != null && !n.displayName.isBlank())
                            ? n.displayName
                            : readNameFromBackingBlock(n);
                    if (nName != null && nName.equals(input)) {
                        srcByName = n;
                        break;
                    }
                }

                if (srcByName == null) {
                    JOptionPane.showMessageDialog(
                            panel,
                            "No block found with Name \"" + input + "\".\n" +
                            "Either set the block's Name or enter the full property path (e.g. fcs/alpha-limiter).",
                            "Add Input",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                // Get canonical property path from that block if possible
                String outProp = readPrimaryOutputProperty(srcByName);
                if (outProp != null && !outProp.isBlank()) {
                    xmlPath = outProp;
                } else {
                    // Fallback guess: "fcs/<name-with-dashes>"
                    xmlPath = "fcs/" + input.trim().toLowerCase().replace(' ', '-');
                }
                srcForEdge = srcByName;
            }

            // Update list (XML)
            listModel.addElement(xmlPath);
            syncInputsListToBackingBlock(node, listModel);

            // Update edges (graph) only if we have a concrete source node
            if (srcForEdge != null) {
                appendInputEdgeForNode(node, model, srcForEdge);
                view.repaint();
            } else {
                // No edge, but XML will still contain the <input> string
                System.err.println("Added input \"" + xmlPath +
                        "\" for node type " + node.type.label +
                        " with no matching source node; XML is updated, but no edge drawn.");
            }
        });

        // delete the selected entry + its corresponding edge
        removeBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx < 0) return;

            listModel.remove(idx);
            syncInputsListToBackingBlock(node, listModel);

            // Remove Nth incoming edge (if present) to keep graph synced
            removeInputEdgeAtIndex(node, model, idx);
            view.repaint();
        });

        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);

        bottomPanel.add(signPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Pull the "real" XML string from JAXB-generated objects
    private static String extractXmlString(Object v) {
        if (v == null) return null;

        if (v instanceof String) return (String) v;
        if (v instanceof CharSequence) return v.toString();

        try {
            Method getValue = v.getClass().getMethod("getValue");
            Object inner = getValue.invoke(v);
            if (inner instanceof String) return (String) inner;
            if (inner instanceof CharSequence) return inner.toString();
        } catch (NoSuchMethodException ignored) {
            // fall through
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return v.toString();
    }

    // Convert a human name like "g load command" into a JSBSim-style
    // property token "fcs/g-load-command".
    private static String nameToPropertyToken(String name) {
        if (name == null) return null;
        String slug = name.trim().toLowerCase();
        if (slug.isEmpty()) return null;

        // Replace any run of non-alphanumeric chars with a single dash
        slug = slug.replaceAll("[^a-z0-9]+", "-");

        // Trim leading/trailing dashes
        slug = slug.replaceAll("^-+", "").replaceAll("-+$", "");
        if (slug.isEmpty()) return null;

        return "fcs/" + slug;
    }


    // Turn "fcs/g-load-norm" -> "g load norm"
    // and also normalize "G_LOAD-NORM" etc to "g load norm"
    private static String cleanPropertyLabel(String s) {
        if (s == null) return "";
        s = s.trim();

        // remove prefix path "fcs/" etc
        int slash = s.lastIndexOf('/');
        if (slash >= 0 && slash < s.length() - 1) {
            s = s.substring(slash + 1);
        }

        // replace separators with spaces
        s = s.replace('-', ' ').replace('_', ' ');

        // collapse multiple spaces
        s = s.replaceAll("\\s+", " ");

        return s.trim();
    }

    // Small descriptor used when an <input> list is wrapped in an inner JAXB type.
    private static final class InputListDescriptor {
        final Object container;          // object that actually owns the List field
        final Method listGetter;         // getter that returned the List (or its wrapper)
        final java.util.List<?> list;    // the actual modifiable List

        InputListDescriptor(Object container, Method listGetter, java.util.List<?> list) {
            this.container = container;
            this.listGetter = listGetter;
            this.list = list;
        }
    }

    // Some generated classes (notably generated.Switch) wrap the real <input>
    // List in an inner JAXB type, so calling getInput()/getInputs() on the
    // block returns that wrapper instead of the List itself.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static InputListDescriptor findInputListDescriptor(Object block, Method topGetter) {
        if (block == null || topGetter == null) return null;

        Object topValue;
        try {
            topValue = topGetter.invoke(block);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        // getter already returned a List
        if (topValue instanceof java.util.List) {
            return new InputListDescriptor(block, topGetter, (java.util.List) topValue);
        }

        Class<?> retType = topGetter.getReturnType();

        // getter's return type IS a List but the value was null
        if (topValue == null && java.util.List.class.isAssignableFrom(retType)) {
            try {
                // Try to locate and initialize the backing field directly
                String fieldName = null;
                String gname = topGetter.getName(); // e.g. "getInput" or "getInputs"
                if (gname.startsWith("get") && gname.length() > 3) {
                    fieldName = Character.toLowerCase(gname.charAt(3)) + gname.substring(4);
                }

                if (fieldName != null) {
                    java.lang.reflect.Field f = block.getClass().getDeclaredField(fieldName);
                    f.setAccessible(true);
                    java.util.List list = (java.util.List) f.get(block);
                    if (list == null) {
                        list = new java.util.ArrayList();
                        f.set(block, list);
                    }
                    return new InputListDescriptor(block, topGetter, list);
                }
            } catch (Exception ignore) {
                // If we can't initialize it, treat as single-value property
            }
            return null;
        }

        // wrapper-style property (Switch-style), value is null
        if (topValue == null && !retType.isPrimitive()) {
            try {
                // Create the wrapper instance (e.g. new Switch.Inputs())
                Object wrapper = retType.getDeclaredConstructor().newInstance();

                // Derive setter name from getter: getInputs -> setInputs
                String gname = topGetter.getName();
                if (gname.startsWith("get") && gname.length() > 3) {
                    String prop = gname.substring(3); // "Inputs"
                    String setterName = "set" + prop;

                    try {
                        Method setter = block.getClass().getMethod(setterName, retType);
                        setter.invoke(block, wrapper);
                        topValue = wrapper;
                    } catch (NoSuchMethodException ignored) {
                        // No setter found, try to assign the field directly
                        try {
                            String fieldName = Character.toLowerCase(prop.charAt(0)) + prop.substring(1);
                            java.lang.reflect.Field f = block.getClass().getDeclaredField(fieldName);
                            f.setAccessible(true);
                            f.set(block, wrapper);
                            topValue = wrapper;
                        } catch (Exception ignored2) {
                            // Give up, treat as single-valued
                            return null;
                        }
                    }
                }
            } catch (Exception ex) {
                // Could not create wrapper; treat as single-valued
                return null;
            }
        }

        if (topValue == null) {
            // this property behaves like a single value
            return null;
        }

        // wrapper object exists; look inside for a List
        Object wrapper = topValue;
        Class<?> wCls = wrapper.getClass();
        Method innerGetter = null;

        // Try common patterns on the wrapper: getInput(), getInputs(), getValue()
        for (String name : new String[] { "getInput", "getInputs", "getValue" }) {
            try {
                innerGetter = wCls.getMethod(name);
                break;
            } catch (NoSuchMethodException ignored) {
            }
        }

        if (innerGetter != null) {
            try {
                Object innerVal = innerGetter.invoke(wrapper);
                if (innerVal instanceof java.util.List) {
                    return new InputListDescriptor(wrapper, innerGetter, (java.util.List) innerVal);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // No list found, treat as single-value property
        return null;
    }

    // Check whether the JAXB "input"/"inputs" property is declared with @XmlElementRef
    private static boolean usesXmlElementRef(Class<?> blockClass, Method getter) {
        if (getter != null &&
                (getter.isAnnotationPresent(XmlElementRef.class)
                        || getter.isAnnotationPresent(XmlElementRefs.class))) {
            return true;
        }

        String fieldName = null;
        if (getter != null) {
            String gname = getter.getName();
            if (gname.startsWith("get") && gname.length() > 3) {
                fieldName = Character.toLowerCase(gname.charAt(3)) + gname.substring(4);
            }
        }

        if (fieldName != null) {
            try {
                java.lang.reflect.Field f = blockClass.getDeclaredField(fieldName);
                if (f.isAnnotationPresent(XmlElementRef.class)
                        || f.isAnnotationPresent(XmlElementRefs.class)) {
                    return true;
                }
            } catch (NoSuchFieldException ignored) {
                // fall through
            }
        }

        // Fallback: any List field annotated with XmlElementRef(s)
        for (java.lang.reflect.Field f : blockClass.getDeclaredFields()) {
            if (java.util.List.class.isAssignableFrom(f.getType()) &&
                    (f.isAnnotationPresent(XmlElementRef.class)
                            || f.isAnnotationPresent(XmlElementRefs.class))) {
                return true;
            }
        }
        return false;
    }

    // Replace the JAXB <input> list on a backing block with the given canonical strings,
    // creating either plain Strings or JAXBElement<String> instances depending on how
    // the property is declared in the generated class.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void writeCanonicalInputsToJaxbList(Object block, Method inputGetter, java.util.List jaxbList,
                                                       java.util.List<String> canonicalInputs) {
        if (block == null || jaxbList == null) return;

        // Prototype element if there was one, so we can preserve QName
        Object prototype = jaxbList.isEmpty() ? null : jaxbList.get(0);

        boolean useElementRef = usesXmlElementRef(block.getClass(), inputGetter);

        jaxbList.clear();

        for (String s : canonicalInputs) {
            if (s == null) continue;
            String value = s.trim();
            if (value.isEmpty()) continue;

            Object element;

            if (prototype instanceof JAXBElement<?>) {
                // Keep the original QName
                JAXBElement<?> proto = (JAXBElement<?>) prototype;
                QName qName = proto.getName();
                element = new JAXBElement<>(qName, String.class, value);
            } else if (useElementRef) {
                QName qName = new QName("", "input");
                element = new JAXBElement<>(qName, String.class, value);
            } else {
                element = value;
            }

            jaxbList.add(element);
        }
    }

    // For blocks where getInput()/getInputs() does NOT return a List but a single
    // value (e.g. generated.Switch), push the first canonical input string into that property.
    private static void setSingleInputValueOnBlock(Object block, Method getter, Object valueObj, String newValue) {
        if (block == null) return;
        Class<?> cls = block.getClass();

        Method setter = null;
        for (Method m : cls.getMethods()) {
            if ((m.getName().equals("setInput") || m.getName().equals("setInputs"))
                    && m.getParameterCount() == 1) {
                setter = m;
                break;
            }
        }

        if (setter != null) {
            Class<?> pt = setter.getParameterTypes()[0];

            try {
                Object arg;

                if (JAXBElement.class.isAssignableFrom(pt)) {
                    // If we already have a prototype JAXBElement from the getter,
                    // reuse its QName so we match the schema.
                    QName qName = null;
                    if (valueObj instanceof JAXBElement<?>) {
                        qName = ((JAXBElement<?>) valueObj).getName();
                    }
                    if (qName == null) {
                        qName = new QName("", "input");
                    }
                    arg = new JAXBElement<>(qName, String.class, newValue);
                } else if (pt == String.class || pt == CharSequence.class) {
                    arg = newValue;
                } else {
                    if (newValue == null) {
                        arg = null;
                    } else {
                        arg = pt.getConstructor(String.class).newInstance(newValue);
                    }
                }

                setter.invoke(block, arg);
                return;
            } catch (Exception ignored) {
                // fall through
            }
        }

        // If there is no setter or invoking it failed, but we have an object,
        // try mutating that object via setValue(String)
        if (valueObj != null && newValue != null) {
            try {
                Method setVal = valueObj.getClass().getMethod("setValue", String.class);
                setVal.invoke(valueObj, newValue);
            } catch (Exception ignored) {
                System.err.println("Could not set single input value on " + cls.getName());
            }
        }
    }

    // Append a new incoming edge from src into targetNode at the next input index
    private static void appendInputEdgeForNode(FlightControlModel.Node targetNode, FlightControlModel model, FlightControlModel.Node src) {
        int maxIndex = -1;
        for (FlightControlModel.Edge e : model.edges) {
            if (e.to == targetNode && e.toInputIndex > maxIndex) {
                maxIndex = e.toInputIndex;
            }
        }
        int newIndex = maxIndex + 1;
        model.addEdge(src, targetNode, newIndex);
    }

    // Remove the Nth incoming edge (by toInputIndex ordering) for this node
    private static void removeInputEdgeAtIndex(FlightControlModel.Node targetNode, FlightControlModel model, int removeIndex) {
        List<FlightControlModel.Edge> incoming = new ArrayList<>();
        for (FlightControlModel.Edge e : model.edges) {
            if (e.to == targetNode) {
                incoming.add(e);
            }
        }
        incoming.sort((a, b) -> Integer.compare(a.toInputIndex, b.toInputIndex));
        if (removeIndex < 0 || removeIndex >= incoming.size()) return;

        FlightControlModel.Edge toRemove = incoming.get(removeIndex);
        model.edges.remove(toRemove);

        // Reindex the remaining edges
        int idx = 0;
        for (FlightControlModel.Edge e : incoming) {
            if (e == toRemove) continue;
            e.toInputIndex = idx++;
            e.updatePoints();
        }
        // Update how many input ports this node should expose
        int remaining = 0;
        for (FlightControlModel.Edge e : model.edges) {
            if (e.to == targetNode) remaining++;
        }
        int basePorts = (targetNode.type != null) ? targetNode.type.inPorts : 1;
        targetNode.inputPortCount = Math.max(basePorts, remaining);
    }

    // Sync this node's <input> list in the JAXB backing block from the current graph edges.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void syncNodeInputsFromEdges(FlightControlModel.Node targetNode, FlightControlModel model) {
        if (targetNode == null || targetNode.backingBlock == null) return;

        Object block = targetNode.backingBlock;
        Class<?> cls = block.getClass();

        // Collect incoming edges to this node
        java.util.List<FlightControlModel.Edge> incoming = new java.util.ArrayList<>();
        for (FlightControlModel.Edge e : model.edges) {
            if (e.to == targetNode) {
                incoming.add(e);
            }
        }
        incoming.sort((a, b) -> Integer.compare(a.toInputIndex, b.toInputIndex));

        // Reindex edges to keep them tidy and update geometry
        int idx = 0;
        for (FlightControlModel.Edge e : incoming) {
            e.toInputIndex = idx++;
            e.updatePoints();
        }

        // Reflect actual number of inputs, but never below the type's default
        int basePorts = (targetNode.type != null) ? targetNode.type.inPorts : 1;
        targetNode.inputPortCount = Math.max(basePorts, incoming.size());

        // Build canonical input strings in input-port order
        java.util.List<String> canonical = new java.util.ArrayList<>();
        for (FlightControlModel.Edge e : incoming) {
            FlightControlModel.Node src = e.from;
            String prop = computeCanonicalOutputProperty(src);
            if (prop != null && !prop.isBlank()) {
                canonical.add(prop.trim());
            }
        }

        //  SPECIAL CASE: <switch> blocks 
        if (targetNode.type == FlightControlModel.NodeType.SWITCH && block instanceof Switch sw) {
            java.util.List<Switch.Test> tests = sw.getTest();

            if (canonical.isEmpty()) {
                tests.clear();
            } else {
                // Ensure exactly ONE <test> element
                Switch.Test t;
                if (tests.isEmpty()) {
                    t = new Switch.Test();
                    tests.add(t);
                } else {
                    t = tests.get(0);
                    // discard any extra tests (we only want one)
                    while (tests.size() > 1) {
                        tests.remove(1);
                    }
                }

                StringBuilder body = new StringBuilder();
                for (int i = 0; i < canonical.size(); i++) {
                    if (i > 0) body.append('\n');
                    body.append(canonical.get(i));
                }

                t.setValue(body.toString());

                // Set logic explicitly and DO NOT touch valueX
                t.setLogic(ANDOR.OR);
                t.setValueX(null);
            }

            // We DO NOT use <input> for switch blocks
            return;
        }
        //  END SWITCH SPECIAL CASE 

        // For all other block types, drive their <input> property
        // from the canonical list.
        Method getter;
        try {
            getter = cls.getMethod("getInput");
        } catch (NoSuchMethodException ex) {
            try {
                getter = cls.getMethod("getInputs");
            } catch (NoSuchMethodException ex2) {
                // This node type has no generic input property; nothing to sync.
                return;
            }
        }

        Object valueObj;
        try {
            valueObj = getter.invoke(block);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        // Try to find a real List of <input> elements, even if it's wrapped
        InputListDescriptor desc = findInputListDescriptor(block, getter);

        if (desc != null) {
            writeCanonicalInputsToJaxbList(desc.container, desc.listGetter, desc.list, canonical);
        } else {
            // single-valued input property
            String first = canonical.isEmpty() ? null : canonical.get(0);
            setSingleInputValueOnBlock(block, getter, valueObj, first);
        }
    }

    // Decide on a canonical property string for the given node, used in
    // <input> elements when wiring blocks together.
    private static String computeCanonicalOutputProperty(FlightControlModel.Node node) {
        if (node == null) return null;

        // Explicit <output> from the backing block, if present
        String prop = readPrimaryOutputProperty(node);
        if (prop != null && !prop.isBlank()) {
            return prop.trim();
        }

        // Pull a "name" from either the node or its backing block
        String name = (node.displayName != null && !node.displayName.isBlank())
                ? node.displayName
                : readNameFromBackingBlock(node);

        if (node.type == FlightControlModel.NodeType.SOURCE) {
            if (name != null && !name.isBlank()) {
                String trimmed = name.trim();

                // If it already looks right, keep it
                // e.g. "velocities/p-aero-rad_sec" or "fcs/alpha-limiter"
                if (trimmed.contains("/")) {
                    return trimmed;
                }

                // Otherwise, treat it like other blocks
                String token = nameToPropertyToken(trimmed);
                if (token != null && !token.isBlank()) {
                    return token;
                }

                // Last resort: raw name
                return trimmed;
            }
        }
        // --- END UPDATED SOURCE HANDLING ---

        String token = nameToPropertyToken(name);
        if (token != null && !token.isBlank()) {
            return token;
        }

        if (name != null && !name.isBlank()) {
            return name.trim();
        }

        return node.type.label + " " + node.id;
    }

    // Try to read the primary output property name for a node
    private static String readPrimaryOutputProperty(FlightControlModel.Node node) {
        if (node == null || node.backingBlock == null) return null;

        Object block = node.backingBlock;
        Class<?> cls = block.getClass();

        try {
            Method getter;
            try {
                getter = cls.getMethod("getOutput");
            } catch (NoSuchMethodException ex1) {
                    return null;
            }

            Object listObj = getter.invoke(block);
            if (listObj instanceof java.util.List<?>) {
                java.util.List<?> list = (java.util.List<?>) listObj;
                if (!list.isEmpty() && list.get(0) != null) {
                    return extractXmlString(list.get(0));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Read the current <input> strings from this node's JAXB backing block
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static java.util.List<String> readInputStringsFromBackingBlock(FlightControlModel.Node node) {
        java.util.List<String> result = new ArrayList<>();
        if (node == null || node.backingBlock == null) return result;

        Object block = node.backingBlock;
        Class<?> cls = block.getClass();

        //  SPECIAL CASE: <switch> blocks 
        if (block instanceof Switch sw) {
            // Each <test> may contain multiple lines of text, e.g.
            //   <test logic="OR">
            //     velocities/vc-kts lt 250
            //     gear/gear-cmd-norm == 1
            //   </test>
            //
            // We treat the FIRST token on each non-empty line as the "input"
            for (Switch.Test t : sw.getTest()) {
                String raw = t.getValue();
                if (raw != null && !raw.isBlank()) {
                    String[] lines = raw.split("\\r?\\n");
                    for (String line : lines) {
                        String trimmed = line.trim();
                        if (trimmed.isEmpty()) continue;

                        String[] toks = trimmed.split("\\s+");
                        if (toks.length > 0) {
                            String path = toks[0];
                            if (path != null && !path.isBlank()) {
                                result.add(path.trim());
                            }
                        }
                    }
                } else {
                    // Fallback: parse valueX in the same way, in case some configs
                    // store the paths there instead of the text body.
                    String vX = t.getValueX();
                    if (vX != null && !vX.isBlank()) {
                        String[] lines = vX.split("[\\r\\n]+");
                        for (String line : lines) {
                            String trimmed = line.trim();
                            if (trimmed.isEmpty()) continue;

                            String[] toks = trimmed.split("\\s+");
                            if (toks.length > 0) {
                                String path = toks[0];
                                if (path != null && !path.isBlank()) {
                                    result.add(path.trim());
                                }
                            }
                        }
                    }
                }
            }

            // If there are no tests but there is a primary <input>
            if (result.isEmpty()) {
                String in = sw.getInput();
                if (in != null && !in.isBlank()) {
                    result.add(in.trim());
                }
            }

            return result;
        }
        //  END SWITCH SPECIAL CASE 

        try {
            // Try getInput() first, then getInputs()
            Method getter;
            try {
                getter = cls.getMethod("getInput");
            } catch (NoSuchMethodException ex) {
                try {
                    getter = cls.getMethod("getInputs");
                } catch (NoSuchMethodException ex2) {
                    System.err.println("Backing block has no getInput/getInputs: " + cls.getName());
                    return result;
                }
            }

            Object valueObj = getter.invoke(block);

            // Prefer treating it as a List
            InputListDescriptor desc = findInputListDescriptor(block, getter);
            if (desc != null) {
                for (Object o : desc.list) {
                    String s = extractXmlString(o);
                    if (s != null) {
                        result.add(s);
                    }
                }
            } else if (valueObj != null) {
                // Truly single-valued property
                String s = extractXmlString(valueObj);
                if (s != null) {
                    result.add(s);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    // Find a node whose primary output matches the given XML path, with a relaxed comparison
    private static FlightControlModel.Node findSourceNodeByOutputProperty(FlightControlModel.Node targetNode, FlightControlModel model, String xmlPath) {
        if (xmlPath == null || xmlPath.isBlank()) return null;

        String targetClean = cleanPropertyLabel(xmlPath).toLowerCase();

        for (FlightControlModel.Node n : model.nodes) {
            if (n == targetNode) continue;

            // Don't connect DESTINATIONs directly to SOURCE blocks
            if (targetNode.type == FlightControlModel.NodeType.DESTINATION &&
                n.type == FlightControlModel.NodeType.SOURCE) {
                continue;
            }
            String out = computeCanonicalOutputProperty(n);
            if (out == null || out.isBlank()) continue;

            // exact match first
            if (out.equals(xmlPath)) {
                return n;
            }

            // relaxed comparison: ignore prefix, dashes, case
            String outClean = cleanPropertyLabel(out).toLowerCase();
            if (outClean.equals(targetClean)) {
                return n;
            }
        }
        return null;
    }

    // Push the current list of canonical input strings (from the Inputs tab)
    // into the node's JAXB backing block. Supports both list-style and
    // single-style input properties.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void syncInputsListToBackingBlock(FlightControlModel.Node node, DefaultListModel<String> listModel) {
        if (node == null || node.backingBlock == null) return;

        Object block = node.backingBlock;
        Class<?> cls = block.getClass();

        //  SPECIAL CASE: <switch> blocks 
        if (node.type == FlightControlModel.NodeType.SWITCH && block instanceof Switch sw) {
            java.util.List<String> canonical = new java.util.ArrayList<>();
            for (int i = 0; i < listModel.getSize(); i++) {
                String s = listModel.getElementAt(i);
                if (s != null) s = s.trim();
                if (s == null || s.isEmpty()) continue;
                canonical.add(s);
            }

            java.util.List<Switch.Test> tests = sw.getTest();

            if (canonical.isEmpty()) {
                tests.clear();
            } else {
                // Ensure exactly ONE <test> element
                Switch.Test t;
                if (tests.isEmpty()) {
                    t = new Switch.Test();
                    tests.add(t);
                } else {
                    t = tests.get(0);
                    while (tests.size() > 1) {
                        tests.remove(1);
                    }
                }

                // Multi-line text body, 1 line per input property
                StringBuilder body = new StringBuilder();
                for (int i = 0; i < canonical.size(); i++) {
                    if (i > 0) body.append('\n');
                    body.append(canonical.get(i));
                }

                t.setValue(body.toString());
                t.setLogic(ANDOR.OR); // same default; can be edited in XML if needed
                t.setValueX(null);    // don't emit valueX
            }

            return;
        }
        //  END SWITCH SPECIAL CASE 

        try {
            Method getter;
            try {
                getter = cls.getMethod("getInput");
            } catch (NoSuchMethodException ex) {
                try {
                    getter = cls.getMethod("getInputs");
                } catch (NoSuchMethodException ex2) {
                    System.err.println("Backing block has no getInput/getInputs: " + cls.getName());
                    return;
                }
            }

            Object valueObj = getter.invoke(block);

            java.util.List<String> canonical = new java.util.ArrayList<>();
            for (int i = 0; i < listModel.getSize(); i++) {
                String s = listModel.getElementAt(i);
                if (s != null) s = s.trim();
                if (s == null || s.isEmpty()) continue;
                canonical.add(s);
            }

            // Try to find a real List of <input> elements, even if wrapped
            InputListDescriptor desc = findInputListDescriptor(block, getter);
            if (desc != null) {
                writeCanonicalInputsToJaxbList(desc.container, desc.listGetter, desc.list, canonical);
            } else {
                // Single-valued property: use the first string only
                String first = canonical.isEmpty() ? null : canonical.get(0);
                setSingleInputValueOnBlock(block, getter, valueObj, first);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // --- Generic popup for other node types ---
    private static void openGenericPopup(FlightControlModel.Node node, FlightControlView view, String title, String typeLabel, String defaultOrder) {
        JDialog d = new JDialog();
        d.setTitle(title);
        d.setSize(400, 300);
        d.setLocationRelativeTo(null);
        d.setModal(true);
        d.setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        JPanel basicTab = buildSimpleBasicTab(node, typeLabel, defaultOrder);
        tabs.addTab("Basic", basicTab);

        d.add(tabs, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        okBtn.addActionListener(e -> {
            JPanel basicPanel = (JPanel) tabs.getComponentAt(0);
            JTextField nameField = (JTextField) basicPanel.getClientProperty("nameField");
            if (nameField != null) {
                writeNameToBackingBlock(node, nameField.getText());
            }
            view.repaint();
            d.dispose();
        });

        cancelBtn.addActionListener(e -> d.dispose());

        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        d.add(buttonPanel, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    private static JPanel buildSimpleBasicTab(FlightControlModel.Node node, String typeLabel, String defaultOrder) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        String nameText = (node.displayName != null && !node.displayName.isBlank())
                ? node.displayName
                : readNameFromBackingBlock(node);
        if (nameText == null)
            nameText = "";

        JTextField nameField = new JTextField(nameText);
        panel.add(makeRow("Name:", nameField));
        panel.add(Box.createVerticalStrut(8));
        panel.putClientProperty("nameField", nameField);

        JTextField typeField = new JTextField(typeLabel);
        typeField.setEditable(false);
        panel.add(makeRow("Type:", typeField));
        panel.add(Box.createVerticalStrut(8));

        JTextField orderField = new JTextField(defaultOrder);
        panel.add(makeRow("Order:", orderField));
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    // Rebuild the in-memory edge list (model.edges) from whatever is currently
    // stored in each node's JAXB backing block.
    //
    // Goes from XML to graph. Call it once after you have created
    // all nodes and bound their backingBlock fields from the FdmConfig.
    //
    // It understands both ordinary <input> lists and the <switch><test>...</test>
    // format we are using for switches.
    public static void rebuildEdgesFromBackingBlocks(FlightControlModel model) {
        if (model == null) return;

        // Start from a clean slate
        model.edges.clear();

        // FIRST PASS: normal blocks with <input> lists
        for (FlightControlModel.Node target : model.nodes) {
            if (target.backingBlock == null) {
                continue;
            }

            java.util.List<String> inputs = readInputStringsFromBackingBlock(target);
            if (inputs == null || inputs.isEmpty()) {
                continue;
            }

            int inputIndex = 0;
            for (String xmlPath : inputs) {
                if (xmlPath == null || xmlPath.isBlank()) {
                    continue;
                }

                FlightControlModel.Node src =
                        findSourceNodeByOutputProperty(target, model, xmlPath);

                if (src == null) {
                    System.err.println(
                            "rebuildEdgesFromBackingBlocks: could not find source for \"" +
                            xmlPath + "\" feeding " + target.type.label +
                            " (node id " + target.id + ")");
                    continue;
                }

                model.addEdge(src, target, inputIndex++);
            }

            int basePorts = (target.type != null) ? target.type.inPorts : 1;
            target.inputPortCount = Math.max(basePorts, inputIndex);
        }

        // SECOND PASS: auto-wire DESTINATION nodes
        for (FlightControlModel.Node dest : model.nodes) {
            if (dest.type != FlightControlModel.NodeType.DESTINATION) {
                continue;
            }

            // Skip if this destination already has an incoming edge (e.g. user-drawn)
            boolean hasIncoming = false;
            for (FlightControlModel.Edge e : model.edges) {
                if (e.to == dest) {
                    hasIncoming = true;
                    break;
                }
            }
            if (hasIncoming) continue;

            String label = (dest.displayName != null && !dest.displayName.isBlank())
                    ? dest.displayName
                    : readNameFromBackingBlock(dest);

            if (label == null || label.isBlank()) {
                continue;
            }

            // If the label already looks like "fcs/..." keep it; otherwise
            // turn "Elevator Command" into "fcs/elevator-command"
            String xmlPath;
            if (label.contains("/")) {
                xmlPath = label.trim();
            } else {
                xmlPath = nameToPropertyToken(label);
                if (xmlPath == null || xmlPath.isBlank()) {
                    continue;
                }
            }

            FlightControlModel.Node src =
                    findSourceNodeByOutputProperty(dest, model, xmlPath);

            if (src == null) {
                System.err.println(
                        "rebuildEdgesFromBackingBlocks: could not find source for DESTINATION \"" +
                        xmlPath + "\" (node id " + dest.id + ")");
                continue;
            }

            // One input port (index 0) feeding the destination
            model.addEdge(src, dest, 0);
            dest.inputPortCount = Math.max(dest.inputPortCount, 1);
        }

        // Recompute geometry for all edges (including the new DESTINATION ones)
        for (FlightControlModel.Edge e : model.edges) {
            e.updatePoints();
        }
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

        // Prevent Filter connecting directly to Destination
        if (src == FlightControlModel.NodeType.FILTER && dst == FlightControlModel.NodeType.DESTINATION) {
            return false;
        }

        // Default: allow connection
        return true;
    }

    public static void attachToPanel(javax.swing.JPanel host, FlightControlModel model, FlightControlView view) {

        // If this model came from XML, rebuild the edge list from the JAXB blocks
        rebuildEdgesFromBackingBlocks(model);

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

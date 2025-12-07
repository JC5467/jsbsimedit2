package uta.cse3310.tab.concreteTabs.flightcontrol;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import generated.Channel;
import generated.FcsModel;
import generated.FdmConfig;
import jakarta.xml.bind.JAXBElement;
import uta.cse3310.commander.model.FlightControlModel;


public class FlightControlXMLLoader {

   
    public static void loadChannel(FdmConfig cfg, FlightControlModel model, String channelName) {
        // Clear current graph
        model.nodes.clear();
        model.edges.clear();

        if (cfg == null || channelName == null) return;

        List<JAXBElement<FcsModel>> list = cfg.getSystemOrAutopilotOrFlightControl();
        if (list == null) return;

        for (JAXBElement<FcsModel> element : list) {
            // Find the <flight_control> entry
            if (!"flight_control".equalsIgnoreCase(element.getName().getLocalPart())) {
                continue;
            }

            FcsModel fc = element.getValue();
            if (fc == null) continue;

            for (Object obj : fc.getDocumentationOrLimitationOrProperty()) {
                if (!(obj instanceof Channel)) continue;
                Channel ch = (Channel) obj;
                if (ch.getName() == null) continue;

                if (!ch.getName().equalsIgnoreCase(channelName)) continue;

                // We found the channel we're looking for
                List<Object> blocks = ch.getAccelerometerOrActuatorOrAerosurfaceScale();
                if (blocks == null || blocks.isEmpty()) return;

                int blockX = 260;
                int baseY  = 80;
                int gapY   = 120;

                // Create a node for each FCS block in this channel
                Map<Object, FlightControlModel.Node> blockToNode = new HashMap<>();
                int idx = 0;
                for (Object block : blocks) {
                    if (block == null) continue;

                    String typeName = block.getClass().getSimpleName();
                    FlightControlModel.NodeType nodeType = mapClassNameToNodeType(typeName);

                    FlightControlModel.Node node =
                        model.addNode(nodeType, blockX, baseY + idx * gapY);

                    // NEW: remember which JAXB block this node represents
                    node.backingBlock = block;

                    String blockName = reflectiveGetName(block);
                    if (blockName == null || blockName.isBlank()) {
                        blockName = typeName;
                    }
                    node.displayName = blockName;

                    blockToNode.put(block, node);
                    idx++;
                }

                // Index outputs by property token (wiring & destinations)
                Map<String, FlightControlModel.Node> outputTokenToNode = new HashMap<>();
                Map<String, String> explicitOutputLabel = new HashMap<>();
                Set<String> explicitOutputTokens = new HashSet<>();

                for (Object block : blocks) {
                    FlightControlModel.Node producer = blockToNode.get(block);
                    if (producer == null) continue;

                    String rawOut = reflectiveGetOutput(block);
                    if (rawOut != null && !rawOut.isBlank() && !"[]".equals(rawOut.trim())) {
                        String key = normalizeToken(rawOut);
                        if (key != null) {
                            // used for both wiring ~and~ destinations
                            outputTokenToNode.put(key, producer);
                            explicitOutputTokens.add(key);
                            explicitOutputLabel.put(key, rawOut.trim());
                        }
                    }

                    // JSBSim property from the block's name for wiring only
                    String blockName = reflectiveGetName(block);
                    if (blockName != null && !blockName.isBlank()) {
                        String implicitProp = nameToPropertyToken(blockName); // e.g. "g load command" becomes "fcs/g-load-command"
                        if (implicitProp != null && !implicitProp.isBlank()) {
                            String key2 = normalizeToken(implicitProp);
                            if (key2 != null && !outputTokenToNode.containsKey(key2)) {
                                // used only for wiring, not destinations
                                outputTokenToNode.put(key2, producer);
                            }
                        }
                    }
                }

                // Wire edges and create Source nodes for external inputs
                Map<String, FlightControlModel.Node> sourceNodes = new HashMap<>();
                Set<String> usedOutputTokens = new HashSet<>();

                int sourceX = blockX - 200;
                int sourceCount = 0;

                for (Object block : blocks) {
                    FlightControlModel.Node target = blockToNode.get(block);
                    if (target == null) continue;

                    List<String> inputs = reflectiveGetInputs(block);
                    for (String rawInput : inputs) {
                        if (rawInput == null) continue;

                        String key = normalizeToken(rawInput);
                        if (key == null) continue;

                        // Check if coming from another block's output
                        FlightControlModel.Node producer = outputTokenToNode.get(key);
                        if (producer != null) {
                            model.addEdge(producer, target);
                            usedOutputTokens.add(key);
                            continue;
                        }

                        // Otherwise, treat this as a source node
                        FlightControlModel.Node src = sourceNodes.get(key);
                        if (src == null) {
                            int y = baseY + sourceCount * gapY;
                            src = model.addNode(FlightControlModel.NodeType.SOURCE, sourceX, y);
                            src.displayName = rawInput.trim();
                            sourceNodes.put(key, src);
                            sourceCount++;
                        }
                        model.addEdge(src, target);
                    }
                }

                // Create Destination nodes for outputs that don't feed any other block
                Map<String, FlightControlModel.Node> destNodes = new HashMap<>();
                int destX = blockX + 200;
                int destCount = 0;

                for (String outKey : explicitOutputTokens) {
                    if (usedOutputTokens.contains(outKey)) continue;

                    FlightControlModel.Node producer = outputTokenToNode.get(outKey);
                    if (producer == null) continue;

                    int y = baseY + destCount * gapY;
                    FlightControlModel.Node dest =
                            model.addNode(FlightControlModel.NodeType.DESTINATION, destX, y);

                    String label = explicitOutputLabel.get(outKey);
                    dest.displayName = (label != null ? label : outKey);

                    destNodes.put(outKey, dest);
                    destCount++;

                    model.addEdge(producer, dest);
                }

                // Re-layout the entire graph for this channel
                autoLayout(model);

                return;
            }
        }
    }

    // Helper to unwrap JAXB elements to plain strings
    private static String unwrapTokenObject(Object v) {
        if (v == null) return null;
        if (v instanceof JAXBElement<?>) {
            Object inner = ((JAXBElement<?>) v).getValue();
            return inner != null ? inner.toString() : null;
        }
        return v.toString();
    }

    // Normalize property tokens for matching (trim, lowercase, drop initial '-')
    private static String normalizeToken(String token) {
        if (token == null) return null;
        String t = token.trim();
        if (t.isEmpty()) return null;
        if (t.startsWith("-")) {
            t = t.substring(1).trim();
        }
        return t.toLowerCase();
    }

    private static String nameToPropertyToken(String name) {
        if (name == null) return null;
        String slug = name.trim().toLowerCase();
        if (slug.isEmpty()) return null;

        // Replace any run of non-alphanumeric with a single dash
        slug = slug.replaceAll("[^a-z0-9]+", "-");

        // Trim leading/trailing dashes
        slug = slug.replaceAll("^-+", "").replaceAll("-+$", "");
        if (slug.isEmpty()) return null;

        return "fcs/" + slug;
    }

    // Lay out nodes in layered columns based on the graph structure.
    // - Sources start in layer 0,
    // - Other indegree-0 nodes start in layer 1
    // - Children are pushed one layer to the right
    // - Destinations will end up in the rightmost layers
    private static void autoLayout(FlightControlModel model) {
        if (model.nodes.isEmpty()) return;

        // Build indegree and adjacency
        Map<FlightControlModel.Node, Integer> indegree = new HashMap<>();
        Map<FlightControlModel.Node, List<FlightControlModel.Node>> children = new HashMap<>();

        for (FlightControlModel.Node n : model.nodes) {
            indegree.put(n, 0);
            children.put(n, new ArrayList<>());
        }

        for (FlightControlModel.Edge e : model.edges) {
            FlightControlModel.Node from = e.from;
            FlightControlModel.Node to = e.to;
            children.get(from).add(to);
            indegree.put(to, indegree.get(to) + 1);
        }

        // Compute layer numbers with BFS ish topological traversal:
        Map<FlightControlModel.Node, Integer> layer = new HashMap<>();
        Deque<FlightControlModel.Node> queue = new ArrayDeque<>();

        // Start with all nodes that have no incoming edges
        for (FlightControlModel.Node n : model.nodes) {
            if (indegree.get(n) == 0) {
                int baseLayer = (n.type == FlightControlModel.NodeType.SOURCE) ? 0 : 1;
                layer.put(n, baseLayer);
                queue.add(n);
            }
        }

        // Propagate layers to children
        while (!queue.isEmpty()) {
            FlightControlModel.Node n = queue.removeFirst();
            int myLayer = layer.getOrDefault(n, 0);

            for (FlightControlModel.Node child : children.get(n)) {
                int current = layer.getOrDefault(child, 1);
                int candidate = Math.max(current, myLayer + 1);
                layer.put(child, candidate);

                int deg = indegree.get(child) - 1;
                indegree.put(child, deg);
                if (deg == 0) {
                    queue.add(child);
                }
            }
        }

        // Any nodes not assigned a layer get pushed to the right
        int maxLayer = 0;
        for (int v : layer.values()) {
            if (v > maxLayer) maxLayer = v;
        }
        for (FlightControlModel.Node n : model.nodes) {
            if (!layer.containsKey(n)) {
                int defaultLayer =
                        (n.type == FlightControlModel.NodeType.DESTINATION) ? maxLayer + 1 : 1;
                layer.put(n, defaultLayer);
            }
        }

        // Group nodes by layer
        Map<Integer, List<FlightControlModel.Node>> byLayer = new HashMap<>();
        for (Map.Entry<FlightControlModel.Node, Integer> e : layer.entrySet()) {
            byLayer.computeIfAbsent(e.getValue(), k -> new ArrayList<>()).add(e.getKey());
        }

        // Sort layers and nodes within each layer for stable layout
        List<Integer> layers = new ArrayList<>(byLayer.keySet());
        Collections.sort(layers);

        int baseX    = 80;  // left margin
        int baseY    = 80;  // top margin
        int colWidth = 170; // horizontal spacing between columns
        int rowGap   = 110; // vertical spacing between nodes

        for (Integer l : layers) {
            List<FlightControlModel.Node> column = byLayer.get(l);
            // sort by id so layout is stable.
            column.sort((a, b) -> Integer.compare(a.id, b.id));

            int x = baseX + l * colWidth;
            for (int i = 0; i < column.size(); i++) {
                FlightControlModel.Node n = column.get(i);
                int y = baseY + i * rowGap;
                n.bounds.x = x;
                n.bounds.y = y;
            }
        }

        // Recompute edge attachment points
        for (FlightControlModel.Edge e : model.edges) {
            e.updatePoints();
        }
    }


    private static FlightControlModel.NodeType mapClassNameToNodeType(String typeName) {
        if (typeName == null) return FlightControlModel.NodeType.FCSFUNCTION;
        switch (typeName) {
            case "Summer":
                return FlightControlModel.NodeType.SUMMER;
            case "PureGain":
                return FlightControlModel.NodeType.GAIN;
            case "AerosurfaceScale":
                return FlightControlModel.NodeType.GAIN;
            case "Kinematic":
                return FlightControlModel.NodeType.KINEMAT;
            case "Switch":
                return FlightControlModel.NodeType.SWITCH;
            case "LeadLagFilter":
                return FlightControlModel.NodeType.FILTER;
            case "Integrator":
                return FlightControlModel.NodeType.PID;
            case "ScheduledGain":
                return FlightControlModel.NodeType.GAIN;
            default:
                return FlightControlModel.NodeType.FCSFUNCTION;
        }
    }

    private static String reflectiveGetName(Object o) {
        try {
            var m = o.getClass().getMethod("getName");
            Object v = m.invoke(o);
            return v != null ? v.toString() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    // Get <input> 
    private static List<String> reflectiveGetInputs(Object o) {
        List<String> out = new ArrayList<>();
        try {
            Method m = o.getClass().getMethod("getInput");
            Object v = m.invoke(o);
            if (v instanceof List<?>) {
                for (Object item : (List<?>) v) {
                    String s = unwrapTokenObject(item);
                    if (s != null) out.add(s);
                }
            } else {
                String s = unwrapTokenObject(v);
                if (s != null) out.add(s);
            }
        }
        catch (Exception ignore) {}

        try {
            Method m2 = o.getClass().getMethod("getInputs");
            Object v2 = m2.invoke(o);
            if (v2 instanceof List<?>) {
                for (Object item : (List<?>) v2) {
                    String s = unwrapTokenObject(item);
                    if (s != null) out.add(s);
                }
            }
        }
        catch (Exception ignore) {}
        return out;
    }

    private static String reflectiveGetOutput(Object o) {
        try {
            Method m = o.getClass().getMethod("getOutput");
            Object v = m.invoke(o);

            if (v == null) return null;

            // If it's a List, grab the first non-empty item
            if (v instanceof java.util.List<?>) {
                for (Object item : (java.util.List<?>) v) {
                    String s = unwrapTokenObject(item);
                    if (s != null && !s.isBlank()) {
                        return s;
                    }
                }
                return null;
            }

            // Otherwise unwrap as usual
            String s = unwrapTokenObject(v);
            if (s != null && !"[]".equals(s.trim()) && !s.trim().isEmpty()) {
                return s;
            }
            return null;
            
        } catch (Exception ignore) {
            return null;
        }
    }
}

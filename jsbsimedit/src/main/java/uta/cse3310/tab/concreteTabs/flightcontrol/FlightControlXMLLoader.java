package uta.cse3310.tab.concreteTabs.flightcontrol;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.JAXBElement;

import generated.FdmConfig;
import generated.FcsModel;
import generated.Channel;

import uta.cse3310.commander.model.FlightControlModel;


public class FlightControlXMLLoader {

   
    public static void loadChannel(FdmConfig cfg, FlightControlModel model, String channelName) {
        if (cfg == null) return;
        List<JAXBElement<FcsModel>> list = cfg.getSystemOrAutopilotOrFlightControl();
        if (list == null) return;

        for (JAXBElement<FcsModel> element : list) {
            // Find the flight_control JAXB element among system/autopilot/flight_control
            if (!"flight_control".equalsIgnoreCase(element.getName().getLocalPart())) continue;
            FcsModel fc = element.getValue();
            if (fc == null) continue;

            for (Object obj : fc.getDocumentationOrLimitationOrProperty()) {
                if (!(obj instanceof Channel)) continue; 
                Channel ch = (Channel) obj;
                if (ch.getName() == null) continue;

                if (!ch.getName().equalsIgnoreCase(channelName)) continue; // not our channel

                // We found the requested channel. Now iterate its child elements.Channel has a property which contains elements like Summer, PureGain, Pid, Integrator, etc.
                
                List<Object> blocks = ch.getAccelerometerOrActuatorOrAerosurfaceScale();
                if (blocks == null) return;
                
                int x = 100;
                int yStart = 80;
                int gapY = 120;

                Map<String, FlightControlModel.Node> tokenToNode = new HashMap<>();


                for (int i = 0; i < blocks.size(); i++) {
                    Object block = blocks.get(i);
                    if (block == null) continue;

                    String typeName = block.getClass().getSimpleName(); // e.g., Summer, PureGain, Pid
                    FlightControlModel.NodeType nodeType = mapClassNameToNodeType(typeName);

                    FlightControlModel.Node node =
                        model.addNode(nodeType, x, yStart + model.nodes.size() * gapY);

                    String blockName = reflectiveGetName(block);
                    
                    if (blockName == null){
                        blockName = typeName;

                    }

                    tokenToNode.put(blockName.toLowerCase(), node);

                    String output = reflectiveGetOutput(block);
                    if (output != null && !output.isBlank()) {
                        tokenToNode.put(output.toLowerCase().trim(), node);
                    }

                }

                for (Object block : blocks) {
                    if (block == null) {
                    }

                    String blockName = reflectiveGetName(block);
                    if (blockName == null)
                        blockName = block.getClass().getSimpleName();

                    FlightControlModel.Node target = tokenToNode.get(blockName.toLowerCase());
                    if (target == null) continue;

                    // list of input tokens from reflection
                    List<String> inputs = reflectiveGetInputs(block);

                    for (String inputTok : inputs) {
                        if (inputTok == null) continue;
                        String tok = inputTok.toLowerCase().trim();

                        
                        if (tok.startsWith("-"))
                            tok = tok.substring(1);

                        FlightControlModel.Node source = tokenToNode.get(tok);

                        if (source != null) {
                            model.addEdge(source, target);
                        }
                    }
                }

                System.out.println("FlightControlXMLLoader: building edges for channel " + channelName);

                // Map block -> node
                Map<Object, FlightControlModel.Node> nodeMap = new HashMap<>();
                for (int i = 0; i < blocks.size(); i++) {
                    nodeMap.put(blocks.get(i), model.nodes.get(i));
                }

                // Map output token → node (for wiring)
                Map<String, FlightControlModel.Node> outputMap = new HashMap<>();

                for (Object block : blocks) {
                    String output = reflectiveGetOutput(block);
                    if (output != null) {
                        outputMap.put(output.trim(), nodeMap.get(block));
                    }

                    String blockName = block.getClass().getSimpleName();
                    outputMap.put(blockName, nodeMap.get(block));
                }

                for (Object block : blocks) {
                    FlightControlModel.Node targetNode = nodeMap.get(block);
                    if (targetNode == null) continue;

                    List<String> inputs = reflectiveGetInputs(block);
                    for (String input : inputs) {
                        if (input == null) continue;
                        input = input.trim();

                        FlightControlModel.Node sourceNode = outputMap.get(input);

                        if (sourceNode == null && input.startsWith("-")) {
                            sourceNode = outputMap.get(input.substring(1));
                        }

                        if (sourceNode != null) {
                            model.addEdge(sourceNode, targetNode);
                        }
                    }
                }
                
                

                

                return;
            }
        }
    }

    private static FlightControlModel.NodeType mapClassNameToNodeType(String className) {
        if (className == null) return FlightControlModel.NodeType.GAIN;

        return switch (className.toLowerCase()) {
            case "summer" -> FlightControlModel.NodeType.SUMMER;
            case "puregain" -> FlightControlModel.NodeType.GAIN;
            case "pid" -> FlightControlModel.NodeType.PID;
            case "leadlagfilter" -> FlightControlModel.NodeType.FILTER;
            case "lagfilter" -> FlightControlModel.NodeType.FILTER;
            case "switch" -> FlightControlModel.NodeType.SWITCH;
            case "kinematic" -> FlightControlModel.NodeType.KINEMAT;
            case "fcsfunction" -> FlightControlModel.NodeType.FCSFUNCTION;
            default -> FlightControlModel.NodeType.GAIN;
        };
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

    //Get <input> tokens
    private static List<String> reflectiveGetInputs(Object o) {
        List<String> out = new ArrayList<>();
        try {
            Method m = o.getClass().getMethod("getInput");
            Object v = m.invoke(o);
            if (v instanceof List) {
                for (Object item : (List<?>) v) out.add(item.toString());
            } else if (v != null) out.add(v.toString());
        }
        catch (Exception ignore) {}

        try {
            Method m2 = o.getClass().getMethod("getInputs");
            Object v2 = m2.invoke(o);
            if (v2 instanceof List) {
                for (Object item : (List<?>) v2) out.add(item.toString());
            }
        }
        catch (Exception ignore) {}
        return out;
    }

    private static String reflectiveGetOutput(Object o) {
        try {
            Method m = o.getClass().getMethod("getOutput");
            Object v = m.invoke(o);
            return v != null ? v.toString() : null;
        } catch (Exception ignore) {
            return null;
        }
    }

}

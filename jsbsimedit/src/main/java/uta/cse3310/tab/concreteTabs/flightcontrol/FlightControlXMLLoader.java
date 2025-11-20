package uta.cse3310.tab.concreteTabs.flightcontrol;

import java.util.List;
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
                int x = 100;
                int yStart = 80;
                int gapY = 120;

                for (Object block : ch.getAccelerometerOrActuatorOrAerosurfaceScale()) {
                    if (block == null) continue;

                    String typeName = block.getClass().getSimpleName(); // e.g., Summer, PureGain, Pid
                    FlightControlModel.NodeType nodeType = mapClassNameToNodeType(typeName);

                    model.addNode(nodeType, x, yStart + model.nodes.size() * gapY);
                }

                return;
            }
        }
    }

    private static FlightControlModel.NodeType mapClassNameToNodeType(String className) {
        if (className == null) return FlightControlModel.NodeType.GAIN;

        switch (className.toLowerCase()) {
            case "summer":         return FlightControlModel.NodeType.SUMMER;
            case "puregain":       return FlightControlModel.NodeType.GAIN;
            case "pid":            return FlightControlModel.NodeType.PID;
            case "leadlagfilter":  return FlightControlModel.NodeType.FILTER;
            case "lagfilter":      return FlightControlModel.NodeType.FILTER;
            case "switch":         return FlightControlModel.NodeType.SWITCH;
            case "kinematic":      return FlightControlModel.NodeType.KINEMAT;
            case "fcsfunction":    return FlightControlModel.NodeType.FCSFUNCTION;

            default:               return FlightControlModel.NodeType.GAIN;
        }
    }
}

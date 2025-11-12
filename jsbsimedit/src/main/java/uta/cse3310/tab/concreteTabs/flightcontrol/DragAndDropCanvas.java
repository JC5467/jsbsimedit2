package uta.cse3310.commander.view.flightcontrol;

import javax.swing.*;
import java.awt.*;

import uta.cse3310.commander.model.FlightControlModel;
import uta.cse3310.commander.view.flightcontrol.FlightControlView;
import uta.cse3310.commander.controller.FlightControlController;


public class DragAndDropCanvas {

    private JPanel root;

    public DragAndDropCanvas() {
        root = new JPanel(new BorderLayout());

        //blank inner panel
        JPanel hostPanel = new JPanel(new BorderLayout());

        FlightControlController.start(hostPanel);

        root.add(hostPanel, BorderLayout.CENTER);
    }

    public JComponent getPanel() {
        return root;
    }
}

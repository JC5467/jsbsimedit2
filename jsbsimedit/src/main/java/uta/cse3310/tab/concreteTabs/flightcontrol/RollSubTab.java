package uta.cse3310.tab.concreteTabs.flightcontrol;

import javax.swing.*;
import java.awt.BorderLayout;

import uta.cse3310.tab.concreteTabs.flightcontrol.DragAndDropCanvas;

public class RollSubTab {
    private JPanel panel;

    public JComponent buildPanel() {
        panel = new JPanel(new BorderLayout());
        
        DragAndDropCanvas canvas = new DragAndDropCanvas();
        panel.add(canvas.getPanel(), BorderLayout.CENTER);
        
        return panel;
    }

    //later add a load from XML method
}

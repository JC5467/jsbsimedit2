package uta.cse3310.tab.concreteTabs.flightcontrol;

import javax.swing.*;
import java.awt.BorderLayout;

import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;
import uta.cse3310.commander.model.FlightControlModel;
import uta.cse3310.tab.concreteTabs.flightcontrol.FlightControlXMLLoader;
import uta.cse3310.tab.concreteTabs.flightcontrol.FlightControlView;
import uta.cse3310.commander.controller.FlightControlController;

public class CanopySubTab {
    private tabFrame TF;
    private dataStore DS;

    private JPanel panel;
    
    private FlightControlView view;
    private FlightControlModel model;

    public CanopySubTab(tabFrame tf, dataStore ds, String label){
        this.TF = tf;
        this.DS = ds;
        panel = new JPanel(new BorderLayout());

        model = new FlightControlModel();
        view = new FlightControlView(model);
    }

    public JComponent buildPanel() {
        /** 
        panel = new JPanel(new BorderLayout());
        
        DragAndDropCanvas canvas = new DragAndDropCanvas();
        panel.add(canvas.getPanel(), BorderLayout.CENTER);
        **/
        return panel;
    }

    //later add a load from XML method
    public void loadData() {

        model.nodes.clear();
        model.edges.clear();

        if( DS == null || DS.cfg == null) {
            panel.removeAll();
            panel.add(new JLabel("No XML loaded yet", SwingConstants.CENTER), BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();
            return;
        }

        FlightControlXMLLoader.loadChannel(DS.cfg, model, "Canopy");

        if (model.nodes.isEmpty()) {
            panel.removeAll();
            panel.add(new JLabel("No Canopy channel found in this aircraft configuration", SwingConstants.CENTER), BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();
            return;
        }

        FlightControlController.attachToPanel(panel, model, view);

        panel.revalidate();
        panel.repaint();


    }
}

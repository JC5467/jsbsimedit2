package uta.cse3310.tab.concreteTabs.flightcontrol;

import javax.swing.*;
import java.awt.BorderLayout;

import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

public class ThrottleSubTab {
    private tabFrame TF;
    private dataStore DS;

    private JPanel panel;

    public ThrottleSubTab(tabFrame tf, dataStore ds, String label){
        this.TF = tf;
        this.DS = ds;
    }

    public JComponent buildPanel() {
        panel = new JPanel(new BorderLayout());
        
        DragAndDropCanvas canvas = new DragAndDropCanvas();
        panel.add(canvas.getPanel(), BorderLayout.CENTER);
        
        return panel;
    }

    //later add a load from XML method
            public void loadData() {
        System.out.println("ThrottleSubTab: loadData called");

        if( DS == null || DS.cfg == null) {
            System.out.println("ThrottleSubTab: No XML loaded yet");
            return;
        }


        //later will need to extract specific channel elements for this tab
    }
}

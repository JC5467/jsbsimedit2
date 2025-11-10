package uta.cse3310.tab.concreteTabs;


import javax.swing.SwingUtilities;

import uta.cse3310.commander.controller.FlightControlController;
import uta.cse3310.dataStore;
import uta.cse3310.tab.simpleTab;
import uta.cse3310.tabFrame;

public class FlightControlTab extends simpleTab {
    // attributes that only pertain to the 'concrete' tab called oneTab

    public FlightControlTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        System.out.println("in Flight Control constructor");

        // this is what is unique about 1 tab
        TF = tf;
        //panel.add(new JLabel("Content of Tab 8", SwingConstants.CENTER), BorderLayout.CENTER);
        //System.out.println("data structure is " + DS.valid + " and the version is " + DS.version);
        
        //Initialize Controller for Flight Control Tab
        SwingUtilities.invokeLater(() -> FlightControlController.start(panel));
    }

    // public void loadData() {
    //     System.out.println("this is in loadData() for fileHeader");
    //     System.out.println("data structure is " + DS.valid + " and the version is " + DS.version);

    //     // here is the data to put into widgets.....
    //     System.out.println(DS.cfg.getFileheader().getCopyright());
    //     System.out.println(DS.cfg.getFileheader().getVersion());
    //     // the can be lists and individual items
    // }
}

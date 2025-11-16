package uta.cse3310.tab;

import uta.cse3310.dataStore;

import javax.swing.*;
import java.awt.*;

/*
The object that will be used to hold the XML information is in dataStore.java
- cfg is the name of said object --  FdmConfig cfg = (FdmConfig) um.unmarshal(file);
takes your f16.xml and, using JAXB and the generated package, turns it into a full Java object graph:
You can access other sections using cfg for ex - cfg.getMassBalance(), cfg.getName(), etc.

Within your tab add import generated.FdmConfig; above with the other libraries.

to share this obj within baseTab that will then be shared to other tabs add
    - protected static FdmConfig fdmConfig;
Then use getters and setters to call on the object
For example -
 // Called once from App after XML is loaded
    public static void setFdmConfig(FdmConfig cfg) {
        fdmConfig = cfg;
    }
// Used by concrete tabs to read from the config
    protected FdmConfig getFdmConfig() {
        return fdmConfig;
    }

Then before running the program add this line and the one below in tabFrame.java but also could be dataStore test to check to make the object visible to all tabs
 - uta.cse3310.tab.baseTab.setFdmConfig(cfg);
 Make sure to add for the correct tab you are working on.

Here are some stackoverflow links that may explain more indepth
https://stackoverflow.com/questions/tagged/jaxb?
https://stackoverflow.com/questions/8356849/can-jaxb-initialize-values-in-base-classes?

Heres a small example of what to add if you wanted to work on the metrics tab for example-
// Access shared config from baseTab
        FdmConfig cfg = getFdmConfig();
if (cfg != null && cfg.getMetrics() != null) {
            // Example: show some metrics text
  String text = "Aircraft: " + cfg.getName() + "\nVersion: " + cfg.getFileheader().getVersion();}else{}

You could do similar within the other tabs.




 */

public class baseTab {
    // attributes common to all tabs
    public JPanel panel;
    public String label;

    public dataStore DS; // the data store that contains the information from the xml file
    public Integer version;

    public void loadData() {
        System.out.println("i am in basetab loadData");
    }

    public baseTab(dataStore ds, String LABEL) {

        System.out.println("in base Tab constructor");
        // this is common to all tabs
        DS = ds; // save off a pointer to the data store
        version = DS.version;

        label = LABEL;
        panel = new JPanel();
        panel.setLayout(new BorderLayout()); // Use BorderLayout for panel1

        panel.setBackground(new Color(200, 220, 255)); // Light blue background
    }
}

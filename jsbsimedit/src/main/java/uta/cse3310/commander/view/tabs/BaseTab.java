package uta.cse3310.commander.view.tabs;

public abstract class BaseTab {
    // Base class for all tabs
    protected String tabName;

    public abstract void initializeTab();
    public abstract void refreshData();
}


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

package uta.cse3310.tab.concreteTabs;

import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

import generated.FdmConfig;

import uta.cse3310.dataStore;
import uta.cse3310.tab.baseTab;
import uta.cse3310.tabFrame;

//subtabs imported
import uta.cse3310.commander.view.flightcontrol.*;


public class FlightControlTab extends baseTab {

    private tabFrame TF;
    private dataStore DS;

    private JTabbedPane subTabs;

    // Subtabs
    private PitchSubTab pitch = new PitchSubTab();
    private RollSubTab roll = new RollSubTab();
    private YawSubTab yaw = new YawSubTab();
    private LEFSubTab lef = new LEFSubTab();
    private ThrottleSubTab throttle = new ThrottleSubTab();
    private FlapsSubTab flaps = new FlapsSubTab();
    private SpeedbrakeSubTab speedbrake = new SpeedbrakeSubTab();
    private LandingGearSubTab landingGear = new LandingGearSubTab();
    private CanopySubTab canopy = new CanopySubTab();

    public FlightControlTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        this.TF = tf;
        this.DS = ds;

        panel.setLayout(new BorderLayout());

        subTabs = new JTabbedPane();

        subTabs.addTab("Pitch", pitch.buildPanel());
        subTabs.addTab("Roll", roll.buildPanel());
        subTabs.addTab("Yaw", yaw.buildPanel());
        subTabs.addTab("Leading Edge Flap", lef.buildPanel());
        subTabs.addTab("Throttle", throttle.buildPanel());
        subTabs.addTab("Flaps", flaps.buildPanel());
        subTabs.addTab("Speedbrake", speedbrake.buildPanel());
        subTabs.addTab("Landing Gear", landingGear.buildPanel());
        subTabs.addTab("Canopy", canopy.buildPanel());

        panel.add(subTabs, BorderLayout.CENTER);
    }

    @Override
    public void loadData() {
        FdmConfig cfg = DS.cfg;
        if (cfg == null)
            return;

        // Later: we can load XML Data for each subtab
    }
}




/* 
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

*/

package uta.cse3310.tab.concreteTabs;

import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

import generated.FdmConfig;

import uta.cse3310.dataStore;
import uta.cse3310.tab.baseTab;
import uta.cse3310.tabFrame;

//subtabs imported
import uta.cse3310.tab.concreteTabs.flightcontrol.*;


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
  

        panel.add(subTabs, BorderLayout.CENTER);
    }

    @Override
    public void loadData() {
	    // a new file has been loaded.
	    // it is in the dataStore at this point.
	    System.out.println("i am in the flight controls superTab!");
	    // probably need to call a function in all of the subTabs that
	    // says "hey dude, the data in dataStore has changed"
    }
}

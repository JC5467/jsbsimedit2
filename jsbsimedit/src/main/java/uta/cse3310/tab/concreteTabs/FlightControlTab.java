package uta.cse3310.tab.concreteTabs;

import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

import generated.FdmConfig;

import uta.cse3310.dataStore;
import uta.cse3310.tab.baseTab;
import uta.cse3310.tab.simpleTab;
import uta.cse3310.tabFrame;

//subtabs imported
import uta.cse3310.tab.concreteTabs.flightcontrol.*;


public class FlightControlTab extends simpleTab {

    private tabFrame TF;
    private dataStore DS;

    private JTabbedPane subTabs;

    // Subtabs
    private PitchSubTab pitch;
    private RollSubTab roll;
    private YawSubTab yaw;
    private LEFSubTab lef;
    private ThrottleSubTab throttle;
    private FlapsSubTab flaps;
    private SpeedbrakeSubTab speedbrake;
    private LandingGearSubTab landingGear;
    
    public FlightControlTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);

        this.TF = tf;
        this.DS = ds;

        panel.setLayout(new BorderLayout());

        subTabs = new JTabbedPane();


        pitch = new PitchSubTab(TF, DS, "Pitch");
        roll = new RollSubTab(TF, DS, "Roll");
        yaw = new YawSubTab(TF, DS, "Yaw");
        lef = new LEFSubTab(TF, DS, "Leading Edge Flap");
        throttle = new ThrottleSubTab(TF, DS, "Throttle");
        flaps = new FlapsSubTab(TF, DS, "Flaps");
        speedbrake = new SpeedbrakeSubTab(TF, DS, "Speedbrake");
        landingGear = new LandingGearSubTab(TF, DS, "Landing Gear");

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
	    System.out.println("FlightControlTab: loadData() triggered - XML is loaded");
	    // probably need to call a function in all of the subTabs that
	    // says "hey dude, the data in dataStore has changed"

        pitch.loadData();
        roll.loadData();
        yaw.loadData();
        lef.loadData();
        throttle.loadData();
        flaps.loadData();
        speedbrake.loadData();
        landingGear.loadData();

    }
}

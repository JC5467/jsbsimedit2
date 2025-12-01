package uta.cse3310.tab.concreteTabs;

import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

import uta.cse3310.dataStore;
import uta.cse3310.tab.simpleTab;
import uta.cse3310.tabFrame;

//subtabs imported
import uta.cse3310.tab.concreteTabs.flightcontrol.*;

public class FlightControlTab extends simpleTab {

    private tabFrame TF;
    private dataStore DS; // XML Data

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
    // private CanopySubTab canopy;

    public FlightControlTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);

        this.TF = tf;
        this.DS = ds;

        panel.setLayout(new BorderLayout());

        subTabs = new JTabbedPane();

        pitch = new PitchSubTab(tf, ds, "Pitch");
        roll = new RollSubTab(tf, ds, "Roll");
        yaw = new YawSubTab(tf, ds, "Yaw");
        lef = new LEFSubTab(tf, ds, "Leading Edge Flap");
        throttle = new ThrottleSubTab(tf, ds, "Throttle");
        flaps = new FlapsSubTab(tf, ds, "Flaps");
        speedbrake = new SpeedbrakeSubTab(tf, ds, "Speedbrake");
        landingGear = new LandingGearSubTab(tf, ds, "Landing Gear");
        // canopy = new CanopySubTab(tf, ds, "Canopy");

        subTabs = new JTabbedPane();

        subTabs.addTab("Pitch", pitch.buildPanel());
        subTabs.addTab("Roll", roll.buildPanel());
        subTabs.addTab("Yaw", yaw.buildPanel());
        subTabs.addTab("Leading Edge Flap", lef.buildPanel());
        subTabs.addTab("Throttle", throttle.buildPanel());
        subTabs.addTab("Flaps", flaps.buildPanel());
        subTabs.addTab("Speedbrake", speedbrake.buildPanel());
        subTabs.addTab("Landing Gear", landingGear.buildPanel());
        // subTabs.addTab("Canopy", canopy.buildPanel());

        panel.add(subTabs, BorderLayout.CENTER);
    }

    @Override
    public void loadData() {
        // a new file has been loaded.
        // it is in the dataStore at this point.
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
        // canopy.loadData();

    }
}

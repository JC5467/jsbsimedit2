package uta.cse3310.commander.view.tabs;

import javax.swing.*;

//import generated.FdmConfig;

import java.awt.*;

import uta.cse3310.tab.concreteTabs.flightcontrol.*;

public class FlightControlTab extends BaseTab{

    private JPanel rootPanel;
    private JTabbedPane subTabs;

    //subtabs
    private PitchSubTab pitch = new PitchSubTab();
    private RollSubTab roll = new RollSubTab();
    private YawSubTab yaw = new YawSubTab();
    private LEFSubTab lef = new LEFSubTab();
    private ThrottleSubTab throttle = new ThrottleSubTab();
    private FlapsSubTab flaps = new FlapsSubTab();
    private SpeedbrakeSubTab speedbrake = new SpeedbrakeSubTab();
    private LandingGearSubTab landingGear = new LandingGearSubTab();

    
    @Override
    public void initializeTab(){
        
        tabName = "Flight Control";

        rootPanel = new JPanel(new BorderLayout());
        subTabs = new JTabbedPane();

        subTabs.addTab("Pitch", pitch.buildPanel());
        subTabs.addTab("Roll", roll.buildPanel());
        subTabs.addTab("Yaw", yaw.buildPanel());
        subTabs.addTab("Leading Edge Flap", lef.buildPanel());
        subTabs.addTab("Throttle", throttle.buildPanel());
        subTabs.addTab("Flaps", flaps.buildPanel());
        subTabs.addTab("Speedbrake", speedbrake.buildPanel());
        subTabs.addTab("Landing Gear", landingGear.buildPanel());

        rootPanel.add(subTabs, BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        //later we can call the methods from the subtabs to load XML data
    }
    


    public JPanel getPanel() {
        return rootPanel;
    }

    
}

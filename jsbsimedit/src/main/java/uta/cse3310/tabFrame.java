package uta.cse3310;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import uta.cse3310.commander.controller.JSBSimCommanderApp;
import uta.cse3310.tab.baseTab;
import uta.cse3310.tab.concreteTabs.CanopySystemTab;
import uta.cse3310.tab.concreteTabs.ExternalReactionsTab;
import uta.cse3310.tab.concreteTabs.FileHeaderTab;
import uta.cse3310.tab.concreteTabs.FlightControlTab;
import uta.cse3310.tab.concreteTabs.GroundReactionsTab;
import uta.cse3310.tab.concreteTabs.HookSystemTab;
import uta.cse3310.tab.concreteTabs.MassBalanceTab;
import uta.cse3310.tab.concreteTabs.MetricsTab;
import uta.cse3310.tab.concreteTabs.PropulsionTab;

public class tabFrame {
    baseTab frameTabs[];

    public void dataLoaded() {
        // this function is called when new xml file is loaded
        System.out.println("in dataLoaded");

        // for each frame in frameTabs()
        // tell them to load the data
        for (baseTab t : frameTabs) {
            System.out.println("the label is " + t.label);
            t.loadData();
        }
    }

    public tabFrame() {

        System.out.println("in tabFrame constructor");

        // start out with no xml file
        dataStore DS = new dataStore(this);

        // make the 'main' panel
        new JSBSimCommanderApp(DS);

        // Create the main frame
        JFrame frame = new JFrame("JTabbedPane Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280,800);
        frame.setLocationRelativeTo(null); // Center the frame

        // Create a JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();

        // demonstrates using inheritiance to structure code for each
        // type and each special 'tab' in the program.

        // the array is probably not the ideal data structure for this list, but it is
        // simple.

        // create the array
        frameTabs = new uta.cse3310.tab.baseTab[9];

        frameTabs[0] = new CanopySystemTab(this, DS, "Canopy System Tab");
        tabbedPane.addTab("Canopy", null, frameTabs[0].panel, "This is the Canopy System tab.");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1); // Set mnemonic for Tab 1

        frameTabs[1] = new ExternalReactionsTab(this, DS, "External Reactions Tab");
        tabbedPane.addTab("External Reactions", null, frameTabs[1].panel, "This is the External Reactions tab.");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2); // Set mnemonic for Tab 2

        frameTabs[2] = new GroundReactionsTab(this, DS, "Ground Reactions Tab");
        tabbedPane.addTab("Ground Reactions", null, frameTabs[2].panel, "This is the Ground Reactions tab.");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3); // Set mnemonic for Tab 3

        frameTabs[3] = new HookSystemTab(this, DS, "Hook System Tab");
        tabbedPane.addTab("Hook", null, frameTabs[3].panel, "This is the Hook System tab.");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4); // Set mnemonic for Tab 4

        frameTabs[4] = new MassBalanceTab(this, DS, "Mass Balance Tab");
        tabbedPane.addTab("Mass Balance", null, frameTabs[4].panel, "This is the Mass Balance tab.");
        tabbedPane.setMnemonicAt(4, KeyEvent.VK_5); // Set mnemonic for Tab 5

        frameTabs[5] = new MetricsTab(this, DS, "Metrics");
        tabbedPane.addTab("Metrics", null, frameTabs[5].panel, "This is the Metrics tab.");
        tabbedPane.setMnemonicAt(5, KeyEvent.VK_6); // Set mnemonic for Tab 6

        frameTabs[6] = new PropulsionTab(this, DS, "Propulsion Tab");
        tabbedPane.addTab("Propulsion", null, frameTabs[6].panel, "This is the Propulsion tab.");
        tabbedPane.setMnemonicAt(6, KeyEvent.VK_7); // Set mnemonic for Tab 7
                                                    //
        frameTabs[7] = new FileHeaderTab(this, DS, "General Information Tab");
        tabbedPane.addTab("General Information", null, frameTabs[7].panel, "This is the general information tab.");
        tabbedPane.setMnemonicAt(7, KeyEvent.VK_8); // Set mnemonic for Tab 8

        frameTabs[8] = new FlightControlTab(this, DS, "Flight Control Tab");
        tabbedPane.addTab("Flight Control", null, frameTabs[8].panel, "This is the Flight Control tab.");
        tabbedPane.setMnemonicAt(8, KeyEvent.VK_9); // Set mnemonic for Tab 9

        // Add the JTabbedPane to the frame
        frame.add(tabbedPane, BorderLayout.CENTER);

        // Make the frame visible
        frame.setVisible(true);
    }
}

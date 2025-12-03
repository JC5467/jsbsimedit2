package uta.cse3310;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JOptionPane; // NEW IMPORT for error dialogs
import javax.swing.*;
import uta.cse3310.commander.controller.JSBSimCommanderApp;
import uta.cse3310.tab.baseTab;
import uta.cse3310.tab.concreteTabs.AerodynamicsTab;
import uta.cse3310.tab.concreteTabs.ExternalReactionsTab;
import uta.cse3310.tab.concreteTabs.GeneralInformationTab;
import uta.cse3310.tab.concreteTabs.FlightControlTab;
import uta.cse3310.tab.concreteTabs.GroundReactionsTab;
import uta.cse3310.tab.concreteTabs.MassBalanceTab;
import uta.cse3310.tab.concreteTabs.MetricsTab;
import uta.cse3310.tab.concreteTabs.PropulsionTab; //imported for aerodynamics tab
import uta.cse3310.tab.concreteTabs.BuoyantForcesTab;
import uta.cse3310.tab.concreteTabs.OutputTab;

public class tabFrame {
    Vector<baseTab> frameTabs; // Changed from array to Vector

    // We need a reference to the main JFrame to anchor the dialog box
    private JFrame mainFrame;
    private final dataStore DS;

    public void dataLoaded() {
        for (baseTab t : frameTabs) {
            t.loadData();
        }
    }

    /**
     * REQUIRED METHOD: Displays an error message to the user in a modal dialog.
     * This is called by dataStore.openFile() when a JAXB or file error occurs.
     * 
     * @param message The error message to display.
     */
    public void showError(String message) {
        if (mainFrame != null) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    message,
                    "File Load Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            // Fallback for when mainFrame isn't initialized yet
            System.err.println("CRITICAL UI ERROR: Could not show dialog: " + message);
        }
    }

    public tabFrame() {
        //Create  dataStore and pass this frame into it
        this.DS = new dataStore(this);

        dataStore DS = new dataStore(this);
        new JSBSimCommanderApp(DS);

        JFrame frame = new JFrame("Aircraft Manager");
        this.mainFrame = frame; // Store reference to the JFrame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 800);
        frame.setLocationRelativeTo(null);
        //icon
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("assets/JSBSimEdit128x128.png"));
		frame.setIconImage(icon.getImage());

        //New Menu bar with file
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open XML");

        openItem.addActionListoner(e-> {
            JFileChooser chooser = new JFileChooser();
            if(result == JFileChooser.APPROVE_OPTION){

                DS.openFile(chooser.getSelectedFile());
            }
        });
        fileMenu.add(openItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);
        //End of  new menu bar for loading XML

        JTabbedPane tabbedPane = new JTabbedPane();

        // Use Vector instead of array
        frameTabs = new Vector<>();

        frameTabs.add(new GeneralInformationTab(this, DS, "General Information Tab"));
        tabbedPane.addTab("General Information", null, frameTabs.lastElement().panel,
                "This is the general information tab.");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        frameTabs.add(new ExternalReactionsTab(this, DS, "External Reactions Tab"));
        tabbedPane.addTab("External Reactions", null, frameTabs.lastElement().panel,
                "This is the External Reactions tab.");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        frameTabs.add(new GroundReactionsTab(this, DS, "Ground Reactions Tab"));
        tabbedPane.addTab("Ground Reactions", null, frameTabs.lastElement().panel, "This is the Ground Reactions tab.");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        frameTabs.add(new MassBalanceTab(this, DS, "Mass Balance Tab"));
        tabbedPane.addTab("Mass Balance", null, frameTabs.lastElement().panel, "This is the Mass Balance tab.");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_5);

        frameTabs.add(new MetricsTab(this, DS, "Metrics"));
        tabbedPane.addTab("Metrics", null, frameTabs.lastElement().panel, "This is the Metrics tab.");
        tabbedPane.setMnemonicAt(4, KeyEvent.VK_6);

        frameTabs.add(new PropulsionTab(this, DS, "Propulsion Tab"));
        tabbedPane.addTab("Propulsion", null, frameTabs.lastElement().panel, "This is the Propulsion tab.");
        tabbedPane.setMnemonicAt(5, KeyEvent.VK_7);

        frameTabs.add(new FlightControlTab(this, DS, "Flight Control Tab"));
        tabbedPane.addTab("Flight Control", null, frameTabs.lastElement().panel, "This is the Flight Control tab.");
        tabbedPane.setMnemonicAt(6, KeyEvent.VK_8);

        frameTabs.add(new AerodynamicsTab(this, DS, "Aerodynamics Tab"));
        tabbedPane.addTab("Aerodynamics", null, frameTabs.lastElement().panel, "This is the Aerodynamics tab.");
        tabbedPane.setMnemonicAt(7, KeyEvent.VK_9);

        frameTabs.add(new BuoyantForcesTab(this, DS, "Buoyant Forces Tab"));
        tabbedPane.addTab("Buoyant Forces", null, frameTabs.lastElement().panel, "This is the Buoyant Forces tab.");
        tabbedPane.setMnemonicAt(8, KeyEvent.VK_B);

        frameTabs.add(new OutputTab(this, DS, "Output Tab"));
        tabbedPane.addTab("Output", null, frameTabs.lastElement().panel, "This is the Output tab.");
        tabbedPane.setMnemonicAt(9, KeyEvent.VK_O);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}

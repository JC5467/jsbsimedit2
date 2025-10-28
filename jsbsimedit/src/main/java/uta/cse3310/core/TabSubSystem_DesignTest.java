package uta.cse3310.core;
//DESIGN TESTING ONLY DO NOT JUDGE PLEASE P-P
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import generated.FdmConfig;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import uta.cse3310.tab.concreteTabs.CanopySystemTab;
import uta.cse3310.tab.concreteTabs.ExternalReactionsTab;
import uta.cse3310.tab.concreteTabs.GroundReactionsTab;
import uta.cse3310.tab.concreteTabs.HookSystemTab;
import uta.cse3310.tab.concreteTabs.MassBalanceTab;
import uta.cse3310.tab.concreteTabs.MetricsTab;
import uta.cse3310.tab.concreteTabs.PropulsionTab;

public class TabSubSystem_DesignTest extends JTabbedPane{
	// start jaxb / xml example code
	
	TabSubSystem_DesignTest(File f){
		
    try {

        // JAXBContext jaxbContext = JAXBContext.newInstance(FdmConfig.class);

        // Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        // FdmConfig cfg = (FdmConfig) jaxbUnmarshaller.unmarshal(file);

        // System.out.println(cfg.getName());

        JAXBContext jc = JAXBContext.newInstance("generated");

        Unmarshaller um = jc.createUnmarshaller();
        FdmConfig cfg = (FdmConfig) um.unmarshal(f);

        System.out.println(cfg);
        System.out.println(cfg.getFileheader().getCopyright());
        System.out.println(cfg.getFileheader().getVersion());
        System.out.println(cfg.getAerodynamics().getAxis().get(0).getName());
        System.out.println(cfg.getAerodynamics().getAxis().get(0).getDocumentationOrFunction());
        System.out.println(cfg.getAerodynamics().getAxis().get(0).getClass());

        // Marshaller m = jc.createMarshaller();
        // m.setProperty("jaxb.formatted.output", true);
        // m.marshal(cfg, System.out);

    } catch (JAXBException e) {
        e.printStackTrace();
    }
    //
    //
    //
    // end xml / jaxb example code
    //
    

    // Create a JTabbedPane
 

    // demonstrates using inheritiance to structure code for each
    // type and each special 'tab' in the program.

    // the array is probably not the ideal data structure for this list, but it is
    // simple.

    try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			e.printStackTrace();
		}

    uta.cse3310.tab.baseTab frameTabs[] = new uta.cse3310.tab.baseTab[7];

    frameTabs[0] = new CanopySystemTab("Canopy System Tab");
    this.addTab("Tab 1", null, frameTabs[0].panel, "This is the Canopy System tab.");
    this.setMnemonicAt(0, KeyEvent.VK_1); // Set mnemonic for Tab 1

    frameTabs[1] = new ExternalReactionsTab("External Reactions Tab");
    this.addTab("Tab 2", null, frameTabs[1].panel, "This is the External Reactions tab.");
    this.setMnemonicAt(1, KeyEvent.VK_2); // Set mnemonic for Tab 2

    frameTabs[2] = new GroundReactionsTab("Ground Reactions Tab");
    this.addTab("Tab 3", null, frameTabs[2].panel, "This is the Ground Reactions tab.");
    this.setMnemonicAt(2, KeyEvent.VK_3); // Set mnemonic for Tab 3

    frameTabs[3] = new HookSystemTab("Hook System Tab");
    this.addTab("Tab 4", null, frameTabs[3].panel, "This is the Hook System tab.");
    this.setMnemonicAt(3, KeyEvent.VK_4); // Set mnemonic for Tab 4

    frameTabs[4] = new MassBalanceTab("Mass Balance Tab");
    this.addTab("Tab 5", null, frameTabs[4].panel, "This is the Mass Balance tab.");
    this.setMnemonicAt(4, KeyEvent.VK_5); // Set mnemonic for Tab 5

    frameTabs[5] = new MetricsTab("Metrics Tab");
    this.addTab("Tab 6", null, frameTabs[5].panel, "This is the Metrics tab.");
    this.setMnemonicAt(5, KeyEvent.VK_6); // Set mnemonic for Tab 6

    frameTabs[6] = new PropulsionTab("Propulsion Tab");
    this.addTab("Tab 7", null, frameTabs[6].panel, "This is the Propulsion tab.");
    this.setMnemonicAt(6, KeyEvent.VK_7); // Set mnemonic for Tab 7

    //For the panels below dont forget to implement all 7 panels above.

    // Create Panel 1 and add components
    // JPanel panel1 = new JPanel();
    // panel1.setLayout(new BorderLayout()); // Use BorderLayout for panel1
    // panel1.add(new JLabel("Content of Tab 1", SwingConstants.CENTER),
    // BorderLayout.CENTER);
    // panel1.setBackground(new Color(200, 220, 255)); // Light blue background

    // Create Panel 2 and add components
    // JPanel panel2 = new JPanel();
    // panel2.setLayout(new FlowLayout()); // Use FlowLayout for panel2
    // panel2.add(new JLabel("Enter your name:"));
    // panel2.add(new JTextField(15));
    // panel2.setBackground(new Color(255, 220, 200)); // Light orange background

    // Create Panel 3 and add components
    // JPanel panel3 = new JPanel();
    // panel3.setLayout(new GridLayout(2, 1)); // Use GridLayout for panel3
    // panel3.add(new JButton("Click Me!"));
    // panel3.add(new JCheckBox("Enable Feature"));
    // panel3.setBackground(new Color(220, 255, 200)); // Light green background

    // Add panels to the JTabbedPane
    // tabbedPane.addTab("Tab 1", null, panel1, "This is the first tab.");
    // tabbedPane.setMnemonicAt(0, KeyEvent.VK_1); // Set mnemonic for Tab 1

    // tabbedPane.addTab("Tab 2", null, panel2, "This is the second tab.");
    // tabbedPane.setMnemonicAt(1, KeyEvent.VK_2); // Set mnemonic for Tab 2

    // tabbedPane.addTab("Tab 3", null, panel3, "This is the third tab.");
    // tabbedPane.setMnemonicAt(2, KeyEvent.VK_3); // Set mnemonic for Tab 3

    // Add the JTabbedPane to the frame
   

    // Make the frame visible

	}

}

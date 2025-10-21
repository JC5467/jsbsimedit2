package uta.cse3310;

/**
 * Hello world!
 *
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class App {

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("JTabbedPane Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); // Center the frame

        // Create a JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create Panel 1 and add components
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout()); // Use BorderLayout for panel1
        panel1.add(new JLabel("Content of Tab 1", SwingConstants.CENTER), BorderLayout.CENTER);
        panel1.setBackground(new Color(200, 220, 255)); // Light blue background

        // Create Panel 2 and add components
        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout()); // Use FlowLayout for panel2
        panel2.add(new JLabel("Enter your name:"));
        panel2.add(new JTextField(15));
        panel2.setBackground(new Color(255, 220, 200)); // Light orange background

        // Create Panel 3 and add components
        JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayout(2, 1)); // Use GridLayout for panel3
        panel3.add(new JButton("Click Me!"));
        panel3.add(new JCheckBox("Enable Feature"));
        panel3.setBackground(new Color(220, 255, 200)); // Light green background

        // Add panels to the JTabbedPane
        tabbedPane.addTab("Tab 1", null, panel1, "This is the first tab.");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1); // Set mnemonic for Tab 1

        tabbedPane.addTab("Tab 2", null, panel2, "This is the second tab.");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2); // Set mnemonic for Tab 2

        tabbedPane.addTab("Tab 3", null, panel3, "This is the third tab.");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3); // Set mnemonic for Tab 3

        // Add the JTabbedPane to the frame
        frame.add(tabbedPane, BorderLayout.CENTER);

        // Make the frame visible
        frame.setVisible(true);

    }
}
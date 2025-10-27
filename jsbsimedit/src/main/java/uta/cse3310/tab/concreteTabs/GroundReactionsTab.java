package uta.cse3310.tab.concreteTabs;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.*;

import uta.cse3310.tab.simpleTab;

public class GroundReactionsTab extends simpleTab{
    // attributes that only pertain to the 'concrete' tab called oneTab

    public GroundReactionsTab(String label){
        super(label);
        System.out.println("in GroundReactions constructor");
         
        // what is unique about this tab
        panel.setLayout(new FlowLayout()); // Use FlowLayout for panel2
        panel.add(new JLabel("Enter your name:"));
        panel.add(new JTextField(15));
        panel.setBackground(new Color(255, 220, 200)); // Light orange background

    }

}

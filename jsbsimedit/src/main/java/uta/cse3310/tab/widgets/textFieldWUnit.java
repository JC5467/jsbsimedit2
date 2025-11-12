package uta.cse3310.tab.widgets;

import uta.cse3310.tab.widgets.dirtyFunction;
import uta.cse3310.tab.widgets.setValFunction;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class textFieldWUnit {
	private JLabel label;
	private JLabel jl;

	public textFieldWUnit(JPanel panel, String labelText, Integer labelX, Integer labelY,
    			Integer labelW, Integer labelH, String unit, Integer unitX, Integer unitY, Integer unitW,
                	Integer unitH ){

		System.out.println("in textFieldWUnit widget constructor");

		label = new JLabel(labelText);
		label.setBounds(labelX, labelY, labelW, labelH);
		panel.add(label);

		jl = new JLabel(unit);
                jl.setBounds(unitX,unitY,unitW,unitH);
                panel.add(jl);
		
    }
}

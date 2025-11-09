package uta.cse3310.tab.widgets;

import uta.cse3310.dataStore;  //  THIS IS A POOR DESIGN.  SHOULD NOT BE NEEDING TO HAVE VISIBILITY TO THIS

import java.util.function.Consumer; // going to need this when the lambda is added as an option to the constructor

import javax.swing.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class textFieldWLabel
{
	private JLabel label;
	private JTextField tf;

	// I don't like passing Datastore in here, but the below lambda was complaining about 
	// passing a non object. TODO: fixme
	public textFieldWLabel(dataStore DS ,JPanel panel,String labelText,Integer labelX,Integer labelY,Integer labelW,Integer labelH,String text,Integer textX, Integer textY, Integer textH, Integer textW) { 
           System.out.println("in textFieldWLabel widget constructor");

	        label = new JLabel(labelText);
                label.setBounds(labelX,labelY,labelW,labelH);
                panel.add(label);

                tf = new JTextField();
                tf.setBounds(textX,textY,textW,textH);
                tf.setText(text);
                panel.add(tf);

                // set up listener
                tf.getDocument().addDocumentListener(new DocumentListener() {
                        public void changedUpdate(DocumentEvent e) {
				DS.dirty=true;
                                System.out.println("change");
                        }

                        public void removeUpdate(DocumentEvent e) {
                                System.out.println("remove");
                        }

                        public void insertUpdate(DocumentEvent e) {
                                System.out.println("insert");
                        }
                });
	}
}

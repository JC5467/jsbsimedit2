package uta.cse3310.tab.widgets;

import uta.cse3310.tab.widgets.dirtyFunction;
import uta.cse3310.tab.widgets.setValFunction;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import generated.AreaUnit;
import generated.LengthUnit;
import generated.Metrics;

import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;



public class textFieldWLabel {
        private JLabel label;
        private JTextField tf;
        private JLabel jl;
	// if you want a label + unit, use this one...
        public textFieldWLabel(setValFunction SF, dirtyFunction DF, JPanel panel, String labelText, Integer labelX, Integer labelY,
                        Integer labelW, Integer labelH, String text, Integer textX, Integer textY, Integer textW,
                        Integer textH, String unit, Integer boxX, Integer boxY, Integer boxW,
                        Integer boxH,setUnitFunction SU){

                this(SF,DF,panel,labelText,labelX,labelY,labelW,labelH,text,textX,textY,textW,textH);
                String search = "area";
                
                label = new JLabel(labelText);
                label.setBounds(labelX, labelY, labelW, labelH);
                panel.add(label);
                
                if(labelText.toLowerCase().indexOf(search.toLowerCase()) != -1){
                        JComboBox unitBoxArea = new JComboBox<>(AreaUnit.values());
                        AreaUnit AU = AreaUnit.valueOf(unit);
                        unitBoxArea.setSelectedItem(AU);
                        unitBoxArea.setBounds(boxX,boxY,boxW,boxH);
                        panel.add(unitBoxArea);
                        unitBoxArea.addActionListener(e -> {
                        DF.set();
                        SU.setUnit((AreaUnit) unitBoxArea.getSelectedItem());
                        });
                        }
                        else{
                        JComboBox unitBoxLen = new JComboBox<>(LengthUnit.values());
                        LengthUnit LU = LengthUnit.valueOf(unit);
                        unitBoxLen.setSelectedItem(LU);
                        unitBoxLen.setBounds(boxX,boxY,boxW,boxH);
                        panel.add(unitBoxLen);
                        unitBoxLen.addActionListener(e -> {
                        DF.set();
                        SU.setUnit((LengthUnit) unitBoxLen.getSelectedItem());
                        });
                }
	}

	// the original
        public textFieldWLabel(setValFunction SF, dirtyFunction DF, JPanel panel, String labelText, Integer labelX, Integer labelY,
                        Integer labelW, Integer labelH, String text, Integer textX, Integer textY, Integer textW,
                        Integer textH ) {
                label = new JLabel(labelText);
                label.setBounds(labelX, labelY, labelW, labelH);
                panel.add(label);

                tf = new JTextField();
                tf.setBounds(textX, textY, textW, textH);
                tf.setText(text);
                panel.add(tf);

                // set up listener
                tf.getDocument().addDocumentListener(new DocumentListener() {
                        public void changedUpdate(DocumentEvent e) {
                                DF.set();
                                System.out.println("inside the lambda " + tf.getText());
				SF.setVal(Double.parseDouble(tf.getText()));
                                System.out.println("change");
                        }

                        public void removeUpdate(DocumentEvent e) {
                                DF.set();
                                String text = tf.getText();
                                if(text.isEmpty()){
                                System.out.println("Text Box Empty. Please enter numeric value.");
                                } else {
                                SF.setVal(Double.parseDouble(text));
                                System.out.println("New Value: " + tf.getText());
                                System.out.println("remove");      

                                }
                        }

                        public void insertUpdate(DocumentEvent e) {
                                DF.set();
                                String text = tf.getText();
                                if(text.matches("-?\\d*\\.?\\d*")){
                                SF.setVal(Double.parseDouble(text));
                                System.out.println("New Value: " + tf.getText());
                                System.out.println("insert");
                                } else {
                                  System.out.println("Invalid Figure in Text Box");
                                }
                        }
                });

                 tf.addFocusListener(new FocusAdapter() {

                        public void focusLost(FocusEvent e) {
                                if(tf.getText().isEmpty()) {
                                   tf.setText("0.0");
                                }
                        }
                });

        }
}

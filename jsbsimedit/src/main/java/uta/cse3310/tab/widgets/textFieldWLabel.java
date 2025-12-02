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

        private double safeParse(String text) {
                if (text == null || text.trim().isEmpty()) {
                        return 0.0;
                }
                try {
                        return Double.parseDouble(text);
                } catch (NumberFormatException e) {
                        return 0.0;
                }
        }

        // if you want a label + unit, use this one...
        public textFieldWLabel(setValFunction SF, dirtyFunction DF, JPanel panel, String labelText, Integer labelX,
                        Integer labelY,
                        Integer labelW, Integer labelH, String text, Integer textX, Integer textY, Integer textW,
                        Integer textH, String unit, Integer boxX, Integer boxY, Integer boxW,
                        Integer boxH, setUnitFunction SU) {

                this(SF, DF, panel, labelText, labelX, labelY, labelW, labelH, text, textX, textY, textW, textH);
                String search = "area";

                label = new JLabel(labelText);
                label.setBounds(labelX, labelY, labelW, labelH);
                panel.add(label);

                if (labelText.toLowerCase().indexOf(search.toLowerCase()) != -1) {
                        JComboBox unitBoxArea = new JComboBox<>(AreaUnit.values());
                        AreaUnit AU = AreaUnit.valueOf(unit);
                        unitBoxArea.setSelectedItem(AU);
                        unitBoxArea.setBounds(boxX, boxY, boxW, boxH);
                        panel.add(unitBoxArea);
                        unitBoxArea.addActionListener(e -> {
                                DF.set();
                                SU.setUnit((AreaUnit) unitBoxArea.getSelectedItem());
                        });
                } else {
                        JComboBox unitBoxLen = new JComboBox<>(LengthUnit.values());
                        LengthUnit LU = LengthUnit.valueOf(unit);
                        unitBoxLen.setSelectedItem(LU);
                        unitBoxLen.setBounds(boxX, boxY, boxW, boxH);
                        panel.add(unitBoxLen);
                        unitBoxLen.addActionListener(e -> {
                                DF.set();
                                SU.setUnit((LengthUnit) unitBoxLen.getSelectedItem());
                        });
                }
        }

        // the original
        public textFieldWLabel(setValFunction SF, dirtyFunction DF, JPanel panel, String labelText, Integer labelX,
                        Integer labelY,
                        Integer labelW, Integer labelH, String text, Integer textX, Integer textY, Integer textW,
                        Integer textH) {
                label = new JLabel(labelText);
                label.setBounds(labelX, labelY, labelW, labelH);
                panel.add(label);

                tf = new JTextField();
                tf.setBounds(textX, textY, textW, textH);
                tf.setText(text);
                panel.add(tf);

                // set up listener (use safe parsing to avoid NumberFormatException and null model values)
                tf.getDocument().addDocumentListener(new DocumentListener() {
                        public void changedUpdate(DocumentEvent e) {
                                DF.set();
                                String text = tf.getText();
                                double val = safeParse(text);
                                SF.setVal(val);
                                System.out.println("change -> value=" + val);
                        }

                        public void removeUpdate(DocumentEvent e) {
                                DF.set();
                                String text = tf.getText();
                                if (text == null || text.isEmpty()) {
                                        // ensure model has a valid numeric value instead of null
                                        SF.setVal(0.0);
                                        System.out.println("Text Box Empty. Set to 0.0");
                                } else {
                                        double val = safeParse(text);
                                        SF.setVal(val);
                                        System.out.println("New Value: " + val + " (remove)");
                                }
                        }

                        public void insertUpdate(DocumentEvent e) {
                                DF.set();
                                String text = tf.getText();
                                if (text.matches("-?\\d*\\.?\\d*")) {
                                        double val = safeParse(text);
                                        SF.setVal(val);
                                        System.out.println("New Value: " + val + " (insert)");
                                } else {
                                        // invalid input -> set to 0.0 to avoid leaving model null
                                        SF.setVal(0.0);
                                        System.out.println("Invalid Figure in Text Box. Set to 0.0");
                                }
                        }
                });

                tf.addFocusListener(new FocusAdapter() {

                        public void focusLost(FocusEvent e) {
                                if (tf.getText().isEmpty()) {
                                        tf.setText("0.0");
                                }
                        }
                });

        }
}

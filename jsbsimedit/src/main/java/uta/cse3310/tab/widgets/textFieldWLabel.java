package uta.cse3310.tab.widgets;

import uta.cse3310.tab.widgets.dirtyFunction;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class textFieldWLabel {
        private JLabel label;
        private JTextField tf;

        public textFieldWLabel(dirtyFunction DF, JPanel panel, String labelText, Integer labelX, Integer labelY,
                        Integer labelW, Integer labelH, String text, Integer textX, Integer textY, Integer textH,
                        Integer textW) {
                System.out.println("in textFieldWLabel widget constructor");

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
                                System.out.println("change");
                        }

                        public void removeUpdate(DocumentEvent e) {
                                DF.set();
                                System.out.println("inside the lambda " + tf.getText());
                                System.out.println("remove");
                        }

                        public void insertUpdate(DocumentEvent e) {
                                DF.set();
                                System.out.println("inside the lambda " + tf.getText());
                                System.out.println("insert");
                        }
                });
        }
}

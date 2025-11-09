package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;
import uta.cse3310.tab.widgets.textFieldWLabel;

public class MetricsTab extends simpleTab {
        // attributes that only pertain to the 'concrete' tab called oneTab

        public MetricsTab(tabFrame tf, dataStore ds, String label) {
                super(ds, label);
                System.out.println("in Metrics constructor");

                // rough outline just to test xml read and check if values appear

                // using setbounds for now will fix formting better as it
                // currently needs the tab to be resized to see everything.
                // needs drop down values for units on each field still will add later.

                // // aerodynamic reference point & x,y,z values w units
                // JLabel aeroRefPntLbl = new JLabel("AeroDynamic Reference Point(*)");
                // aeroRefPntLbl.setBounds(10, 200, 300, 20);
                // panel.add(aeroRefPntLbl);

                // // x y and z fields
                // JLabel aeroXlbl = new JLabel("X = ");
                // aeroXlbl.setBounds(50, 240, 200, 20);
                // panel.add(aeroXlbl);

                // JTextField aeroX = new JTextField();
                // aeroX.setBounds(100, 240, 150, 20);
                // panel.add(aeroX);

                // JLabel aeroYlbl = new JLabel("Y = ");
                // aeroYlbl.setBounds(270, 240, 200, 20);
                // panel.add(aeroYlbl);

                // JTextField aeroY = new JTextField();
                // aeroY.setBounds(320, 240, 150, 20);
                // panel.add(aeroY);

                // JLabel aeroZlbl = new JLabel("Z = ");
                // aeroZlbl.setBounds(490, 240, 200, 20);
                // panel.add(aeroZlbl);

                // JTextField aeroZ = new JTextField();
                // aeroZ.setBounds(560, 240, 150, 20);
                // panel.add(aeroZ);

                // // Eye Point
                // JLabel eyePoint = new JLabel("Eye Point");
                // eyePoint.setBounds(10, 280, 300, 20);
                // panel.add(eyePoint);

                // // x y and z values for eye pint
                // JLabel eyeXlbl = new JLabel("X = ");
                // eyeXlbl.setBounds(50, 320, 200, 20);
                // panel.add(eyeXlbl);

                // JTextField eyeX = new JTextField();
                // eyeX.setBounds(100, 320, 150, 20);
                // panel.add(eyeX);

                // JLabel eyeYlbl = new JLabel("Y = ");
                // eyeYlbl.setBounds(270, 320, 200, 20);
                // panel.add(eyeYlbl);

                // JTextField eyeY = new JTextField();
                // eyeY.setBounds(320, 320, 150, 20);
                // panel.add(eyeY);

                // JLabel eyeZlbl = new JLabel("Z = ");
                // eyeZlbl.setBounds(490, 320, 200, 20);
                // panel.add(eyeZlbl);

                // JTextField eyeZ = new JTextField();
                // eyeZ.setBounds(560, 320, 150, 20);
                // panel.add(eyeZ);

                // // Visual Reference Point
                // JLabel visRefPointLbl = new JLabel("Visual Reference Point(*)");
                // visRefPointLbl.setBounds(10, 360, 300, 20);
                // panel.add(visRefPointLbl);

                // // x y and z values for visual ref point
                // JLabel refXlbl = new JLabel("X = ");
                // refXlbl.setBounds(50, 400, 200, 20);
                // panel.add(refXlbl);

                // JTextField refX = new JTextField();
                // refX.setBounds(100, 400, 150, 20);
                // panel.add(refX);

                // JLabel refYlbl = new JLabel("Y = ");
                // refYlbl.setBounds(270, 400, 200, 20);
                // panel.add(refYlbl);

                // JTextField refY = new JTextField();
                // refY.setBounds(320, 400, 150, 20);
                // panel.add(refY);

                // JLabel refZlbl = new JLabel("Z = ");
                // refZlbl.setBounds(490, 400, 200, 20);
                // panel.add(refZlbl);

                // JTextField refZ = new JTextField();
                // refZ.setBounds(560, 400, 150, 20);
                // panel.add(refZ);

                // this is what is unique about 1 tab
                TF = tf;
                panel.add(new JLabel("Content of Tab 1", SwingConstants.CENTER), BorderLayout.CENTER);

        }

        public void loadData() {
                System.out.println("this is in loadData() for Metrics");
                System.out.println("data structure is " + DS.valid + " and the version is " + DS.version);
                // TODO:
                // Need to delete all the widgets on the page at this point.
                panel.removeAll();

                // Next, extract the info for each data item from the datastore, and build
                // the widgets at this time
                // (below print statements just for debugging and demonstration purposes)
                System.out.println(DS.cfg.getMetrics().getWingarea().getValue());
                System.out.println(DS.cfg.getMetrics().getWingarea().getUnit());

                // text and fields for wingarea
                // in the event the data is changed by the user, it has to be written back to
                // the DS (there are set() methods) and the
                // DS.dirty flag has to be set.
                //
                // Wingarea
                //JLabel wingarea = new JLabel("wingarea(*) = ");
                //wingarea.setBounds(10, 40, 200, 20);
                //panel.add(wingarea);

                JTextField wArea = new JTextField();
                wArea.setBounds(100, 40, 100, 20);
                wArea.setText(String.valueOf(DS.cfg.getMetrics().getWingarea().getValue()));
                panel.add(wArea);

                //unit
                JLabel wAreaUnit = new JLabel(String.valueOf(DS.cfg.getMetrics().getWingarea().getUnit()));
                wAreaUnit.setBounds(220,40,80,20);
                panel.add(wAreaUnit);

                // set up listener
                wArea.getDocument().addDocumentListener(new DocumentListener() {
                        public void changedUpdate(DocumentEvent e) {
                                System.out.println("change");
                        }

                        public void removeUpdate(DocumentEvent e) {
                                System.out.println("remove");
                        }

                        public void insertUpdate(DocumentEvent e) {
                                System.out.println("insert");
                        }
                });
                // End of Wingarea
                // text and fields for wingspan
                JLabel wingspan = new JLabel("wingspan(*) = ");
                wingspan.setBounds(10, 80, 200, 20);
                panel.add(wingspan);

                JTextField wSpan = new JTextField();
                wSpan.setBounds(100, 80, 100, 20);
                wSpan.setText(String.valueOf(DS.cfg.getMetrics().getWingspan().getValue()));
                panel.add(wSpan);

                //unit
                JLabel wSpanUnit = new JLabel(String.valueOf(DS.cfg.getMetrics().getWingspan().getUnit()));
                wSpanUnit.setBounds(220,80,80,20);
                panel.add(wSpanUnit);

                // set up listener
                wSpan.getDocument().addDocumentListener(new DocumentListener() {
                        public void changedUpdate(DocumentEvent e) {
                                System.out.println("change");
                        }

                        public void removeUpdate(DocumentEvent e) {
                                System.out.println("remove");
                        }

                        public void insertUpdate(DocumentEvent e) {
                                System.out.println("insert");
                        }
                });
                // text and fields for chord
                JLabel chordLbl = new JLabel("chord(*) = ");
                chordLbl.setBounds(10, 120, 200, 20);
                panel.add(chordLbl);

                JTextField chord = new JTextField();
                chord.setBounds(100, 120, 100, 20);
                chord.setText(String.valueOf(DS.cfg.getMetrics().getChord().getValue()));
                panel.add(chord);

                //unit
                JLabel chordUnit = new JLabel(String.valueOf(DS.cfg.getMetrics().getChord().getUnit()));
                chordUnit.setBounds(220,120,80,20);
                panel.add(chordUnit);

                // set up listener
                chord.getDocument().addDocumentListener(new DocumentListener() {
                        public void changedUpdate(DocumentEvent e) {
                                System.out.println("change");
                        }

                        public void removeUpdate(DocumentEvent e) {
                                System.out.println("remove");
                        }

                        public void insertUpdate(DocumentEvent e) {
                                System.out.println("insert");
                        }
                });

                // text and fields for htailarea
                JLabel htailArea = new JLabel("htailarea = ");
                htailArea.setBounds(340, 40, 200, 20);
                panel.add(htailArea);

                JTextField htailA = new JTextField();
                htailA.setBounds(430, 40, 100, 20);
                htailA.setText(String.valueOf(DS.cfg.getMetrics().getHtailarea().getValue()));
                panel.add(htailA);

                // text and fields for htailarm
                JLabel htailAUnit = new JLabel(String.valueOf(DS.cfg.getMetrics().getHtailarea().getUnit()));
                htailAUnit.setBounds(550,40,80,20);
                panel.add(htailAUnit);

                // set up listener
                htailA.getDocument().addDocumentListener(new DocumentListener() {
                        public void changedUpdate(DocumentEvent e) {
                                System.out.println("change");
                        }

                        public void removeUpdate(DocumentEvent e) {
                                System.out.println("remove");
                        }

                        public void insertUpdate(DocumentEvent e) {
                                System.out.println("insert");
                        }
                });
                JLabel htailArmLbl = new JLabel("htailarm = ");
                htailArmLbl.setBounds(340, 80, 200, 20);
                panel.add(htailArmLbl);

                JTextField htailArm = new JTextField();
                htailArm.setBounds(430, 80, 100, 20);
                htailArm.setText(String.valueOf(DS.cfg.getMetrics().getHtailarm().getValue()));
                panel.add(htailArm);

                JLabel htailArmUnit = new JLabel(String.valueOf(DS.cfg.getMetrics().getHtailarm().getUnit()));
                htailArmUnit.setBounds(550,80,80,20);
                panel.add(htailArmUnit);

                //listener
                htailArm.getDocument().addDocumentListener(new DocumentListener() {
                        public void changedUpdate(DocumentEvent e) {
                                System.out.println("change");
                        }

                        public void removeUpdate(DocumentEvent e) {
                                System.out.println("remove");
                        }

                        public void insertUpdate(DocumentEvent e) {
                                System.out.println("insert");
                        }
                });

                // text and fields for vtailarea
                JLabel vtailArea = new JLabel("vtailarea = ");
                vtailArea.setBounds(340, 120, 200, 20);
                panel.add(vtailArea);

                JTextField vtailA = new JTextField();
                vtailA.setBounds(430, 120, 100, 20);
                vtailA.setText(String.valueOf(DS.cfg.getMetrics().getVtailarea().getValue()));
                panel.add(vtailA);

                JLabel vtailAUnit = new JLabel(String.valueOf(DS.cfg.getMetrics().getVtailarea().getUnit()));
                vtailAUnit.setBounds(550,120,80,20);
                panel.add(vtailAUnit);


                // set up listener
                vtailA.getDocument().addDocumentListener(new DocumentListener() {
                        public void changedUpdate(DocumentEvent e) {
                                System.out.println("change");
                        }

                        public void removeUpdate(DocumentEvent e) {
                                System.out.println("remove");
                        }

                        public void insertUpdate(DocumentEvent e) {
                                System.out.println("insert");
                        }
                });

                // text and fields for vtailarm
                JLabel vtailArmLbl = new JLabel("vtailarm = ");
                vtailArmLbl.setBounds(340, 160, 200, 20);
                panel.add(vtailArmLbl);

                JTextField vtailArm = new JTextField();
                vtailArm.setBounds(430, 160, 100, 20);
                vtailArm.setText(String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue()));
                panel.add(vtailArm);

                JLabel vtailArmUnit = new JLabel(String.valueOf(DS.cfg.getMetrics().getVtailarm().getUnit()));
                vtailArmUnit.setBounds(550,160,80,20);
                panel.add(vtailArmUnit);

                // set up listener
                vtailArm.getDocument().addDocumentListener(new DocumentListener() {
                        public void changedUpdate(DocumentEvent e) {
                                System.out.println("change");
                        }

                        public void removeUpdate(DocumentEvent e) {
                                System.out.println("remove");
                        }

                        public void insertUpdate(DocumentEvent e) {
                                System.out.println("insert");
                        }
                });

                
                panel.add(new JLabel("-", SwingConstants.CENTER), BorderLayout.CENTER);

                // other way to do it.....
		textFieldWLabel L1 = new textFieldWLabel(DS::setDirty,panel,"wingarea(*) = ",10,40,200,20,String.valueOf(DS.cfg.getMetrics().getWingarea().getValue()),100,40,150,20);
                //will implement this way as current version takes to many lines.
        }
}

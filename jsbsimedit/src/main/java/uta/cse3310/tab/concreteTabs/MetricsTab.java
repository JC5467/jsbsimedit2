package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;
import uta.cse3310.tab.widgets.textFieldWLabel;

import generated.Metrics;


public class MetricsTab extends simpleTab {
        // attributes that only pertain to the 'concrete' tab called oneTab

        public MetricsTab(tabFrame tf, dataStore ds, String label) {
                super(ds, label);
                System.out.println("in Metrics constructor");
                TF = tf;
                panel.add(new JLabel("No aircraft file read.", SwingConstants.CENTER), BorderLayout.CENTER);

        }

        public void loadData() {
                System.out.println("this is in loadData() for Metrics");
                System.out.println("data structure is " + DS.valid + " and the version is " + DS.version);

		// Need to delete all the widgets on the page at this point.
                panel.removeAll();

                // Next, extract the info for each data item from the datastore, and build
                // the widgets

		String V,U,L;
                // Wingarea
		V = String.valueOf(DS.cfg.getMetrics().getWingarea().getValue());
                U = String.valueOf(DS.cfg.getMetrics().getWingarea().getUnit());
                L = "wingarea(*) = ";		 // just to make the next line simpler
                textFieldWLabel L1 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getWingarea().setValue(Double),DS::setDirty,panel,L,10,40,200,20,V,100,40,100,20,U,220,40,80,20);

                // wingspan
                V = String.valueOf(DS.cfg.getMetrics().getWingspan().getValue());
                U = String.valueOf(DS.cfg.getMetrics().getWingspan().getUnit());
		L = "wingspan(*) = ";
                textFieldWLabel L2 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getWingspan().setValue(Double),DS::setDirty,panel,L,10,80,200,20,V,100,80,100,20,U,220,80,80,20);

                //unit
                JLabel wSpanUnit = new JLabel(String.valueOf(DS.cfg.getMetrics().getWingspan().getUnit()));
                wSpanUnit.setBounds(220,80,80,20);
                panel.add(wSpanUnit);

                // text and fields for chord

                textFieldWLabel L3 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getChord().setValue(Double),DS::setDirty,panel,"chord(*) = ",10,120,200,20,String.valueOf(DS.cfg.getMetrics().getChord().getValue()),100,120,100,20);

                //unit
                JLabel chordUnit = new JLabel(String.valueOf(DS.cfg.getMetrics().getChord().getUnit()));
                chordUnit.setBounds(220,120,80,20);
                panel.add(chordUnit);

                // text and fields for htailarea

                textFieldWLabel L4 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getHtailarea().setValue(Double),DS::setDirty,panel,"htailarea = ",340,40,200,20,String.valueOf(DS.cfg.getMetrics().getHtailarea().getValue()),430,40,100,20);

                JLabel htailAUnit = new JLabel(String.valueOf(DS.cfg.getMetrics().getHtailarea().getUnit()));
                htailAUnit.setBounds(550,40,80,20);
                panel.add(htailAUnit);

                //tail arm text and field
                textFieldWLabel L5 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getHtailarm().setValue(Double),DS::setDirty,panel,"htailarm = ",340,80,200,20,String.valueOf(DS.cfg.getMetrics().getHtailarm().getValue()),430,80,100,20);
                
                JLabel htailArmUnit = new JLabel(String.valueOf(DS.cfg.getMetrics().getHtailarm().getUnit()));
                htailArmUnit.setBounds(550,80,80,20);
                panel.add(htailArmUnit);

                // text and fields for vtailarea
                textFieldWLabel L6 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarea().setValue(Double),DS::setDirty,panel,"vtailarea = ",340,120,200,20,String.valueOf(DS.cfg.getMetrics().getVtailarea().getValue()),430,120,100,20);
                //unit for vtailarea
                JLabel vtailAUnit = new JLabel(String.valueOf(DS.cfg.getMetrics().getVtailarea().getUnit()));
                vtailAUnit.setBounds(550,120,80,20);
                panel.add(vtailAUnit);

                // text and fields for vtailarm
                textFieldWLabel L7 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"vtailarm = ",340,160,200,20,String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue()),430,160,100,20);
                //vtailarm unit
                JLabel vtailArmUnit = new JLabel(String.valueOf(DS.cfg.getMetrics().getVtailarm().getUnit()));
                vtailArmUnit.setBounds(550,160,80,20);
                panel.add(vtailArmUnit);
                
                // aerodynamic reference point & x,y,z values w units
                JLabel aeroRefPntLbl = new JLabel("AeroDynamic Reference Point(*)");
                aeroRefPntLbl.setBounds(10, 200, 300, 20);
                panel.add(aeroRefPntLbl);

                //temp: currently not getting correct values
                // x y and z values for aero ref point
                textFieldWLabel areoX = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"X = ",30,240,120,20,String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue()),80,240,100,20);

                textFieldWLabel areoY = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"Y = ",210,240,120,20,String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue()),260,240,100,20);

                textFieldWLabel areoZ = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"Z = ",390,240,120,20,String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue()),440,240,100,20);
                
                //unit for aerodynamic ref point
                JLabel aeroUnit = new JLabel("unit here");
                aeroUnit.setBounds(590, 240, 100, 20);
                panel.add(aeroUnit);

                // Eye Point
                JLabel eyePoint = new JLabel("Eye Point");
                eyePoint.setBounds(10, 280, 300, 20);
                panel.add(eyePoint);
                // x y and z values for eypoint
                textFieldWLabel eyeX = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"X = ",30,320,120,20,String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue()),80,320,100,20);

                textFieldWLabel eyeY = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"Y = ",210,320,120,20,String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue()),260,320,100,20);

                textFieldWLabel eyeZ = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"Z = ",390,320,120,20,String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue()),440,320,100,20);
                
                //unit for eyepoint
                JLabel eyePUnit = new JLabel("unit here");
                eyePUnit.setBounds(590, 320, 100, 20);
                panel.add(eyePUnit);

                // Visual Reference Point
                JLabel visRefPointLbl = new JLabel("Visual Reference Point(*)");
                visRefPointLbl.setBounds(10, 360, 300, 20);
                panel.add(visRefPointLbl);

                // x y and z values for visual ref point
                textFieldWLabel visX = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"X = ",30,400,120,20,String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue()),80,400,100,20);

                textFieldWLabel visY = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"Y = ",210,400,120,20,String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue()),260,400,100,20);

                textFieldWLabel visZ = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"Z = ",390,400,120,20,String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue()),440,400,100,20);
                
                 //unit for eyepoint
                JLabel visRefPntUnit = new JLabel("unit here");
                visRefPntUnit.setBounds(590, 400, 100, 20);
                panel.add(visRefPntUnit);

                panel.add(new JLabel("-", SwingConstants.CENTER), BorderLayout.CENTER);


		System.out.println("Locations:");
		for (Metrics.Location  l : DS.cfg.getMetrics().getLocation() ) {
                     System.out.println(l.getX());
                     System.out.println(l.getY());
                     System.out.println(l.getZ());
		     System.out.println(l.getUnit());
		}
		System.out.println("End Locations:");
        }
}

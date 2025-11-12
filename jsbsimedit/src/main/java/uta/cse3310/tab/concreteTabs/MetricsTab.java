package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;
import uta.cse3310.tab.widgets.textFieldWLabel;
import uta.cse3310.tab.widgets.textFieldWUnit;

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

                // text and fields for chord
                V = String.valueOf(DS.cfg.getMetrics().getChord().getValue());
                U = String.valueOf(DS.cfg.getMetrics().getChord().getUnit());;
                L = "chord(*) = ";
                textFieldWLabel L3 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getChord().setValue(Double),DS::setDirty,panel,L,10,120,200,20,V,100,120,100,20,U,220,120,80,20);

                // text and fields for htailarea
                V = String.valueOf(DS.cfg.getMetrics().getHtailarea().getValue());
                U = String.valueOf(DS.cfg.getMetrics().getHtailarea().getUnit());;
                L = "htailarea = ";
                textFieldWLabel L4 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getHtailarea().setValue(Double),DS::setDirty,panel,L,340,40,200,20,V,430,40,100,20,U,550,40,80,20);

                //tail arm text and field
                V = String.valueOf(DS.cfg.getMetrics().getHtailarm().getValue());
                U = String.valueOf(DS.cfg.getMetrics().getHtailarm().getUnit());;
                L = "htailarm = ";
                textFieldWLabel L5 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getHtailarm().setValue(Double),DS::setDirty,panel,L,340,80,200,20,V,430,80,100,20,U,550,80,80,20);

                // text and fields for vtailarea
                V = String.valueOf(DS.cfg.getMetrics().getVtailarea().getValue());
                U = String.valueOf(DS.cfg.getMetrics().getVtailarea().getUnit());;
                L = "vtailarea = ";
                textFieldWLabel L6 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarea().setValue(Double),DS::setDirty,panel,L,340,120,200,20,V,430,120,100,20,U,550,120,80,20);

                // text and fields for vtailarm
                V = String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue());
                U = String.valueOf(DS.cfg.getMetrics().getVtailarm().getUnit());;
                L = "vtailarm = ";
                textFieldWLabel L7 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,L,340,160,200,20,V,430,160,100,20,U,550,160,80,20);

                // x y and z values for aero ref point
                Metrics.Location lc = DS.cfg.getMetrics().getLocation().get(0);//plan to simplify this to account for out bounds cases and reduce line count
                U = String.valueOf(lc.getUnit());
                textFieldWUnit aeroLblWUnit = new textFieldWUnit(panel,"AeroDynamic Reference Point(*)",10, 200, 300, 20,U,590, 240, 100, 20);
                textFieldWLabel areoX = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"X = ",30,240,120,20,String.valueOf(lc.getX()),80,240,100,20);
                textFieldWLabel areoY = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"Y = ",210,240,120,20,String.valueOf(lc.getY()),260,240,100,20);
                textFieldWLabel areoZ = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"Z = ",390,240,120,20,String.valueOf(lc.getZ()),440,240,100,20);

                // x y and z values for eypoint
                lc = DS.cfg.getMetrics().getLocation().get(1);
                U = String.valueOf(lc.getUnit());
                textFieldWUnit eyeFieldWUnit = new textFieldWUnit(panel,"Eye Point",10, 280, 300, 20,U,590, 320, 100, 20);
                textFieldWLabel eyeX = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"X = ",30,320,120,20,String.valueOf(lc.getX()),80,320,100,20);
                textFieldWLabel eyeY = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"Y = ",210,320,120,20,String.valueOf(lc.getY()),260,320,100,20);
                textFieldWLabel eyeZ = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"Z = ",390,320,120,20,String.valueOf(lc.getZ()),440,320,100,20);

                // x y and z values for visual ref point
                lc = DS.cfg.getMetrics().getLocation().get(2);
                U = String.valueOf(lc.getUnit());
                textFieldWUnit visFieldWUnit = new textFieldWUnit(panel,"Visual Reference Point(*)",10, 360, 300, 20,U,590, 400, 100, 20);
                textFieldWLabel visX = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"X = ",30,400,120,20,String.valueOf(lc.getX()),80,400,100,20);
                textFieldWLabel visY = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"Y = ",210,400,120,20,String.valueOf(lc.getY()),260,400,100,20);
                textFieldWLabel visZ = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,"Z = ",390,400,120,20,String.valueOf(lc.getZ()),440,400,100,20);

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

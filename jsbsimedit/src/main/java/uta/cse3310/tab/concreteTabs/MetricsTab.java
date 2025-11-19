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
import generated.LengthUnit;
import generated.AreaUnit;

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
		String V,L,U;

                // Wingarea
		V = String.valueOf(DS.cfg.getMetrics().getWingarea().getValue());
                L = "wingarea(*) = ";		 // just to make the next line simpler U,220,40,80,20
                textFieldWLabel L1 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getWingarea().setValue(Double),DS::setDirty,panel,L,10,40,200,20,V,100,40,100,20);
                JComboBox<AreaUnit> wingareaUnit = new JComboBox<>(AreaUnit.values());
                wingareaUnit.setSelectedItem(DS.cfg.getMetrics().getWingarea().getUnit());
                wingareaUnit.setBounds(220, 40, 80, 20);
                panel.add(wingareaUnit);
                wingareaUnit.addActionListener(e -> {
                    DS.cfg.getMetrics().getWingarea().setUnit((AreaUnit) wingareaUnit.getSelectedItem());
                    DS.setDirty();
                });

                // wingspan
                V = String.valueOf(DS.cfg.getMetrics().getWingspan().getValue());
		L = "wingspan(*) = ";
                textFieldWLabel L2 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getWingspan().setValue(Double),DS::setDirty,panel,L,10,80,200,20,V,100,80,100,20);
                JComboBox<LengthUnit> wingspanUnit = new JComboBox<>(LengthUnit.values());
                wingspanUnit.setSelectedItem(DS.cfg.getMetrics().getWingspan().getUnit());
                wingspanUnit.setBounds(220, 80, 80, 20);
                panel.add(wingspanUnit);
                wingspanUnit.addActionListener(e -> {
                    DS.cfg.getMetrics().getWingspan().setUnit((LengthUnit) wingspanUnit.getSelectedItem());
                    DS.setDirty();
                });

                // text and fields for chord
                V = String.valueOf(DS.cfg.getMetrics().getChord().getValue());
                L = "chord(*) = ";
                textFieldWLabel L3 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getChord().setValue(Double),DS::setDirty,panel,L,10,120,200,20,V,100,120,100,20);
                JComboBox<LengthUnit> chordUnit = new JComboBox<>(LengthUnit.values());
                chordUnit.setSelectedItem(DS.cfg.getMetrics().getChord().getUnit());
                chordUnit.setBounds(220, 120, 80, 20);
                panel.add(chordUnit);
                chordUnit.addActionListener(e -> {
                    DS.cfg.getMetrics().getChord().setUnit((LengthUnit) chordUnit.getSelectedItem());
                    DS.setDirty();
                });

                // text and fields for htailarea
                V = String.valueOf(DS.cfg.getMetrics().getHtailarea().getValue());
                L = "htailarea = ";
                textFieldWLabel L4 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getHtailarea().setValue(Double),DS::setDirty,panel,L,340,40,200,20,V,430,40,100,20);
                JComboBox<AreaUnit> htailareaUnit = new JComboBox<>(AreaUnit.values());
                htailareaUnit.setSelectedItem(DS.cfg.getMetrics().getHtailarea().getUnit());
                htailareaUnit.setBounds(550, 40, 80, 20);
                panel.add(htailareaUnit);
                htailareaUnit.addActionListener(e -> {
                    DS.cfg.getMetrics().getHtailarea().setUnit((AreaUnit) htailareaUnit.getSelectedItem());
                    DS.setDirty();
                });

                //tail arm text and field
                V = String.valueOf(DS.cfg.getMetrics().getHtailarm().getValue());
                L = "htailarm = ";
                textFieldWLabel L5 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getHtailarm().setValue(Double),DS::setDirty,panel,L,340,80,200,20,V,430,80,100,20);
                JComboBox<LengthUnit> htailarmUnit = new JComboBox<>(LengthUnit.values());
                htailarmUnit.setSelectedItem(DS.cfg.getMetrics().getHtailarm().getUnit());
                htailarmUnit.setBounds(550, 80, 80, 20);
                panel.add(htailarmUnit);
                htailarmUnit.addActionListener(e -> {
                    DS.cfg.getMetrics().getHtailarm().setUnit((LengthUnit) htailarmUnit.getSelectedItem());
                    DS.setDirty();
                });

                // text and fields for vtailarea
                V = String.valueOf(DS.cfg.getMetrics().getVtailarea().getValue());
                L = "vtailarea = ";
                textFieldWLabel L6 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarea().setValue(Double),DS::setDirty,panel,L,340,120,200,20,V,430,120,100,20);
                JComboBox<AreaUnit> vtailareaUnit = new JComboBox<>(AreaUnit.values());
                vtailareaUnit.setSelectedItem(DS.cfg.getMetrics().getVtailarea().getUnit());
                vtailareaUnit.setBounds(550, 120, 80, 20);
                panel.add(vtailareaUnit);
                vtailareaUnit.addActionListener(e -> {
                    DS.cfg.getMetrics().getVtailarea().setUnit((AreaUnit) vtailareaUnit.getSelectedItem());
                    DS.setDirty();
                });

                // text and fields for vtailarm
                V = String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue());
                L = "vtailarm = ";
                textFieldWLabel L7 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,L,340,160,200,20,V,430,160,100,20);
                JComboBox<LengthUnit> vtailarmUnit = new JComboBox<>(LengthUnit.values());
                vtailarmUnit.setSelectedItem(DS.cfg.getMetrics().getVtailarm().getUnit());
                vtailarmUnit.setBounds(550, 160, 80, 20);
                panel.add(vtailarmUnit);
                vtailarmUnit.addActionListener(e -> {
                    DS.cfg.getMetrics().getVtailarm().setUnit((LengthUnit) vtailarmUnit.getSelectedItem());
                    DS.setDirty();
                });

                // x y and z values for aero ref point
                Metrics.Location Aero = DS.cfg.getMetrics().getLocation().get(0);//plan to simplify this to account for out bounds cases and reduce line count
                U = String.valueOf(Aero.getUnit());
                textFieldWUnit aeroLblWUnit = new textFieldWUnit(panel,DS::setDirty,Aero,"AeroDynamic Reference Point(*)",10, 200, 300, 20,"Unit : ",590, 240, 60, 20,U,660,240,100,20);
                textFieldWLabel areoX = new textFieldWLabel((Double) -> Aero.getX(),DS::setDirty,panel,"X = ",30,240,120,20,String.valueOf(Aero.getX()),80,240,100,20);
                textFieldWLabel areoY = new textFieldWLabel((Double) -> Aero.getY(),DS::setDirty,panel,"Y = ",210,240,120,20,String.valueOf(Aero.getY()),260,240,100,20);
                textFieldWLabel areoZ = new textFieldWLabel((Double) -> Aero.getZ(),DS::setDirty,panel,"Z = ",390,240,120,20,String.valueOf(Aero.getZ()),440,240,100,20);

                //x y and z values for eypoint
                Metrics.Location Eye = DS.cfg.getMetrics().getLocation().get(1);
                U = String.valueOf(Eye.getUnit());
                textFieldWUnit eyeFieldWUnit = new textFieldWUnit(panel,DS::setDirty,Eye,"Eye Point",10, 280, 300, 20,"Unit : ",590, 320, 60, 20,U,660,320,100,20);
                textFieldWLabel eyeX = new textFieldWLabel((Double) -> Eye.getX(),DS::setDirty,panel,"X = ",30,320,120,20,String.valueOf(Eye.getX()),80,320,100,20);
                textFieldWLabel eyeY = new textFieldWLabel((Double) -> Eye.getY(),DS::setDirty,panel,"Y = ",210,320,120,20,String.valueOf(Eye.getY()),260,320,100,20);
                textFieldWLabel eyeZ = new textFieldWLabel((Double) -> Eye.getZ(),DS::setDirty,panel,"Z = ",390,320,120,20,String.valueOf(Eye.getZ()),440,320,100,20);

                // x y and z values for visual ref point
                Metrics.Location Vis = DS.cfg.getMetrics().getLocation().get(2);
                U = String.valueOf(Vis.getUnit());
                textFieldWUnit visFieldWUnit = new textFieldWUnit(panel,DS::setDirty,Vis,"Visual Reference Point(*)",10, 360, 300, 20,"Unit : ",590, 400, 60, 20,U,660,400,100,20);
                textFieldWLabel visX = new textFieldWLabel((Double) -> Vis.getX(),DS::setDirty,panel,"X = ",30,400,120,20,String.valueOf(Vis.getX()),80,400,100,20);
                textFieldWLabel visY = new textFieldWLabel((Double) -> Vis.getY(),DS::setDirty,panel,"Y = ",210,400,120,20,String.valueOf(Vis.getY()),260,400,100,20);
                textFieldWLabel visZ = new textFieldWLabel((Double) -> Vis.getZ(),DS::setDirty,panel,"Z = ",390,400,120,20,String.valueOf(Vis.getZ()),440,400,100,20);

                panel.add(new JLabel("", SwingConstants.CENTER), BorderLayout.CENTER);
		// System.out.println("Locations:");
		// for (Metrics.Location  l : DS.cfg.getMetrics().getLocation() ) {
                //      System.out.println(l.getX());
                //      System.out.println(l.getY());
                //      System.out.println(l.getZ());
		//      System.out.println(l.getUnit());
		// }
		// System.out.println("End Locations:");
        }
}

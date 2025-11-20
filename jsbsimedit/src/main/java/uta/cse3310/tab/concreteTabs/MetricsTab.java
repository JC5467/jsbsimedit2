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
                TF = tf;
                panel.add(new JLabel("No aircraft file read.", SwingConstants.CENTER), BorderLayout.CENTER);
        }

        public void loadData() {
                panel.removeAll();
		String V,L,U;
                // Wingarea
		V = String.valueOf(DS.cfg.getMetrics().getWingarea().getValue());
                U = String.valueOf(DS.cfg.getMetrics().getWingarea().getUnit());
                L = "wingarea(*) = ";		 // just to make the next line simpler U,220,40,80,20
                textFieldWLabel L1 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getWingarea().setValue(Double),DS::setDirty,panel,L,10,40,200,20,V,100,40,100,20,U,220, 40, 80, 20);
                // JComboBox<AreaUnit> wingareaUnit = new JComboBox<>(AreaUnit.values());
                // wingareaUnit.setSelectedItem(DS.cfg.getMetrics().getWingarea().getUnit());
                // wingareaUnit.setBounds(220, 40, 80, 20);
                // panel.add(wingareaUnit);
                // wingareaUnit.addActionListener(e -> {
                //     DS.cfg.getMetrics().getWingarea().setUnit((AreaUnit) wingareaUnit.getSelectedItem());
                //     DS.setDirty();
                // });

                // wingspan
                V = String.valueOf(DS.cfg.getMetrics().getWingspan().getValue());
                U = String.valueOf(DS.cfg.getMetrics().getWingspan().getUnit());
		L = "wingspan(*) = ";
                textFieldWLabel L2 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getWingspan().setValue(Double),DS::setDirty,panel,L,10,80,200,20,V,100,80,100,20,U,220, 80, 80, 20);

                // text and fields for chord
                V = String.valueOf(DS.cfg.getMetrics().getChord().getValue());
                U = String.valueOf(DS.cfg.getMetrics().getChord().getUnit());;
                L = "chord(*) = ";
                textFieldWLabel L3 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getChord().setValue(Double),DS::setDirty,panel,L,10,120,200,20,V,100,120,100,20,U,220, 120, 80, 20);

                // text and fields for htailarea
                V = String.valueOf(DS.cfg.getMetrics().getHtailarea().getValue());
                U = String.valueOf(DS.cfg.getMetrics().getHtailarea().getUnit());;
                L = "htailarea = ";
                textFieldWLabel L4 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getHtailarea().setValue(Double),DS::setDirty,panel,L,340,40,200,20,V,430,40,100,20,U,550, 40, 80, 20);

                //tail arm text and field
                V = String.valueOf(DS.cfg.getMetrics().getHtailarm().getValue());
                U = String.valueOf(DS.cfg.getMetrics().getHtailarm().getUnit());;
                L = "htailarm = ";
                textFieldWLabel L5 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getHtailarm().setValue(Double),DS::setDirty,panel,L,340,80,200,20,V,430,80,100,20,U,550, 80, 80, 20);

                // text and fields for vtailarea
                V = String.valueOf(DS.cfg.getMetrics().getVtailarea().getValue());
                U = String.valueOf(DS.cfg.getMetrics().getVtailarea().getUnit());;
                L = "vtailarea = ";
                textFieldWLabel L6 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarea().setValue(Double),DS::setDirty,panel,L,340,120,200,20,V,430,120,100,20,U,550, 120, 80, 20);

                // text and fields for vtailarm
                V = String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue());
                U = String.valueOf(DS.cfg.getMetrics().getVtailarm().getUnit());;
                L = "vtailarm = ";
                textFieldWLabel L7 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),DS::setDirty,panel,L,340,160,200,20,V,430,160,100,20,U,550, 160, 80, 20);

                // x y and z values for aero ref point
                Metrics.Location Aero = DS.cfg.getMetrics().getLocation().get(0);//plan to simplify this to account for out bounds cases and reduce line count
                U = String.valueOf(Aero.getUnit());
                textFieldWUnit aeroLblWUnit = new textFieldWUnit(panel,DS::setDirty,Aero,"AeroDynamic Reference Point(*)",10, 200, 300, 20,"Unit : ",590, 240, 60, 20,U,660,240,100,20);
                textFieldWLabel areoX = new textFieldWLabel((Double) -> Aero.setX(Double),DS::setDirty,panel,"X = ",30,240,120,20,String.valueOf(Aero.getX()),80,240,100,20);
                textFieldWLabel areoY = new textFieldWLabel((Double) -> Aero.setY(Double),DS::setDirty,panel,"Y = ",210,240,120,20,String.valueOf(Aero.getY()),260,240,100,20);
                textFieldWLabel areoZ = new textFieldWLabel((Double) -> Aero.setZ(Double),DS::setDirty,panel,"Z = ",390,240,120,20,String.valueOf(Aero.getZ()),440,240,100,20);

                //x y and z values for eypoint
                Metrics.Location Eye = DS.cfg.getMetrics().getLocation().get(1);
                U = String.valueOf(Eye.getUnit());
                textFieldWUnit eyeFieldWUnit = new textFieldWUnit(panel,DS::setDirty,Eye,"Eye Point",10, 280, 300, 20,"Unit : ",590, 320, 60, 20,U,660,320,100,20);
                textFieldWLabel eyeX = new textFieldWLabel((Double) -> Eye.setX(Double),DS::setDirty,panel,"X = ",30,320,120,20,String.valueOf(Eye.getX()),80,320,100,20);
                textFieldWLabel eyeY = new textFieldWLabel((Double) -> Eye.setY(Double),DS::setDirty,panel,"Y = ",210,320,120,20,String.valueOf(Eye.getY()),260,320,100,20);
                textFieldWLabel eyeZ = new textFieldWLabel((Double) -> Eye.setZ(Double),DS::setDirty,panel,"Z = ",390,320,120,20,String.valueOf(Eye.getZ()),440,320,100,20);

                // x y and z values for visual ref point
                Metrics.Location Vis = DS.cfg.getMetrics().getLocation().get(2);
                U = String.valueOf(Vis.getUnit());
                textFieldWUnit visFieldWUnit = new textFieldWUnit(panel,DS::setDirty,Vis,"Visual Reference Point(*)",10, 360, 300, 20,"Unit : ",590, 400, 60, 20,U,660,400,100,20);
                textFieldWLabel visX = new textFieldWLabel((Double) -> Vis.setX(Double),DS::setDirty,panel,"X = ",30,400,120,20,String.valueOf(Vis.getX()),80,400,100,20);
                textFieldWLabel visY = new textFieldWLabel((Double) -> Vis.setY(Double),DS::setDirty,panel,"Y = ",210,400,120,20,String.valueOf(Vis.getY()),260,400,100,20);
                textFieldWLabel visZ = new textFieldWLabel((Double) -> Vis.setZ(Double),DS::setDirty,panel,"Z = ",390,400,120,20,String.valueOf(Vis.getZ()),440,400,100,20);

                panel.add(new JLabel("", SwingConstants.CENTER), BorderLayout.CENTER);
        }
}

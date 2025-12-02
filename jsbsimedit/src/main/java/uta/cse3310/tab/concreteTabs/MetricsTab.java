package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;
import uta.cse3310.tab.widgets.textFieldWLabel;
import uta.cse3310.tab.widgets.textFieldWUnit;

import generated.Metrics;
import generated.Area;
import generated.Length;
import generated.LengthUnit;
import generated.AreaUnit;

public class MetricsTab extends simpleTab {

        public MetricsTab(tabFrame tf, dataStore ds, String label) {
                super(ds, label);
                TF = tf;
                panel.add(new JLabel("No aircraft file read.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        public void loadData() {
                panel.removeAll();
                String V, L, U;
                // Guard: if no configuration is loaded, show message and return
                if (DS == null || DS.cfg == null) {
                        panel.add(new JLabel("No aircraft file read.", SwingConstants.CENTER), BorderLayout.CENTER);
                        return;
                }
                // Ensure metrics section exists and has required sub-elements
                Metrics m = DS.cfg.getMetrics();
                if (m == null) {
                        m = new Metrics();
                        // create required children with sensible defaults
                        Area wingarea = new Area();
                        wingarea.setValue(0.0);
                        wingarea.setUnit(AreaUnit.FT_2);
                        m.setWingarea(wingarea);

                        Length wingspan = new Length();
                        wingspan.setValue(0.0);
                        wingspan.setUnit(LengthUnit.FT);
                        m.setWingspan(wingspan);

                        Length chord = new Length();
                        chord.setValue(0.0);
                        chord.setUnit(LengthUnit.FT);
                        m.setChord(chord);

                        // ensure three default locations (AERORP, EYEPOINT, VRP)
                        Metrics.Location aero = new Metrics.Location();
                        aero.setX(0.0);
                        aero.setY(0.0);
                        aero.setZ(0.0);
                        aero.setUnit(LengthUnit.FT);
                        aero.setName(generated.ReferencePoint.AERORP);

                        Metrics.Location eye = new Metrics.Location();
                        eye.setX(0.0);
                        eye.setY(0.0);
                        eye.setZ(0.0);
                        eye.setUnit(LengthUnit.FT);
                        eye.setName(generated.ReferencePoint.EYEPOINT);

                        Metrics.Location vis = new Metrics.Location();
                        vis.setX(0.0);
                        vis.setY(0.0);
                        vis.setZ(0.0);
                        vis.setUnit(LengthUnit.FT);
                        vis.setName(generated.ReferencePoint.VRP);

                        m.getLocation().add(aero);
                        m.getLocation().add(eye);
                        m.getLocation().add(vis);

                        DS.cfg.setMetrics(m);
                }

                // Ensure required simple children are present
                if (m.getWingarea() == null) {
                        Area wingarea = new Area();
                        wingarea.setValue(0.0);
                        wingarea.setUnit(AreaUnit.FT_2);
                        m.setWingarea(wingarea);
                }
                if (m.getWingspan() == null) {
                        Length wingspan = new Length();
                        wingspan.setValue(0.0);
                        wingspan.setUnit(LengthUnit.FT);
                        m.setWingspan(wingspan);
                }
                if (m.getChord() == null) {
                        Length chord = new Length();
                        chord.setValue(0.0);
                        chord.setUnit(LengthUnit.FT);
                        m.setChord(chord);
                }
                // Ensure at least three location entries exist
                while (m.getLocation().size() < 3) {
                        Metrics.Location loc = new Metrics.Location();
                        loc.setX(0.0);
                        loc.setY(0.0);
                        loc.setZ(0.0);
                        loc.setUnit(LengthUnit.FT);
                        // set a name for the first three entries if possible
                        int idx = m.getLocation().size();
                        if (idx == 0) loc.setName(generated.ReferencePoint.AERORP);
                        else if (idx == 1) loc.setName(generated.ReferencePoint.EYEPOINT);
                        else if (idx == 2) loc.setName(generated.ReferencePoint.VRP);
                        m.getLocation().add(loc);
                }
                // text and fields for Wingarea
                V = String.valueOf(m.getWingarea().getValue());
                U = String.valueOf(m.getWingarea().getUnit());
                L = "wingarea(*) = ";
                textFieldWLabel L1 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getWingarea().setValue(Double),
                                DS::setDirty, panel, L, 10, 40, 200, 20, V, 100, 40, 100, 20, U, 220, 40, 80, 20,
                                unit -> DS.cfg.getMetrics().getWingarea().setUnit((AreaUnit) unit));
                // text and fields for wingspan
                V = String.valueOf(m.getWingspan().getValue());
                U = String.valueOf(m.getWingspan().getUnit());
                L = "wingspan(*) = ";
                textFieldWLabel L2 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getWingspan().setValue(Double),
                                DS::setDirty, panel, L, 10, 80, 200, 20, V, 100, 80, 100, 20, U, 220, 80, 80, 20,
                                unit -> DS.cfg.getMetrics().getWingspan().setUnit((LengthUnit) unit));
                // text and fields for chord
                V = String.valueOf(m.getChord().getValue());
                U = String.valueOf(m.getChord().getUnit());
                L = "chord(*) = ";
                textFieldWLabel L3 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getChord().setValue(Double),
                                DS::setDirty, panel, L, 10, 120, 200, 20, V, 100, 120, 100, 20, U, 220, 120, 80, 20,
                                unit -> DS.cfg.getMetrics().getChord().setUnit((LengthUnit) unit));
                // text and fields for htailarea
                if(DS.cfg.getMetrics().getHtailarea() == null){
                        Area htailarea = new Area();
                        htailarea.setValue(0.0);
                        htailarea.setUnit(AreaUnit.FT_2);
                        DS.cfg.getMetrics().setHtailarea(htailarea);
                        V = String.valueOf(DS.cfg.getMetrics().getHtailarea().getValue());
                        U = String.valueOf(DS.cfg.getMetrics().getHtailarea().getUnit());
                }
                else{
                        V = String.valueOf(DS.cfg.getMetrics().getHtailarea().getValue());
                        U = String.valueOf(DS.cfg.getMetrics().getHtailarea().getUnit());
                }
                L = "htailarea = ";
                textFieldWLabel L4 = new textFieldWLabel(
                                (Double) -> DS.cfg.getMetrics().getHtailarea().setValue(Double), DS::setDirty, panel, L,
                                340, 40, 200, 20, V, 430, 40, 100, 20, U, 550, 40, 80, 20,
                                unit -> DS.cfg.getMetrics().getHtailarea().setUnit((AreaUnit) unit));
                // text and fields for htailarm
                if(DS.cfg.getMetrics().getHtailarm() == null){
                        Length htailarm = new Length();
                        htailarm.setValue(0.0);
                        htailarm.setUnit(LengthUnit.FT);
                        DS.cfg.getMetrics().setHtailarm(htailarm);
                        V = String.valueOf(DS.cfg.getMetrics().getHtailarm().getValue());
                        U = String.valueOf(DS.cfg.getMetrics().getHtailarm().getUnit()); 
                }
                else{
                        V = String.valueOf(DS.cfg.getMetrics().getHtailarm().getValue());
                        U = String.valueOf(DS.cfg.getMetrics().getHtailarm().getUnit());
                }
                L = "htailarm = ";
                textFieldWLabel L5 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getHtailarm().setValue(Double),
                                DS::setDirty, panel, L, 340, 80, 200, 20, V, 430, 80, 100, 20, U, 550, 80, 80, 20,
                                unit -> DS.cfg.getMetrics().getHtailarm().setUnit((LengthUnit) unit));
                // text and fields for vtailarea
                if(DS.cfg.getMetrics().getVtailarea() == null){
                        Area vtailarea = new Area();
                        vtailarea.setValue(0.0);
                        vtailarea.setUnit(AreaUnit.FT_2);
                        DS.cfg.getMetrics().setVtailarea(vtailarea);
                        V = String.valueOf(DS.cfg.getMetrics().getVtailarea().getValue());
                        U = String.valueOf(DS.cfg.getMetrics().getVtailarea().getUnit()); 
                }
                else{
                        V = String.valueOf(DS.cfg.getMetrics().getVtailarea().getValue());
                        U = String.valueOf(DS.cfg.getMetrics().getVtailarea().getUnit());
                }
                L = "vtailarea = ";
                textFieldWLabel L6 = new textFieldWLabel(
                                (Double) -> DS.cfg.getMetrics().getVtailarea().setValue(Double), DS::setDirty, panel, L,
                                340, 120, 200, 20, V, 430, 120, 100, 20, U, 550, 120, 80, 20,
                                unit -> DS.cfg.getMetrics().getVtailarea().setUnit((AreaUnit) unit));
                // text and fields for vtailarm
                if(DS.cfg.getMetrics().getVtailarm() == null){
                        Length vtailarm = new Length();
                        vtailarm.setValue(0.0);
                        vtailarm.setUnit(LengthUnit.FT);
                        DS.cfg.getMetrics().setVtailarm(vtailarm);
                        V = String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue());
                        U = String.valueOf(DS.cfg.getMetrics().getVtailarm().getUnit()); 
                }
                else{
                        V = String.valueOf(DS.cfg.getMetrics().getVtailarm().getValue());
                        U = String.valueOf(DS.cfg.getMetrics().getVtailarm().getUnit());
                }
                L = "vtailarm = ";
                textFieldWLabel L7 = new textFieldWLabel((Double) -> DS.cfg.getMetrics().getVtailarm().setValue(Double),
                                DS::setDirty, panel, L, 340, 160, 200, 20, V, 430, 160, 100, 20, U, 550, 160, 80, 20,
                                unit -> DS.cfg.getMetrics().getVtailarm().setUnit((LengthUnit) unit));
                // x y and z values for aero ref point
                Metrics.Location Aero = DS.cfg.getMetrics().getLocation().get(0);
                U = String.valueOf(Aero.getUnit());
                textFieldWUnit aeroLblWUnit = new textFieldWUnit(panel, DS::setDirty, Aero,
                                "AeroDynamic Reference Point(*)", 10, 200, 300, 20, "Unit : ", 590, 240, 60, 20, U, 660,
                                240, 100, 20);
                textFieldWLabel areoX = new textFieldWLabel((Double) -> Aero.setX(Double), DS::setDirty, panel, "X = ",
                                30, 240, 120, 20, String.valueOf(Aero.getX()), 80, 240, 100, 20);
                textFieldWLabel areoY = new textFieldWLabel((Double) -> Aero.setY(Double), DS::setDirty, panel, "Y = ",
                                210, 240, 120, 20, String.valueOf(Aero.getY()), 260, 240, 100, 20);
                textFieldWLabel areoZ = new textFieldWLabel((Double) -> Aero.setZ(Double), DS::setDirty, panel, "Z = ",
                                390, 240, 120, 20, String.valueOf(Aero.getZ()), 440, 240, 100, 20);
                // x y and z values for eypoint
                Metrics.Location Eye = DS.cfg.getMetrics().getLocation().get(1);
                U = String.valueOf(Eye.getUnit());
                textFieldWUnit eyeFieldWUnit = new textFieldWUnit(panel, DS::setDirty, Eye, "Eye Point", 10, 280, 300,
                                20, "Unit : ", 590, 320, 60, 20, U, 660, 320, 100, 20);
                textFieldWLabel eyeX = new textFieldWLabel((Double) -> Eye.setX(Double), DS::setDirty, panel, "X = ",
                                30, 320, 120, 20, String.valueOf(Eye.getX()), 80, 320, 100, 20);
                textFieldWLabel eyeY = new textFieldWLabel((Double) -> Eye.setY(Double), DS::setDirty, panel, "Y = ",
                                210, 320, 120, 20, String.valueOf(Eye.getY()), 260, 320, 100, 20);
                textFieldWLabel eyeZ = new textFieldWLabel((Double) -> Eye.setZ(Double), DS::setDirty, panel, "Z = ",
                                390, 320, 120, 20, String.valueOf(Eye.getZ()), 440, 320, 100, 20);
                // x y and z values for visual ref point
                Metrics.Location Vis = DS.cfg.getMetrics().getLocation().get(2);
                U = String.valueOf(Vis.getUnit());
                textFieldWUnit visFieldWUnit = new textFieldWUnit(panel, DS::setDirty, Vis, "Visual Reference Point(*)",
                                10, 360, 300, 20, "Unit : ", 590, 400, 60, 20, U, 660, 400, 100, 20);
                textFieldWLabel visX = new textFieldWLabel((Double) -> Vis.setX(Double), DS::setDirty, panel, "X = ",
                                30, 400, 120, 20, String.valueOf(Vis.getX()), 80, 400, 100, 20);
                textFieldWLabel visY = new textFieldWLabel((Double) -> Vis.setY(Double), DS::setDirty, panel, "Y = ",
                                210, 400, 120, 20, String.valueOf(Vis.getY()), 260, 400, 100, 20);
                textFieldWLabel visZ = new textFieldWLabel((Double) -> Vis.setZ(Double), DS::setDirty, panel, "Z = ",
                                390, 400, 120, 20, String.valueOf(Vis.getZ()), 440, 400, 100, 20);
                panel.add(new JLabel("", SwingConstants.CENTER), BorderLayout.CENTER);
        }

}

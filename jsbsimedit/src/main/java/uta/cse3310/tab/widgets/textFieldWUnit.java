package uta.cse3310.tab.widgets;

import uta.cse3310.tab.widgets.dirtyFunction;
import uta.cse3310.tab.widgets.setValFunction;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import generated.Metrics;
import generated.AreaUnit;
import generated.LengthUnit;

public class textFieldWUnit {
        private JLabel label;
        private JLabel jl;

        public textFieldWUnit(JPanel panel, dirtyFunction DF, Metrics.Location loc, String labelText, Integer labelX,
                        Integer labelY,
                        Integer labelW, Integer labelH, String unitS, Integer unitX, Integer unitY, Integer unitW,
                        Integer unitH, String unit, Integer boxX, Integer boxY, Integer boxW,
                        Integer boxH) {
                String search = "area";

                label = new JLabel(labelText);
                label.setBounds(labelX, labelY, labelW, labelH);
                panel.add(label);

                jl = new JLabel(unitS);
                jl.setBounds(unitX, unitY, unitW, unitH);
                panel.add(jl);

                if (labelText.toLowerCase().indexOf(search.toLowerCase()) != -1) {
                        JComboBox unitBoxArea = new JComboBox<>(AreaUnit.values());
                        AreaUnit AU = AreaUnit.valueOf(unit);
                        unitBoxArea.setSelectedItem(AU);
                        unitBoxArea.setBounds(boxX, boxY, boxW, boxH);
                        panel.add(unitBoxArea);
                        unitBoxArea.addActionListener(e -> {
                                DF.set();
                                loc.setUnit((LengthUnit) unitBoxArea.getSelectedItem());
                        });
                } else {
                        JComboBox unitBoxLen = new JComboBox<>(LengthUnit.values());
                        LengthUnit LU = LengthUnit.valueOf(unit);
                        unitBoxLen.setSelectedItem(LU);
                        unitBoxLen.setBounds(boxX, boxY, boxW, boxH);
                        panel.add(unitBoxLen);
                        unitBoxLen.addActionListener(e -> {
                                DF.set();
                                loc.setUnit((LengthUnit) unitBoxLen.getSelectedItem());
                        });
                }
        }
}

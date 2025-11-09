package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

public class MetricsTab extends simpleTab {
    // attributes that only pertain to the 'concrete' tab called oneTab

    public MetricsTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        System.out.println("in Metrics constructor");

        //rough outline just to test xml read and check if values appear

        //using setbounds for now will fix formting better as it
        //currently needs the tab to be resized to see everything.
        //needs drop down values for units on each field still will add later.

        //text and fields for wingarea
        JLabel wingarea = new JLabel("wingarea(*) = ");
        wingarea.setBounds(10,40,200,20);
        panel.add(wingarea);
        
        JTextField wArea = new JTextField();
        wArea.setBounds(100,40,150,20);
        panel.add(wArea);
        
        //text and fields for wingspan
        JLabel wingspan = new JLabel("wingspan(*) = ");
        wingspan.setBounds(10,80,200,20);
        panel.add(wingspan);
        
        JTextField wSpan = new JTextField();
        wSpan.setBounds(100,80,150,20);
        panel.add(wSpan);
        
        //text and fields for chord
        JLabel chordLbl = new JLabel("chord(*) = ");
        chordLbl.setBounds(10,120,200,20);
        panel.add(chordLbl);
        
        JTextField chord = new JTextField();
        chord.setBounds(100,120,150,20);
        panel.add(chord);
        
        //text and fields for htailarea
        JLabel htailArea = new JLabel("htailarea = ");
        htailArea.setBounds(400,40,200,20);
        panel.add(htailArea);
        
        JTextField htailA = new JTextField();
        htailA.setBounds(480,40,150,20);
        panel.add(htailA);

        //text and fields for htailarm
        JLabel htailArmLbl = new JLabel("htailarm = ");
        htailArmLbl.setBounds(400,80,200,20);
        panel.add(htailArmLbl);
        
        JTextField htailArm = new JTextField();
        htailArm.setBounds(480,80,150,20);
        panel.add(htailArm);
        
        //text and fields for vtailarea
        JLabel vtailArea = new JLabel("vtailarea = ");
        vtailArea.setBounds(400,120,200,20);
         panel.add(vtailArea);
                
        JTextField vtailA = new JTextField();
        vtailA.setBounds(480,120,150,20);
        panel.add(vtailA);

        //text and fields for vtailarm
        JLabel vtailArmLbl = new JLabel("vtailarm = ");
        vtailArmLbl.setBounds(400,160,200,20);
        panel.add(vtailArmLbl);
        
        JTextField vtailArm = new JTextField();
        vtailArm.setBounds(480,160,150,20);
        panel.add(vtailArm);

        //aerodynamic reference point & x,y,z values w units
        JLabel aeroRefPntLbl = new JLabel("AeroDynamic Reference Point(*)");
        aeroRefPntLbl.setBounds(10,200,300,20);
        panel.add(aeroRefPntLbl);

        //x y and z fields
        JLabel aeroXlbl = new JLabel("X = ");
        aeroXlbl.setBounds(50,240,200,20);
        panel.add(aeroXlbl);
        
        JTextField aeroX = new JTextField();
        aeroX.setBounds(100,240,150,20);
        panel.add(aeroX);

        JLabel aeroYlbl = new JLabel("Y = ");
        aeroYlbl.setBounds(270,240,200,20);
        panel.add(aeroYlbl);
        
        JTextField aeroY = new JTextField();
        aeroY.setBounds(320,240,150,20);
        panel.add(aeroY);

        JLabel aeroZlbl = new JLabel("Z = ");
        aeroZlbl.setBounds(490,240,200,20);
        panel.add(aeroZlbl);
        
        JTextField aeroZ = new JTextField();
        aeroZ.setBounds(560,240,150,20);
        panel.add(aeroZ);

        //Eye Point
        JLabel eyePoint = new JLabel("Eye Point");
        eyePoint.setBounds(10,280,300,20);
        panel.add(eyePoint);

        //x y and z values for eye pint
        JLabel eyeXlbl = new JLabel("X = ");
        eyeXlbl.setBounds(50,320,200,20);
        panel.add(eyeXlbl);
        
        JTextField eyeX = new JTextField();
        eyeX.setBounds(100,320,150,20);
        panel.add(eyeX);

        JLabel eyeYlbl = new JLabel("Y = ");
        eyeYlbl.setBounds(270,320,200,20);
        panel.add(eyeYlbl);
        
        JTextField eyeY = new JTextField();
        eyeY.setBounds(320,320,150,20);
        panel.add(eyeY);

        JLabel eyeZlbl = new JLabel("Z = ");
        eyeZlbl.setBounds(490,320,200,20);
        panel.add(eyeZlbl);
        
        JTextField eyeZ = new JTextField();
        eyeZ.setBounds(560,320,150,20);
        panel.add(eyeZ);

        //Visual Reference Point
        JLabel visRefPointLbl = new JLabel("Visual Reference Point(*)");
        visRefPointLbl.setBounds(10,360,300,20);
        panel.add(visRefPointLbl);

        //x y and z values for visual ref point
        JLabel refXlbl = new JLabel("X = ");
        refXlbl.setBounds(50,400,200,20);
        panel.add(refXlbl);
        
        JTextField refX = new JTextField();
        refX.setBounds(100,400,150,20);
        panel.add(refX);

        JLabel refYlbl = new JLabel("Y = ");
        refYlbl.setBounds(270,400,200,20);
        panel.add(refYlbl);
        
        JTextField refY = new JTextField();
        refY.setBounds(320,400,150,20);
        panel.add(refY);

        JLabel refZlbl = new JLabel("Z = ");
        refZlbl.setBounds(490,400,200,20);
        panel.add(refZlbl);
        
        JTextField refZ = new JTextField();
        refZ.setBounds(560,400,150,20);
        panel.add(refZ);
        
        // this is what is unique about 1 tab
        TF = tf;
        panel.add(new JLabel("Content of Tab 1", SwingConstants.CENTER), BorderLayout.CENTER);

    }

}

package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;
import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

import generated.Engine;
import generated.Mass;
import generated.Tank;


public class PropulsionTab extends simpleTab 
{
    private JTabbedPane tabbedPane;
    private JList<String> availableEnginesList;
    private JList<String> availableThrustersList;
    private DefaultListModel<String> enginesModel;
    private DefaultListModel<String> thrustersModel;
    private DefaultListModel<String> subscribedModel;
    private DefaultListModel<String> tanksModel;

    public PropulsionTab(tabFrame tf, dataStore ds, String label) 
    {
        super(ds, label);
        TF = tf;
        

        panel.setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Available Engines tab
        enginesModel = new DefaultListModel<>();
        availableEnginesList = new JList<>(enginesModel);
        JScrollPane enginesScroll = new JScrollPane(availableEnginesList);
        tabbedPane.addTab("Available Engines", enginesScroll);



        // Available Thrusters tab
        thrustersModel = new DefaultListModel<>();
        availableThrustersList = new JList<>(thrustersModel);
        JScrollPane thrustersScroll = new JScrollPane(availableThrustersList);
        tabbedPane.addTab("Available Thrusters", thrustersScroll);



        // Subscribed Engines tab
        subscribedModel = new DefaultListModel<>();
        JList<String> subscribedList = new JList<>(subscribedModel);
        JScrollPane subscribedScroll = new JScrollPane(subscribedList);
        JPanel subscribedPanel = new JPanel(new BorderLayout());
        subscribedPanel.add(subscribedScroll, BorderLayout.CENTER);

        JPanel subscribedButtons = new JPanel();
        JButton newPair = new JButton("New Pair");
        JButton deletePair = new JButton("Delete Pair");
        JButton detailPair = new JButton("Detail Pair");
        subscribedButtons.add(newPair);
        subscribedButtons.add(deletePair);
        subscribedButtons.add(detailPair);
        subscribedPanel.add(subscribedButtons, BorderLayout.SOUTH);

        tabbedPane.addTab("Subscribed Engine(s)(*)", subscribedPanel);



        // Tanks tab
        tanksModel = new DefaultListModel<>();
        JList<String> tanksList = new JList<>(tanksModel);
        JScrollPane tanksScroll = new JScrollPane(tanksList);
        JPanel tanksPanel = new JPanel(new BorderLayout());
        tanksPanel.add(tanksScroll, BorderLayout.CENTER);

        JPanel tankButtons = new JPanel();
        JButton newTank = new JButton("New Tank");
        JButton deleteTank = new JButton("Delete Tank");
        JButton detailTank = new JButton("Detail Tank");
        tankButtons.add(newTank);
        tankButtons.add(deleteTank);
        tankButtons.add(detailTank);
        tanksPanel.add(tankButtons, BorderLayout.SOUTH);

        tabbedPane.addTab("Tanks", tanksPanel);

        panel.add(tabbedPane, BorderLayout.CENTER);
    }




@Override
public void loadData() 
{
    panel.removeAll();
    panel.setLayout(null);

    // Clear existing models instead of redeclaring new ones
    enginesModel.clear();
    thrustersModel.clear();
    subscribedModel.clear();
    tanksModel.clear();

    // Load engines and tanks from XML
    for (Object obj : DS.cfg.getPropulsion().getDocumentationOrPropertyOrFunction()) 
    {
        if (obj instanceof Engine) 
        {
            Engine eng = (Engine) obj;
            String name = eng.getName() != null ? eng.getName() : ""; // fix leading "null"
            String file = eng.getFile() != null ? eng.getFile() : "";
            String display = name + " (" + file + ")";
            enginesModel.addElement(display);
            thrustersModel.addElement(display);
        } 
        else if (obj instanceof Tank) 
        {
            Tank t = (Tank) obj;
            Mass cap = t.getCapacity();
            String capStr = cap != null ? cap.getValue() + " " + cap.getUnit() : "0 LBS";
            String text = "Tank (" + t.getType() + ") capacity=" + capStr;
            tanksModel.addElement(text);
        }
    }

    // Engines Tab
    JLabel lblEng = new JLabel("Available Engines:");
    lblEng.setBounds(10, 10, 200, 20);
    panel.add(lblEng);

    JList<String> enginesList = new JList<>(enginesModel);
    JScrollPane scrollEng = new JScrollPane(enginesList);
    scrollEng.setBounds(10, 35, 300, 150);
    panel.add(scrollEng);

    // Thrusters Tab
    JLabel lblThr = new JLabel("Available Thrusters:");
    lblThr.setBounds(330, 10, 200, 20);
    panel.add(lblThr);

    JList<String> thrustersList = new JList<>(thrustersModel);
    JScrollPane scrollThr = new JScrollPane(thrustersList);
    scrollThr.setBounds(330, 35, 300, 150);
    panel.add(scrollThr);

    // Subscribed Engines Tab
    JLabel lblSub = new JLabel("Subscribed Engine(s)(*):");
    lblSub.setBounds(10, 200, 300, 20);
    panel.add(lblSub);

    JList<String> subList = new JList<>(subscribedModel);
    JScrollPane scrollSub = new JScrollPane(subList);
    scrollSub.setBounds(10, 225, 300, 150);
    panel.add(scrollSub);

    JButton btnNewPair = new JButton("New Pair");
    btnNewPair.setBounds(10, 385, 100, 25);
    panel.add(btnNewPair);

    JButton btnDelPair = new JButton("Delete Pair");
    btnDelPair.setBounds(120, 385, 120, 25);
    panel.add(btnDelPair);

    JButton btnDetailPair = new JButton("Detail Pair");
    btnDetailPair.setBounds(250, 385, 120, 25);
    panel.add(btnDetailPair);

    // Tanks Tab
    JLabel lblTanks = new JLabel("Tanks:");
    lblTanks.setBounds(330, 200, 200, 20);
    panel.add(lblTanks);

    JList<String> tanksList = new JList<>(tanksModel);
    JScrollPane scrollTanks = new JScrollPane(tanksList);
    scrollTanks.setBounds(330, 225, 300, 150);
    panel.add(scrollTanks);

    JButton btnNewTank = new JButton("New Tank");
    btnNewTank.setBounds(380, 385, 100, 25);
    panel.add(btnNewTank);

    JButton btnDelTank = new JButton("Delete Tank");
    btnDelTank.setBounds(490, 385, 120, 25);
    panel.add(btnDelTank);

    JButton btnDetailTank = new JButton("Detail Tank");
    btnDetailTank.setBounds(620, 385, 120, 25);
    panel.add(btnDetailTank);

    // Populate tanksModel 
    tanksModel.clear();

    for (Object obj : DS.cfg.getPropulsion().getDocumentationOrPropertyOrFunction()) 
    {
        if (obj instanceof Tank) 
        {
            Tank t = (Tank) obj;

            String locStr = "(0,0,0) IN";
            if (t.getLocation() != null) 
            {
                locStr = "(" + t.getLocation().getX() + ", " 
                          + t.getLocation().getY() + ", " 
                          + t.getLocation().getZ() + ") " 
                          + t.getLocation().getUnit();
            }

            String capStr = "0 LBS";
            if (t.getCapacity() != null) 
            {
                capStr = t.getCapacity().getValue() + " " + t.getCapacity().getUnit();
            }

            String text = "Tank (" + t.getType() + ") location=" + locStr + " capacity=" + capStr;
            tanksModel.addElement(text);
        }
    }


    // BUTTON FUNCTIONALITY 

    // New Pair
    btnNewPair.addActionListener(e -> 
    {
        String eName = enginesList.getSelectedValue() != null ? enginesList.getSelectedValue() : "";
        String tName = thrustersList.getSelectedValue() != null ? thrustersList.getSelectedValue() : "";

        if (!eName.isEmpty() && !tName.isEmpty())
        {
            String pair = eName + " + " + tName;
            subscribedModel.addElement(pair);
        }
        else 
        {
            JOptionPane.showMessageDialog(panel,"Select both an engine and a thruster to create a pair.");
        }
    });

    btnDelPair.addActionListener(e -> 
    {
        String selected = subList.getSelectedValue();
        if (selected != null)
        {
            subscribedModel.removeElement(selected);
        }
    });

    btnDetailPair.addActionListener(e -> 
    {
        String selected = subList.getSelectedValue();
        if (selected != null)
        {
            JOptionPane.showMessageDialog(panel, "Details for: " + selected);
        }
    });

    // Tanks
    btnNewTank.addActionListener(e -> 
    {
        JTextField typeField = new JTextField();
        JTextField xField = new JTextField("0");
        JTextField yField = new JTextField("0");
        JTextField zField = new JTextField("0");
        JTextField locUnitField = new JTextField("IN");
        JTextField capField = new JTextField("0");
        JTextField capUnitField = new JTextField("LBS");

        JPanel inputPanel = new JPanel(new GridLayout(0,2));
        inputPanel.add(new JLabel("Tank Type:")); inputPanel.add(typeField);
        inputPanel.add(new JLabel("Location X:")); inputPanel.add(xField);
        inputPanel.add(new JLabel("Location Y:")); inputPanel.add(yField);
        inputPanel.add(new JLabel("Location Z:")); inputPanel.add(zField);
        inputPanel.add(new JLabel("Location Unit:")); inputPanel.add(locUnitField);
        inputPanel.add(new JLabel("Capacity:")); inputPanel.add(capField);
        inputPanel.add(new JLabel("Capacity Unit:")); inputPanel.add(capUnitField);

        int result = JOptionPane.showConfirmDialog(panel, inputPanel, "New Tank", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION)
        {
            String type = typeField.getText();
            String locStr = "(" + xField.getText() + "," + yField.getText() + "," + zField.getText() + ") " + locUnitField.getText();
            String capStr = capField.getText() + " " + capUnitField.getText();
            String newTankStr = "Tank (" + type + ") location=" + locStr + " capacity=" + capStr;

            tanksModel.addElement(newTankStr);
        }
    });

    btnDelTank.addActionListener(e -> 
    {
        String selected = tanksList.getSelectedValue();
        if (selected != null)
        {
            tanksModel.removeElement(selected);
        }
    });

    btnDetailTank.addActionListener(e -> 
    {
        String selected = tanksList.getSelectedValue();
        if (selected != null)
        {
            // Show full info including location and capacity
            JOptionPane.showMessageDialog(panel, "Details for tank:\n" + selected);
        }
    });

    panel.revalidate();
    panel.repaint();
}






}





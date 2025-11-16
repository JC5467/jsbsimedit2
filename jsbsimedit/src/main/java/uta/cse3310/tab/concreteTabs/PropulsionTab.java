package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;
import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

import generated.Engine;
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
        System.out.println("in Propulsion constructor");
        

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

    // Models for lists
    DefaultListModel<Engine> enginesModel = new DefaultListModel<>();
    DefaultListModel<Engine> subscribedEnginesModel = new DefaultListModel<>();
    DefaultListModel<Tank> tanksModel = new DefaultListModel<>();
    DefaultListModel<Engine> thrustersModel = new DefaultListModel<>();

    // Separate lists for XML elements
    for (Object obj : DS.cfg.getPropulsion().getDocumentationOrPropertyOrFunction()) 
    {
        if (obj instanceof Engine) 
        {
            Engine eng = (Engine) obj;
            enginesModel.addElement(eng);
        } 
        else if (obj instanceof Tank) 
        {
            Tank t = (Tank) obj;
            tanksModel.addElement(t);
        }
    }



    // Engines Tab
    JLabel lblEng = new JLabel("Available Engines:");
    lblEng.setBounds(10, 10, 200, 20);
    panel.add(lblEng);

    JList<Engine> enginesList = new JList<>(enginesModel);
    enginesList.setCellRenderer(new DefaultListCellRenderer() 
    {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
        {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Engine) 
            {
                setText(((Engine) value).getName() + " (" + ((Engine) value).getFile() + ")");
            }
            return this;
        }
    });

    JScrollPane scrollEng = new JScrollPane(enginesList);
    scrollEng.setBounds(10, 35, 300, 150);
    panel.add(scrollEng);



    // Thrusters Tab
    JLabel lblThr = new JLabel("Available Thrusters:");
    lblThr.setBounds(330, 10, 200, 20);
    panel.add(lblThr);

    JList<Engine> thrustersList = new JList<>(enginesModel); // Filter later if needed
    thrustersList.setCellRenderer(new DefaultListCellRenderer() 
    {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
        {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Engine) 
            {
                setText(((Engine) value).getName() + " (" + ((Engine) value).getFile() + ")");
            }
            return this;
        }
    });

    JScrollPane scrollThr = new JScrollPane(thrustersList);
    scrollThr.setBounds(330, 35, 300, 150);
    panel.add(scrollThr);



    // Subscribed Engine(s) Tab
    JLabel lblSub = new JLabel("Subscribed Engine(s)(*):");
    lblSub.setBounds(10, 200, 300, 20);
    panel.add(lblSub);

    JList<Engine> subList = new JList<>(subscribedEnginesModel);
    subList.setCellRenderer(new DefaultListCellRenderer() 
    {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
        {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Engine) 
            {
                setText(((Engine) value).getName() + " (" + ((Engine) value).getFile() + ")");
            }
            return this;
        }
    });

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

    JList<Tank> tanksList = new JList<>(tanksModel);
    tanksList.setCellRenderer(new DefaultListCellRenderer() 
    {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
        {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Tank) 
            {
                setText(((Tank) value).getName());
            }
            return this;
        }
    });

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

    //Pair Buttons Functionality
    btnNewPair.addActionListener(e -> 
    {
        Engine selectedEngine = enginesList.getSelectedValue();
        Engine selectedThruster = thrustersList.getSelectedValue();
        if (selectedEngine != null && selectedThruster != null) 
        {
            subscribedEnginesModel.addElement(selectedEngine);
            System.out.println("New pair added: " + selectedEngine.getName() + " + " + selectedThruster.getName());
        } else 
        {
            JOptionPane.showMessageDialog(panel, "Select both an engine and a thruster to create a pair.");
        }
    });

    btnDelPair.addActionListener(e -> 
    {
        Engine selected = subList.getSelectedValue();
        if (selected != null) 
        {
            subscribedEnginesModel.removeElement(selected);
            System.out.println("Pair deleted: " + selected.getName());
        }
    });

    btnDetailPair.addActionListener(e -> 
    {
        Engine selected = subList.getSelectedValue();
        if (selected != null) 
        {
            JOptionPane.showMessageDialog(panel, "Details for: " + selected.getName());
        }
    });

    // Tank Buttons Functionality
    btnNewTank.addActionListener(e -> 
    {
        String name = JOptionPane.showInputDialog(panel, "Enter Tank Name:");
        if (name != null && !name.isEmpty()) 
        {
            Tank newTank = new Tank();
            newTank.setName(name);
            tanksModel.addElement(newTank);
            System.out.println("New tank added: " + name);
        }
    });

    btnDelTank.addActionListener(e -> 
    {
        Tank selected = tanksList.getSelectedValue();
        if (selected != null) 
        {
            tanksModel.removeElement(selected);
            System.out.println("Tank deleted: " + selected.getName());
        }
    });

    btnDetailTank.addActionListener(e -> 
    {
        Tank selected = tanksList.getSelectedValue();
        if (selected != null) 
        {
            JOptionPane.showMessageDialog(panel, "Details for tank: " + selected.getName());
        }
    });

    panel.revalidate();
    panel.repaint();
}



}





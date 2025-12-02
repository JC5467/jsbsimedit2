package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

import generated.BuoyantForces;
import generated.GasCell;
import generated.Ballonet;
import generated.Balloon;
import generated.Location;
import generated.Pressure;
import generated.Valve;

public class BuoyantForcesTab extends simpleTab {

    private DefaultListModel<String> gasCellModel;
    private DefaultListModel<String> ballonetModel;

    private JList<String> gasCellList;
    private JList<String> ballonetList;
    private JTextArea detailsArea;

    public BuoyantForcesTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        TF = tf;
        panel.add(new JLabel("No aircraft file read.", SwingConstants.CENTER), BorderLayout.CENTER);
    }

    public void loadData() {
        panel.removeAll();
        panel.setLayout(new BorderLayout());

        BuoyantForces bf = DS.cfg.getBuoyantForces();
        if (bf == null) {
            panel.add(new JLabel("No <buoyant_forces> section found in this aircraft file.", SwingConstants.CENTER), BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();
            return;
        }

        JPanel main = new JPanel(null);

        gasCellModel = new DefaultListModel<>();
        ballonetModel = new DefaultListModel<>();
        gasCellList = new JList<>(gasCellModel);
        ballonetList = new JList<>(ballonetModel);
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);

        JLabel title = new JLabel("Buoyant Forces");
        title.setBounds(10, 10, 300, 20);
        main.add(title);

        JLabel gasLabel = new JLabel("Gas Cells:");
        gasLabel.setBounds(10, 40, 300, 20);
        main.add(gasLabel);

        JScrollPane gasScroll = new JScrollPane(gasCellList);
        gasScroll.setBounds(10, 65, 250, 190);
        main.add(gasScroll);

        JButton addGas = new JButton("Add Gas Cell");
        addGas.setBounds(10, 260, 120, 25);
        main.add(addGas);

        JButton delGas = new JButton("Delete Gas Cell");
        delGas.setBounds(140, 260, 120, 25);
        main.add(delGas);

        JButton editGas = new JButton("Edit Gas Cell");
        editGas.setBounds(70, 290, 120, 25);
        main.add(editGas);

        JLabel balLabel = new JLabel("Ballonets (selected gas cell):");
        balLabel.setBounds(300, 40, 300, 20);
        main.add(balLabel);

        JScrollPane balScroll = new JScrollPane(ballonetList);
        balScroll.setBounds(300, 65, 250, 190);
        main.add(balScroll);

        JButton addBal = new JButton("Add Ballonet");
        addBal.setBounds(300, 260, 120, 25);
        main.add(addBal);

        JButton delBal = new JButton("Delete Ballonet");
        delBal.setBounds(430, 260, 120, 25);
        main.add(delBal);

        JButton editBal = new JButton("Edit Ballonet");
        editBal.setBounds(360, 290, 120, 25);
        main.add(editBal);

        JLabel detLabel = new JLabel("Details / Properties / Buoyancy:");
        detLabel.setBounds(10, 330, 350, 20);
        main.add(detLabel);

        JScrollPane detScroll = new JScrollPane(detailsArea);
        detScroll.setBounds(10, 355, 540, 180);
        main.add(detScroll);

        gasCellList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadBallonets();
                showGasCellDetails();
            }
        });

        ballonetList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showBallonetDetails();
            }
        });

        addGas.addActionListener(e -> addGasCell());
        delGas.addActionListener(e -> deleteGasCell());
        editGas.addActionListener(e -> editGasCell());
        addBal.addActionListener(e -> addBallonet());
        delBal.addActionListener(e -> deleteBallonet());
        editBal.addActionListener(e -> editBallonet());

        panel.add(main, BorderLayout.CENTER);

        gasCellModel.clear();
        ballonetModel.clear();
        detailsArea.setText("");

        if (bf.getGasCell().isEmpty()) {
            gasCellModel.addElement("No gas cells in XML");
        } else {
            int i = 1;
            for (GasCell cell : bf.getGasCell()) {
                gasCellModel.addElement("Gas Cell " + i + " (type=" + cell.getType() + ")");
                i++;
            }
            gasCellList.setSelectedIndex(0);
        }

        panel.revalidate();
        panel.repaint();
    }

    private GasCell getSelectedGas() {
        if (gasCellList == null) return null;
        int i = gasCellList.getSelectedIndex();
        if (i < 0) return null;
        BuoyantForces bf = DS.cfg.getBuoyantForces();
        if (bf == null || bf.getGasCell().size() <= i) return null;
        return bf.getGasCell().get(i);
    }

    private Ballonet getSelectedBal() {
        GasCell g = getSelectedGas();
        if (g == null || ballonetList == null) return null;
        int i = ballonetList.getSelectedIndex();
        if (i < 0 || i >= g.getBallonet().size()) return null;
        return g.getBallonet().get(i);
    }

    private void loadBallonets() {
        if (ballonetModel == null) return;
        ballonetModel.clear();

        GasCell g = getSelectedGas();
        if (g == null) return;

        int i = 1;
        for (Ballonet b : g.getBallonet()) {
            ballonetModel.addElement("Ballonet " + i + " (fullness=" + b.getFullness() + ")");
            i++;
        }
    }

    private void showGasCellDetails() {
        GasCell g = getSelectedGas();
        if (g == null || detailsArea == null) return;
        detailsArea.setText(makeDetails("Gas Cell", g));
    }

    private void showBallonetDetails() {
        Ballonet b = getSelectedBal();
        if (b == null) {
            showGasCellDetails();
            return;
        }
        if (detailsArea == null) return;
        detailsArea.setText(makeDetails("Ballonet", b));
    }

    private String makeDetails(String label, Balloon b) {
        StringBuilder sb = new StringBuilder();

        sb.append(label).append("\n-----------------\n");
        sb.append("Type: ").append(b.getType()).append("\n");
        sb.append("Fullness: ").append(b.getFullness()).append("\n");

        if (b.getLocation() != null) {
            sb.append("Location: (")
              .append(b.getLocation().getX()).append(", ")
              .append(b.getLocation().getY()).append(", ")
              .append(b.getLocation().getZ()).append(") ")
              .append(b.getLocation().getUnit()).append("\n");
        }

        if (b.getXRadius() != null)
            sb.append("x_radius: ").append(b.getXRadius().getValue()).append(" ").append(b.getXRadius().getUnit()).append("\n");

        if (b.getYRadius() != null)
            sb.append("y_radius: ").append(b.getYRadius().getValue()).append(" ").append(b.getYRadius().getUnit()).append("\n");

        if (b.getZRadius() != null)
            sb.append("z_radius: ").append(b.getZRadius().getValue()).append(" ").append(b.getZRadius().getUnit()).append("\n");

        double xr = (b.getXRadius() != null) ? b.getXRadius().getValue() : 0;
        double yr = (b.getYRadius() != null) ? b.getYRadius().getValue() : 0;
        double zr = (b.getZRadius() != null) ? b.getZRadius().getValue() : 0;

        if (xr > 0 && yr > 0 && zr > 0) {
            double volume = 4.0 / 3.0 * Math.PI * xr * yr * zr;
            double buoyancy = volume * b.getFullness();
            sb.append("Approx volume: ").append(volume).append("\n");
            sb.append("Buoyancy (volume * fullness): ").append(buoyancy).append("\n");
        } else {
            sb.append("Buoyancy: N/A\n");
        }

        return sb.toString();
    }

    private void addGasCell() {
        BuoyantForces bf = DS.cfg.getBuoyantForces();
        if (bf == null) return;

        GasCell g = new GasCell();

        Location loc = new Location();
        loc.setX(0);
        loc.setY(0);
        loc.setZ(0);
        g.setLocation(loc);

        g.setType("HELIUM");
        g.setFullness(1.0);

        Pressure p = new Pressure();
        p.setValue(0.0);
        g.setMaxOverpressure(p);

        Valve v = new Valve();
        v.setValue(0.1);
        g.setValveCoefficient(v);

        g.setHeat(new Balloon.Heat());

        bf.getGasCell().add(g);
        DS.setDirty();
        loadData();
    }

    private void deleteGasCell() {
        GasCell g = getSelectedGas();
        BuoyantForces bf = DS.cfg.getBuoyantForces();
        if (g == null || bf == null) return;

        bf.getGasCell().remove(g);
        DS.setDirty();
        loadData();
    }

    private void editGasCell() {
        GasCell g = getSelectedGas();
        if (g == null) return;

        JTextField tf = new JTextField(Double.toString(g.getFullness()));

        JPanel p = new JPanel(new GridLayout(0, 2));
        p.add(new JLabel("Fullness:"));
        p.add(tf);

        if (JOptionPane.showConfirmDialog(panel, p, "Edit Gas Cell", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            g.setFullness(Double.parseDouble(tf.getText()));
            DS.setDirty();
        }
        showGasCellDetails();
    }

    private void addBallonet() {
        GasCell g = getSelectedGas();
        if (g == null) return;

        Ballonet b = new Ballonet();

        Location loc = new Location();
        loc.setX(0);
        loc.setY(0);
        loc.setZ(0);
        b.setLocation(loc);

        b.setType("AIR");
        b.setFullness(0.5);

        Pressure p = new Pressure();
        p.setValue(0.0);
        b.setMaxOverpressure(p);

        Valve v = new Valve();
        v.setValue(0.1);
        b.setValveCoefficient(v);

        b.setHeat(new Balloon.Heat());

        g.getBallonet().add(b);
        DS.setDirty();

        loadBallonets();
    }

    private void deleteBallonet() {
        Ballonet b = getSelectedBal();
        GasCell g = getSelectedGas();
        if (g == null || b == null) return;

        g.getBallonet().remove(b);
        DS.setDirty();
        loadBallonets();
    }

    private void editBallonet() {
        Ballonet b = getSelectedBal();
        if (b == null) return;

        JTextField tf = new JTextField(Double.toString(b.getFullness()));

        JPanel p = new JPanel(new GridLayout(0, 2));
        p.add(new JLabel("Fullness:"));
        p.add(tf);

        if (JOptionPane.showConfirmDialog(panel, p, "Edit Ballonet", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            b.setFullness(Double.parseDouble(tf.getText()));
            DS.setDirty();
        }
        showBallonetDetails();
    }
}

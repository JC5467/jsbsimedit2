package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import generated.Aerodynamics;
import generated.Axis;
import generated.Function;
import generated.Property;
import generated.Table;
import generated.UnnamedTable;
import generated.MultipleArguments;

import uta.cse3310.dataStore;
import uta.cse3310.tab.simpleTab;
import uta.cse3310.tabFrame;

import jakarta.xml.bind.JAXBElement;

public class AerodynamicsTab extends simpleTab {

    private JTabbedPane axisTabs;

    public AerodynamicsTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        TF = tf;
        panel.add(new JLabel("No aircraft file read.", SwingConstants.CENTER), BorderLayout.CENTER);
    }

    @Override
    public void loadData() {

        panel.removeAll();

        Aerodynamics aero = DS.cfg.getAerodynamics();
        if (aero == null) {
            panel.add(new JLabel("No <aerodynamics> section found.", SwingConstants.CENTER));
            panel.revalidate();
            panel.repaint();
            return;
        }

        axisTabs = new JTabbedPane();
        axisTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        List<Axis> axisList = aero.getAxis();
        if (axisList == null || axisList.isEmpty()) {
            panel.add(new JLabel("No <axis> entries found.", SwingConstants.CENTER));
            panel.revalidate();
            panel.repaint();
            return;
        }

        for (Axis a : axisList) {
            JPanel axisPanel = createAxisPanel(a);
            String tabName = (a.getName() == null || a.getName().isEmpty())
                    ? "Axis"
                    : a.getName();
            axisTabs.addTab(tabName, axisPanel);
        }

        panel.add(axisTabs, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    /** Build UI for one <axis> section */
    private JPanel createAxisPanel(Axis axis) {

        JPanel container = new JPanel(new BorderLayout());
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel title = new JLabel(axis.getName() + " Axis");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(8));

        for (Object obj : axis.getDocumentationOrFunction()) {

            if (obj instanceof String s) {
                JLabel doc = new JLabel("Note: " + s);
                doc.setFont(new Font("Arial", Font.ITALIC, 11));
                doc.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(doc);
                content.add(Box.createVerticalStrut(5));
            } else if (obj instanceof Axis.Function f) {
                content.add(createFunctionPanel(f));
                content.add(Box.createVerticalStrut(12));
            }
        }

        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    /** Build UI for a <function> block */
    private JPanel createFunctionPanel(Axis.Function f) {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(
                f.getName() == null ? "Function" : f.getName()));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        // DESCRIPTION 
        if (f.getDescription() != null) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel("Description: "));
            JTextField descField = new JTextField(f.getDescription(), 30);

            descField.getDocument().addDocumentListener(new SimpleDocListener() {
                @Override
                public void changedUpdate(DocumentEvent e) {
                    f.setDescription(descField.getText());
                    DS.setDirty();
                }
            });

            row.add(descField);
            p.add(row);
        }

        // DIRECT VALUE 
        if (f.getValue() != null) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel("Value: "));
            JTextField txt = new JTextField(f.getValue().toString(), 8);

            // update on ENTER
            txt.addActionListener(e -> updateFunctionValue(f, txt));

            // also update on focus lost
            txt.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    updateFunctionValue(f, txt);
                }
            });

            row.add(txt);
            p.add(row);
        }

        // TABLE (top level)
        if (f.getTable() != null) {
            p.add(new JLabel("Table Data:"));

            JTable table = createEditableTable(f.getTable());
            JScrollPane sp = new JScrollPane(table);
            sp.setPreferredSize(new Dimension(450, 120));
            p.add(sp);
        }

        // PRODUCT (properties, constants, nested tables)
        if (f.getProduct() != null) {
            JLabel prodLabel = new JLabel("Product terms:");
            prodLabel.setFont(new Font("Arial", Font.BOLD, 11));
            p.add(prodLabel);

            MultipleArguments product = f.getProduct();

            int index = 0;
            for (JAXBElement<?> elem : product.getFuncGroup()) {

                Object obj = elem.getValue();

                // PROPERTY term
                if (obj instanceof Property prop) {
                    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    row.add(new JLabel("Property: "));
                    JTextField propField = new JTextField(prop.getValue(), 25);

                    propField.getDocument().addDocumentListener(new SimpleDocListener() {
                        @Override
                        public void changedUpdate(DocumentEvent e) {
                            prop.setValue(propField.getText());
                            DS.setDirty();
                        }
                    });

                    row.add(propField);
                    p.add(row);
                }

                // CONSTANT DOUBLE term
                else if (obj instanceof Double d) {
                    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    row.add(new JLabel("Constant: "));
                    JTextField constField = new JTextField(d.toString(), 10);

                    final int constIndex = index;

                    constField.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            try {
                                double v = Double.parseDouble(constField.getText());
                                @SuppressWarnings("unchecked")
                                JAXBElement<Double> doubleElem =
                                        (JAXBElement<Double>) product.getFuncGroup().get(constIndex);
                                doubleElem.setValue(v);
                                DS.setDirty();
                            } catch (NumberFormatException ex) {
                                constField.setText(d.toString());
                                JOptionPane.showMessageDialog(panel,
                                        "Invalid numeric constant in product term.");
                            }
                        }
                    });

                    row.add(constField);
                    p.add(row);
                }

                // NESTED TABLE
                else if (obj instanceof Table t) {
                    JLabel nested = new JLabel("Nested Table:");
                    nested.setFont(new Font("Arial", Font.ITALIC, 11));
                    p.add(nested);

                    JTable table = createEditableTable(t);
                    JScrollPane sp = new JScrollPane(table);
                    sp.setPreferredSize(new Dimension(450, 100));
                    p.add(sp);
                }

                // Nested product 
                else if (obj instanceof MultipleArguments m) {
                    JLabel nested = new JLabel("Nested Product (not expanded)");
                    nested.setFont(new Font("Arial", Font.ITALIC, 11));
                    p.add(nested);
                }

                index++;
            }
        }

        // APPLY_AT_CG
        if (f.isApplyAtCg() != null) {
            JCheckBox cg = new JCheckBox("Apply at CG", f.isApplyAtCg());
            cg.addActionListener(e -> {
                f.setApplyAtCg(cg.isSelected());
                DS.setDirty();
            });
            p.add(cg);
        }

        return p;
    }

    //update function <value> safely
    private void updateFunctionValue(Function f, JTextField txt) {
        if (f.getValue() == null) return;

        try {
            double v = Double.parseDouble(txt.getText());
            f.setValue(v);
            DS.setDirty();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel, "Invalid number.");
            txt.setText(f.getValue().toString());
        }
    }

    
     //Editable JTable for a Table
     
    private JTable createEditableTable(Table table) {

        List<UnnamedTable.TableData> rows = table.getTableData();

        int maxCols = 0;
        for (UnnamedTable.TableData td : rows) {
            int size = td.getValue().size();   // getValue() never null
            if (size > maxCols) {
                maxCols = size;
            }
        }
        if (maxCols == 0) {
            maxCols = 1;
        }
        final int finalMaxCols = maxCols;

        AbstractTableModel model = new AbstractTableModel() {

            @Override
            public int getRowCount() {
                return rows.size();
            }

            @Override
            public int getColumnCount() {
                return finalMaxCols;
            }

            @Override
            public String getColumnName(int col) {
                return "v" + col;
            }

            @Override
            public Object getValueAt(int rowIndex, int colIndex) {
                List<Double> vals = rows.get(rowIndex).getValue();
                if (colIndex >= vals.size()) {
                    return "";
                }
                return vals.get(colIndex);
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return true;
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int colIndex) {

                List<Double> vals = rows.get(rowIndex).getValue();

                String text = (aValue == null) ? "" : aValue.toString().trim();
                if (text.isEmpty()) {
                    return;
                }

                try {
                    double v = Double.parseDouble(text);

                    // Expand list if needed
                    while (vals.size() <= colIndex) {
                        vals.add(0.0);
                    }

                    vals.set(colIndex, v);
                    DS.setDirty();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel,
                            "Invalid numeric value in table",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        JTable jt = new JTable(model);
        jt.setFillsViewportHeight(true);
        jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // allow horizontal scroll for many columns
        return jt;
    }

    /** Simple document-listener adapter */
    private abstract static class SimpleDocListener implements DocumentListener {
        public abstract void changedUpdate(DocumentEvent e);

        @Override
        public void insertUpdate(DocumentEvent e) { changedUpdate(e); }

        @Override
        public void removeUpdate(DocumentEvent e) { changedUpdate(e); }
    }
}

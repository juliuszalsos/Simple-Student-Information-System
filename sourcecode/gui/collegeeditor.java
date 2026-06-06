package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;

public class collegeeditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
    private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    private JButton editButton = new JButton("...");
    private JTable parentTable;

    public collegeeditor(JTable table, DefaultTableModel model) {
        this.parentTable = table;
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        editButton.setPreferredSize(new Dimension(30, 30));
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setFocusPainted(false);
        panel.add(editButton);

        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteCollege = new JMenuItem("Delete College");
        JMenuItem updateCollege = new JMenuItem("Update Information");

        // --- UPDATE COLLEGE ---
        updateCollege.addActionListener(e -> {
            int viewrow = parentTable.getSelectedRow();
            if (viewrow != -1) {
                int row = parentTable.convertRowIndexToModel(viewrow);
                String oldCCode = model.getValueAt(row, 0).toString().trim().toUpperCase();
                String oldCName = model.getValueAt(row, 1).toString().trim();

                JTextField f1 = new JTextField(oldCCode);
                JTextField f2 = new JTextField(oldCName);
                Object[] message = {"College Code:", f1, "College Name:", f2};

                int option = JOptionPane.showConfirmDialog(null, message, "Update College", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String newCCode = f1.getText().trim().toUpperCase();
                    String newCName = f2.getText().trim();

                    if (newCCode.isEmpty() || newCName.isEmpty()) return;

                    if (!newCCode.equals(oldCCode) && doesCollegeCodeExist(newCCode)) {
                        JOptionPane.showMessageDialog(null, "College code already exists", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    fireEditingStopped();
                    model.setValueAt(newCCode, row, 0);
                    model.setValueAt(newCName, row, 1);
                    
                    updateCollegeCSV(model);
                    cascadeUpdateProgramsCSV(oldCCode, newCCode);
                    
                    refreshOtherTabs();

                    JOptionPane.showMessageDialog(null, "College and dependent programs updated successfully.");
                }
            }
        });

        // --- DELETE COLLEGE ---
        deleteCollege.addActionListener(e -> {
            int viewrow = parentTable.getSelectedRow();
            if (viewrow != -1) {
                int row = parentTable.convertRowIndexToModel(viewrow);
                
                int confirm = JOptionPane.showConfirmDialog(null, 
                    "Delete this College? Related programs will show NULL unless this college is added back.", 
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    fireEditingStopped();
                    model.removeRow(row);
                    updateCollegeCSV(model);
                    
                    refreshOtherTabs();

                    JOptionPane.showMessageDialog(null, "College removed successfully.");
                }
            }
        });

        menu.add(updateCollege);
        menu.add(deleteCollege);
        editButton.addActionListener(e -> menu.show(editButton, editButton.getWidth() / 2, editButton.getHeight() / 2));
    }

    private boolean doesCollegeCodeExist(String code) {
        File file = new File("sourcecode/csvfiles/College.csv");
        if (!file.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length > 0 && data[0].trim().toUpperCase().equals(code)) return true;
            }
        } catch (IOException e) { e.printStackTrace(); }
        return false;
    }

    private void refreshOtherTabs() {
        Window topWindow = SwingUtilities.getWindowAncestor(parentTable);
        if (topWindow instanceof JFrame) {
            JFrame mainFrame = (JFrame) topWindow;
            refreshChildPanels(mainFrame.getContentPane());
        }
    }

    private void refreshChildPanels(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof programinterface) {
                ((programinterface) comp).loadData();
            } else if (comp instanceof studentinterface) {
                ((studentinterface) comp).loadData();
            } else if (comp instanceof Container) {
                refreshChildPanels((Container) comp);
            }
        }
    }

    private void cascadeUpdateProgramsCSV(String oldCCode, String newCCode) {
        if (oldCCode.equalsIgnoreCase(newCCode)) return;
        File inputFile = new File("sourcecode/csvfiles/Program.csv");
        File tempFile = new File("sourcecode/csvfiles/Program_temp.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             PrintWriter pw = new PrintWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length >= 3 && data[2].trim().toUpperCase().equals(oldCCode)) {
                    data[2] = newCCode;
                }
                pw.println(String.join(",", data));
            }
        } catch (IOException e) { e.printStackTrace(); }

        if (inputFile.exists()) inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    private void updateCollegeCSV(DefaultTableModel model) {
        File file = new File("sourcecode/csvfiles/College.csv");
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                out.println(model.getValueAt(i, 0).toString().trim().toUpperCase() + "," + 
                            model.getValueAt(i, 1).toString().trim());
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) { return panel; }
    @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) { return panel; }
    @Override public Object getCellEditorValue() { return ""; }
}
package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;

public class programeditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
    private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    private JButton editButton = new JButton("...");
    private JTable parentTable;

    public programeditor(JTable table, DefaultTableModel model) {
        this.parentTable = table;
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        editButton.setPreferredSize(new Dimension(30, 30));
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setFocusPainted(false);
        panel.add(editButton);

        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteProgram = new JMenuItem("Delete Program");
        JMenuItem updateProgram = new JMenuItem("Update Information");

        // --- UPDATE PROGRAM CODE & CASCADE TO STUDENTS ---
        updateProgram.addActionListener(e -> {
            int viewrow = parentTable.getSelectedRow();
            if (viewrow != -1) {
                int row = parentTable.convertRowIndexToModel(viewrow);
                String oldPCode = model.getValueAt(row, 0).toString().trim().toUpperCase();
                String oldPName = model.getValueAt(row, 1).toString().trim();
                String currentCCode = model.getValueAt(row, 2).toString().trim().toUpperCase();

                JTextField f1 = new JTextField(oldPCode);
                JTextField f2 = new JTextField(oldPName);
                JTextField f3 = new JTextField(currentCCode);
                Object[] message = {"Program Code:", f1, "Program Name:", f2, "College Code:", f3};

                int option = JOptionPane.showConfirmDialog(null, message, "Update Program", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String newPCode = f1.getText().trim().toUpperCase();
                    String newPName = f2.getText().trim();
                    String newCCode = f3.getText().trim().toUpperCase();

                    if (newPCode.isEmpty() || newPName.isEmpty() || newCCode.isEmpty()) return;

                    if (!newPCode.equals(oldPCode) && doesProgramCodeExist(newPCode)) {
                        JOptionPane.showMessageDialog(null, "Program code already exists", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    fireEditingStopped();
                    model.setValueAt(newPCode, row, 0);
                    model.setValueAt(newPName, row, 1);
                    model.setValueAt(newCCode, row, 2);
                    
                    updateProgramCSV(model);
                    cascadeUpdateStudentsCSV(oldPCode, newPCode);
                    
                    refreshOtherTabs();

                    JOptionPane.showMessageDialog(null, "Program updated and cascaded to students successfully.");
                }
            }
        });

        // --- DELETE PROGRAM ---
        deleteProgram.addActionListener(e -> {
            int viewrow = parentTable.getSelectedRow();
            if (viewrow != -1) {
                int row = parentTable.convertRowIndexToModel(viewrow);
                String targetPCode = model.getValueAt(row, 0).toString().trim().toUpperCase();
                
                int confirm = JOptionPane.showConfirmDialog(null, 
                    "Delete this Program? Students under it will permanently be marked as NOT ENROLLED.", 
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    fireEditingStopped();
                    model.removeRow(row);
                    updateProgramCSV(model);
                    
                    // Permanently overwrite student data references to NOT ENROLLED
                    cascadeNullifyStudentsCSV(targetPCode);
                    
                    refreshOtherTabs();
                    
                    JOptionPane.showMessageDialog(null, "Program removed. Dependent student records permanently set to NOT ENROLLED.");
                }
            }
        });

        menu.add(updateProgram);
        menu.add(deleteProgram);
        editButton.addActionListener(e -> menu.show(editButton, editButton.getWidth() / 2, editButton.getHeight() / 2));
    }

    private boolean doesProgramCodeExist(String code) {
        File file = new File("sourcecode/csvfiles/Program.csv");
        if (!file.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length > 0 && data[0].trim().toUpperCase().equals(code)) return true;
            }
        }  catch (IOException e) { e.printStackTrace(); }
        return false;
    }

    private void cascadeUpdateStudentsCSV(String oldPCode, String newPCode) {
        if (oldPCode.equalsIgnoreCase(newPCode)) return;
        File inputFile = new File("sourcecode/csvfiles/Student.csv");
        File tempFile = new File("sourcecode/csvfiles/Student_temp.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             PrintWriter pw = new PrintWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length >= 4 && data[3].trim().toUpperCase().equals(oldPCode)) {
                    data[3] = newPCode;
                }
                pw.println(String.join(",", data));
            }
        } catch (IOException e) { e.printStackTrace(); }

        if (inputFile.exists()) inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    // NEW METHOD: Permanently converts student programs inside Student.csv to NOT ENROLLED
    private void cascadeNullifyStudentsCSV(String targetPCode) {
        File inputFile = new File("sourcecode/csvfiles/Student.csv");
        if (!inputFile.exists()) return;
        File tempFile = new File("sourcecode/csvfiles/Student_temp.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             PrintWriter pw = new PrintWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length >= 4 && data[3].trim().toUpperCase().equals(targetPCode)) {
                    data[3] = "NOT ENROLLED"; // Disconnect records permanently 
                }
                pw.println(String.join(",", data));
            }
        } catch (IOException e) { e.printStackTrace(); }

        if (inputFile.exists()) inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    private void updateProgramCSV(DefaultTableModel model) {
        File file = new File("sourcecode/csvfiles/Program.csv");
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                out.println(model.getValueAt(i, 0).toString().trim().toUpperCase() + "," + 
                            model.getValueAt(i, 1).toString().trim() + "," +
                            model.getValueAt(i, 2).toString().trim().toUpperCase());
            }
        } catch (IOException ex) { ex.printStackTrace(); }
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
            if (comp instanceof studentinterface) {
                ((studentinterface) comp).loadData();
            } else if (comp instanceof programinterface) {
                ((programinterface) comp).loadData();
            } else if (comp instanceof Container) {
                refreshChildPanels((Container) comp);
            }
        }
    }

    @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) { return panel; }
    @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) { return panel; }
    @Override public Object getCellEditorValue() { return ""; }
}
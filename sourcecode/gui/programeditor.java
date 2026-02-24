package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;

public class programeditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
    private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    private JButton editButton = new JButton("...");

    public programeditor(JTable table, DefaultTableModel model) {
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        editButton.setPreferredSize(new Dimension(30, 30));
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setFocusPainted(false);
        editButton.setMargin(new Insets(0, 0, 0, 0));
        panel.add(editButton);

        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteProgram = new JMenuItem("Delete Program");
        JMenuItem updateProgram = new JMenuItem("Update Information");

        updateProgram.addActionListener(e -> {
            int viewrow = table.getSelectedRow();
            if (viewrow != -1) {
                int row = table.convertRowIndexToModel(viewrow);
                String oldPCode = model.getValueAt(row, 0).toString();
                String pname = model.getValueAt(row, 1).toString();
                String ccode = model.getValueAt(row, 2).toString();

                JTextField f1 = new JTextField(oldPCode);
                JTextField f2 = new JTextField(pname);
                JTextField f3 = new JTextField(ccode); 

                Object[] message = {
                    "Program Code:", f1,
                    "Program Name:", f2,
                    "College Code:", f3
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Update Program", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String updatedPCode = f1.getText().trim().toUpperCase();
                    String updatedPName = f2.getText().trim();
                    String updatedCCode = f3.getText().trim().toUpperCase();

                    if (updatedPCode.isEmpty() || updatedPName.isEmpty() || updatedCCode.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Fields cannot be empty!");
                        return;
                    }

                    if (!isCollegeValid(updatedCCode)) {
                        JOptionPane.showMessageDialog(null, "College Code '" + updatedCCode + "' does not exist!");
                        return;
                    }

                    model.setValueAt(updatedPCode, row, 0);
                    model.setValueAt(updatedPName, row, 1);
                    model.setValueAt(updatedCCode, row, 2);
                    
                    updateProgramInfo(model);

                    if (!oldPCode.equalsIgnoreCase(updatedPCode)) {
                        cascadeUpdateStudents(oldPCode, updatedPCode);
                    }
                    
                    JOptionPane.showMessageDialog(null, "Program Updated Successfully");
                }
            }
        });

        deleteProgram.addActionListener(e -> {
            int viewrow = table.getSelectedRow();
            if (viewrow != -1) {
                int row = table.convertRowIndexToModel(viewrow);    
                int confirm = JOptionPane.showConfirmDialog(null, 
                    "Are you sure? This program will be removed.\n" +
                    "Related students will be hidden from the list.", 
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    model.removeRow(row);
                    updateProgramInfo(model); 
                    JOptionPane.showMessageDialog(null, "Program Removed. Related students are now hidden.");
                }
            }
        });

        menu.add(updateProgram);
        menu.add(deleteProgram);

        editButton.addActionListener(e -> {
            menu.show(editButton, editButton.getWidth() / 2, editButton.getHeight() / 2);
        });
    }

    private void cascadeUpdateStudents(String oldPCode, String newPCode) {
        File inputFile = new File("sourcecode/csvfiles/Student.csv");
        File tempFile = new File("sourcecode/csvfiles/Student_temp.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             PrintWriter pw = new PrintWriter(new FileWriter(tempFile))) {
            
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 3 && data[3].equalsIgnoreCase(oldPCode)) {
                    data[3] = newPCode;
                    pw.println(String.join(",", data));
                } else {
                    pw.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (inputFile.delete()) {
            tempFile.renameTo(inputFile);
        }
    }

    private boolean isCollegeValid(String collegeCode) {
        try (BufferedReader br = new BufferedReader(new FileReader("sourcecode/csvfiles/College.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 0 && data[0].equalsIgnoreCase(collegeCode)) return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    private void updateProgramInfo(DefaultTableModel model) {
        try (PrintWriter out = new PrintWriter(new FileWriter("sourcecode/csvfiles/Program.csv"))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                out.println(model.getValueAt(i, 0).toString() + "," + 
                            model.getValueAt(i, 1).toString() + "," + 
                            model.getValueAt(i, 2).toString());
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error updating CSV: " + ex.getMessage());
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return panel;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        panel.setBackground(table.getSelectionBackground());
        return panel;
    }

    @Override
    public Object getCellEditorValue() { return ""; }
}

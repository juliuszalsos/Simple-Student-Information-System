package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;

public class collegeeditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
    private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    private JButton editButton = new JButton("...");

    public collegeeditor(JTable table, DefaultTableModel model) {
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        editButton.setPreferredSize(new Dimension(30, 30));
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setFocusPainted(false);
        editButton.setMargin(new Insets(0, 0, 0, 0));
        panel.add(editButton);

        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteCollege = new JMenuItem("Delete College");
        JMenuItem updateCollege = new JMenuItem("Update Information");

        updateCollege.addActionListener(e -> {
            int viewrow = table.getSelectedRow();
            if (viewrow != -1) {
                int row = table.convertRowIndexToModel(viewrow);
                String oldCCode = model.getValueAt(row, 0).toString();
                String oldCName = model.getValueAt(row, 1).toString();

                JTextField f1 = new JTextField(oldCCode);
                JTextField f2 = new JTextField(oldCName);

                Object[] message = {
                    "College Code:", f1,
                    "College Name:", f2
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Update College", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String newCCode = f1.getText().trim().toUpperCase();
                    String newCName = f2.getText().trim();

                    if (newCCode.isEmpty() || newCName.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Fields cannot be empty!");
                        return;
                    }

                    model.setValueAt(newCCode, row, 0);
                    model.setValueAt(newCName, row, 1);
                    
                    updateCollegeInfo(model);

                    cascadeUpdatePrograms(oldCCode, newCCode);
                    
                    JOptionPane.showMessageDialog(null, "College updated successfully.");
                }
            }
        });

        deleteCollege.addActionListener(e -> {
            int viewrow = table.getSelectedRow();
            if (viewrow != -1) {
                int row = table.convertRowIndexToModel(viewrow);
                
                int confirm = JOptionPane.showConfirmDialog(null, 
                    "Are you sure? Related programs and students will be hidden\n" +
                    "from their lists, but their data will not be deleted.", 
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    model.removeRow(row);
                    updateCollegeInfo(model); 
                    JOptionPane.showMessageDialog(null, "College removed. Related data is now hidden.");
                }
            }
        });

        menu.add(updateCollege);
        menu.add(deleteCollege);

        editButton.addActionListener(e -> {
            menu.show(editButton, editButton.getWidth() / 2, editButton.getHeight() / 2);
        });
    }

       private void cascadeUpdatePrograms(String oldCCode, String newCCode) {
        if (oldCCode.equalsIgnoreCase(newCCode)) return;

        File inputFile = new File("sourcecode/csvfiles/Program.csv");
        File tempFile = new File("sourcecode/csvfiles/Program_temp.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             PrintWriter pw = new PrintWriter(new FileWriter(tempFile))) {
            
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 2 && data[2].trim().equalsIgnoreCase(oldCCode)) {
                    data[2] = newCCode;
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

    private void updateCollegeInfo(DefaultTableModel model) {
        try (PrintWriter out = new PrintWriter(new FileWriter("sourcecode/csvfiles/College.csv"))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                String rowString = model.getValueAt(i, 0).toString() + "," + 
                                   model.getValueAt(i, 1).toString();
                out.println(rowString);
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
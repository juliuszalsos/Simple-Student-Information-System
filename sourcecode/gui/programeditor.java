package gui;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class programeditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer  {
    private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    private JButton editButton = new JButton("...");

    public programeditor(JTable table, DefaultTableModel model) {

        editButton.setFont(new Font("SansSerif", Font.BOLD, 24));
        editButton.setPreferredSize(new Dimension(30, 30));
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setFocusPainted(false);
        editButton.setMargin(new Insets(0, 0, 0, 0));
        panel.add(editButton);

        panel.setBackground(table.getSelectionBackground());

        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteProgram = new JMenuItem("Delete Program");
        JMenuItem updateProgram = new JMenuItem("Update Information");

        updateProgram.addActionListener(e -> {
            int viewrow = table.getSelectedRow();
            if (viewrow != -1) {
                int row = table.convertRowIndexToModel(viewrow);
                String pcode = model.getValueAt(row, 0).toString();
                String pname = model.getValueAt(row, 1).toString();
                String ccode = model.getValueAt(row, 2).toString();

                JTextField f1 = new JTextField(pcode);
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
                    JOptionPane.showMessageDialog(null, "Program Updated Successfully");
                }
            }
        });

        deleteProgram.addActionListener(e -> {
            int viewrow = table.getSelectedRow();
            if (viewrow != -1) {
                int row = table.convertRowIndexToModel(viewrow);    
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this program?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    model.removeRow(row);
                    updateProgramInfo(model); 
                    JOptionPane.showMessageDialog(null, "Program Removed Permanently");
                }
            }
        });

        menu.add(updateProgram);
        menu.add(deleteProgram);

        editButton.addActionListener(e -> {
            menu.show(editButton, editButton.getWidth() / 2, editButton.getHeight() / 2);
        });
    }

    private boolean isCollegeValid(String collegeCode) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("sourcecode/csvfiles/College.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 0 && data[0].equalsIgnoreCase(collegeCode)) return true;
            }
        } catch (java.io.IOException e) {
            return false;
        }
        return false;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        panel.removeAll();
        panel.add(editButton);
        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
        } else {
            panel.setBackground(table.getBackground());
        }
        return panel;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        panel.removeAll();
        panel.add(editButton);
        panel.setBackground(table.getSelectionBackground());
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }

    private void updateProgramInfo(DefaultTableModel model) {
        try (java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter("sourcecode/csvfiles/Program.csv"))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                String rowString = String.join(",", 
                    model.getValueAt(i, 0).toString(),
                    model.getValueAt(i, 1).toString(),
                    model.getValueAt(i, 2).toString()
                );
                out.println(rowString);
            }
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(null, "Error updating CSV: " + ex.getMessage());
        }
    }
}

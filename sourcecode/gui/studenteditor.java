package gui;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class studenteditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
    
    private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    private JButton editButton = new JButton("...");

    public studenteditor(JTable table, DefaultTableModel model) {

        editButton.setFont(new Font("SansSerif", Font.BOLD, 24));
        editButton.setPreferredSize(new Dimension(30, 30));
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setFocusPainted(false);
        editButton.setMargin(new Insets(0, 0, 0, 0));
        panel.add(editButton);

        panel.setBackground(table.getSelectionBackground());

        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteStudent = new JMenuItem("Delete Student");
        JMenuItem updateStudent = new JMenuItem("Update Information");

        updateStudent.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String id = model.getValueAt(row, 0).toString();
                String fn = model.getValueAt(row, 1).toString();
                String ln = model.getValueAt(row, 2).toString();
                String pr = model.getValueAt(row, 3).toString();
                String yr = model.getValueAt(row, 4).toString();
                String gn = model.getValueAt(row, 5).toString();

                JTextField f1 = new JTextField(id);
                JTextField f2 = new JTextField(fn);
                JTextField f3 = new JTextField(ln);
                JTextField f4 = new JTextField(pr);
                JTextField f5 = new JTextField(yr);
                JTextField f6 = new JTextField(gn);

                Object[] message = {
                    "Student ID:", f1,
                    "First Name:", f2,
                    "Last Name:", f3,
                    "Program:", f4,
                    "Year:", f5,
                    "Gender:", f6
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Update Student", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    model.setValueAt(f1.getText(), row, 0);
                    model.setValueAt(f2.getText(), row, 1);
                    model.setValueAt(f3.getText(), row, 2);
                    model.setValueAt(f4.getText(), row, 3);
                    model.setValueAt(f5.getText(), row, 4);
                    model.setValueAt(f6.getText(), row, 5);
                    
                    updateStudentInfo(model);
                    JOptionPane.showMessageDialog(null, "Student Updated Successfully");
                }
            }
        });

        deleteStudent.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    model.removeRow(row);
                    updateStudentInfo(model); 
                    JOptionPane.showMessageDialog(null, "Student Removed Permanently");
                }
            }
        });

        menu.add(updateStudent);
        menu.add(deleteStudent);

        editButton.addActionListener(e -> {
            menu.show(editButton, editButton.getWidth() / 2, editButton.getHeight() / 2);
        });
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

    private void updateStudentInfo(DefaultTableModel model) {
        try (java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter("sourcecode/csvfiles/Student.csv"))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                String rowString = String.join(",", 
                    model.getValueAt(i, 0).toString(),
                    model.getValueAt(i, 1).toString(),
                    model.getValueAt(i, 2).toString(),
                    model.getValueAt(i, 3).toString(),
                    model.getValueAt(i, 4).toString(),
                    model.getValueAt(i, 5).toString()
                );
                out.println(rowString);
            }
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(null, "Error updating CSV: " + ex.getMessage());
        }
    }
}
package gui;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;


public class collegeeditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer  {
    private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    private JButton editButton = new JButton("...");

    public collegeeditor(JTable table, DefaultTableModel model) {

        editButton.setFont(new Font("SansSerif", Font.BOLD, 24));
        editButton.setPreferredSize(new Dimension(30, 30));
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setFocusPainted(false);
        editButton.setMargin(new Insets(0, 0, 0, 0));
        panel.add(editButton);

        panel.setBackground(table.getSelectionBackground());

        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteCollege = new JMenuItem("Delete College");
        JMenuItem updateCollege = new JMenuItem("Update Information");

        updateCollege.addActionListener(e -> {
            int viewrow = table.getSelectedRow();
            if (viewrow != -1) {
                int row = table.convertRowIndexToModel(viewrow);
                String ccode = model.getValueAt(row, 0).toString();
                String cname = model.getValueAt(row, 1).toString();

                JTextField f1 = new JTextField(ccode);
                JTextField f2 = new JTextField(cname);


                Object[] message = {
                    "College Code:", f1,
                    "College Name:", f2
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Update College", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    model.setValueAt(f1.getText(), row, 0);
                    model.setValueAt(f2.getText(), row, 1);
                    updateCollegeInfo(model);
                    JOptionPane.showMessageDialog(null, "College Updated Successfully");
                    }
            }
        });

        deleteCollege.addActionListener(e -> {
            int viewrow = table.getSelectedRow();
            if (viewrow != -1) {
                int row = table.convertRowIndexToModel(viewrow);
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this college?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    model.removeRow(row);
                    updateCollegeInfo(model); 
                    JOptionPane.showMessageDialog(null, "College Removed Permanently");
                }
            }
        });

        menu.add(updateCollege);
        menu.add(deleteCollege);

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

    private void updateCollegeInfo(DefaultTableModel model) {
        try (java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter("sourcecode/csvfiles/College.csv"))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                String rowString = String.join(",", 
                    model.getValueAt(i, 0).toString(),
                    model.getValueAt(i, 1).toString()
                );
                out.println(rowString);
            }
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(null, "Error updating CSV: " + ex.getMessage());
        }
    }
}


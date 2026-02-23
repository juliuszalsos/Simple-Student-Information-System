package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

public class collegeinterface extends JPanel {
    private JTextField tfCCode, tfCName;
    private JTable collegeTable;
    private DefaultTableModel tablemodel2;
    private JTextField tfSearch;
    private TableRowSorter<DefaultTableModel> sorter;

    private final Color PRIMARY_NAVY = new Color(44, 62, 80);
    private final Color ACCENT_BLUE = new Color(52, 152, 219);
    private final Color BG_LIGHT = new Color(245, 247, 250);
    private final Color GRID_COLOR = new Color(210, 210, 210);

    public collegeinterface() {
        this.setLayout(new BorderLayout());
        this.setBackground(BG_LIGHT);

        String[] columns = {"College Code", "College Name", ""};
        tablemodel2 = new DefaultTableModel(columns, 0); 
        collegeTable = new JTable(tablemodel2);

        collegeTable.setRowHeight(32); 
        collegeTable.setShowGrid(true);
        collegeTable.setGridColor(GRID_COLOR);
        collegeTable.setSelectionBackground(new Color(210, 230, 250));

        JTableHeader header = collegeTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 35));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(235, 238, 242));
        header.setBorder(new LineBorder(GRID_COLOR));

        TableColumn actionColumn = collegeTable.getColumnModel().getColumn(2);
        collegeeditor actionEditor = new collegeeditor(collegeTable, tablemodel2);
        actionColumn.setCellRenderer(actionEditor);
        actionColumn.setCellEditor(actionEditor);
        actionColumn.setMaxWidth(45);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        searchPanel.setBackground(PRIMARY_NAVY);
        
        JLabel searchLabel = new JLabel("Search College: ");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        tfSearch = new JTextField(20);
        tfSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT_BLUE, 1), 
                BorderFactory.createEmptyBorder(3, 5, 3, 5)));

        searchPanel.add(searchLabel);
        searchPanel.add(tfSearch);

        JPanel addCollege = new JPanel(new BorderLayout());
        addCollege.setBackground(Color.WHITE);
        addCollege.setBorder(new MatteBorder(2, 0, 0, 0, PRIMARY_NAVY));

        JLabel titleLabel = new JLabel("ADD YOUR COLLEGE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(PRIMARY_NAVY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 20, 5, 0));
        addCollege.add(titleLabel, BorderLayout.NORTH);

        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));

        JPanel gridPanel = new JPanel(new GridLayout(1, 3, 15, 0)); 
        gridPanel.setOpaque(false);

        tfCCode = createStyledField("College Code:");
        tfCName = createStyledField("College Name:");

        gridPanel.add(tfCCode.getParent());
        gridPanel.add(tfCName.getParent());
        gridPanel.add(new JPanel() {{ setOpaque(false); }});

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        btnPanel.setOpaque(false);
        JButton addButton = new JButton("Add College");
        addButton.setBackground(ACCENT_BLUE);
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setPreferredSize(new Dimension(140, 30));
        btnPanel.add(addButton);

        formContainer.add(gridPanel);
        formContainer.add(btnPanel);
        addCollege.add(formContainer, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(collegeTable);
        scrollPane.setBorder(new LineBorder(GRID_COLOR));

        JPanel collegePanel = new JPanel(new BorderLayout());
        collegePanel.add(searchPanel, BorderLayout.NORTH);
        collegePanel.add(scrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, collegePanel, addCollege);
        splitPane.setResizeWeight(0.85); 
        splitPane.setDividerSize(4);
        splitPane.setBorder(null);

        this.add(splitPane, BorderLayout.CENTER);

        setupLogic(addButton);
        loadData();
    }

    private JTextField createStyledField(String labelText) {
        JPanel group = new JPanel(new BorderLayout(0, 2));
        group.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(PRIMARY_NAVY);
        
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        field.setPreferredSize(new Dimension(0, 25));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(GRID_COLOR, 1),
                BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        
        group.add(label, BorderLayout.NORTH);
        group.add(field, BorderLayout.CENTER);
        return field;
    }

    private void setupLogic(JButton addButton) {
        sorter = new TableRowSorter<>(tablemodel2);
        collegeTable.setRowSorter(sorter);
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = tfSearch.getText();
                sorter.setRowFilter(text.trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
            }
        });

        addButton.addActionListener(e -> {
            String ccode = tfCCode.getText().trim().toUpperCase();
            String cname = tfCName.getText().trim();

            if (ccode.isEmpty() || cname.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all required fields!");
                return;
            }

            saveData(ccode, cname);
            loadData();
        });
    }

    private void saveData(String ccode, String cname) {
        try (java.io.FileWriter fw = new java.io.FileWriter("sourcecode/csvfiles/College.csv", true);
             java.io.PrintWriter out = new java.io.PrintWriter(new java.io.BufferedWriter(fw))) {
            out.println(ccode + "," + cname);
            JOptionPane.showMessageDialog(null, "College Added Successfully!");  
            tfCCode.setText("");
            tfCName.setText("");
        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving: " + e.getMessage());
        }
    }

    private void loadData() {
        tablemodel2.setRowCount(0);
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("sourcecode/csvfiles/College.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    tablemodel2.addRow(new Object[]{data[0], data[1], ""});
                }
            }
        } catch (java.io.IOException e) { }
    }
}


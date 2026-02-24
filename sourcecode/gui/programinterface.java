package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class programinterface extends JPanel {
    private JTextField tfPCode, tfPName, tfCCode;
    private JTable programTable;
    private DefaultTableModel tablemodel1;
    private JTextField tfSearch;
    private TableRowSorter<DefaultTableModel> sorter;

    private final Color PRIMARY_NAVY = new Color(44, 62, 80);
    private final Color ACCENT_BLUE = new Color(52, 152, 219);
    private final Color BG_LIGHT = new Color(245, 247, 250);
    private final Color GRID_COLOR = new Color(210, 210, 210);

    public programinterface() {
        this.setLayout(new BorderLayout());
        this.setBackground(BG_LIGHT);

        String[] columns = {"Program Code", "Program Name", "College Code", ""};
        tablemodel1 = new DefaultTableModel(columns, 0);
        programTable = new JTable(tablemodel1);
        
        programTable.setRowHeight(32);
        programTable.setShowGrid(true);
        programTable.setGridColor(GRID_COLOR);
        programTable.setSelectionBackground(new Color(210, 230, 250));
        programTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JTableHeader header = programTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 35));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(235, 238, 242));
        header.setBorder(new LineBorder(GRID_COLOR));

        TableColumn actionColumn = programTable.getColumnModel().getColumn(3);
        programeditor actionEditor = new programeditor(programTable, tablemodel1);
        actionColumn.setCellRenderer(actionEditor);
        actionColumn.setCellEditor(actionEditor);
        actionColumn.setMaxWidth(45);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        searchPanel.setBackground(PRIMARY_NAVY);
        
        JLabel searchLabel = new JLabel("Search Program: ");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        tfSearch = new JTextField(20);
        tfSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT_BLUE, 1), 
                BorderFactory.createEmptyBorder(3, 5, 3, 5)));

        searchPanel.add(searchLabel);
        searchPanel.add(tfSearch);

        JPanel addProgram = new JPanel(new BorderLayout());
        addProgram.setBackground(Color.WHITE);
        addProgram.setBorder(new MatteBorder(2, 0, 0, 0, PRIMARY_NAVY));

        JLabel titleLabel = new JLabel("ADD YOUR PROGRAM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(PRIMARY_NAVY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 20, 5, 0));
        addProgram.add(titleLabel, BorderLayout.NORTH);

        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));

        JPanel gridPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        gridPanel.setOpaque(false);

        tfPCode = createStyledField("Program Code:");
        tfPName = createStyledField("Program Name:");
        tfCCode = createStyledField("College Code:");

        gridPanel.add(tfPCode.getParent());
        gridPanel.add(tfPName.getParent());
        gridPanel.add(tfCCode.getParent());
        gridPanel.add(new JPanel() {{ setOpaque(false); }});

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        btnPanel.setOpaque(false);
        JButton addButton = new JButton("Add Program");
        addButton.setBackground(ACCENT_BLUE);
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setFocusPainted(false);
        addButton.setPreferredSize(new Dimension(140, 30));
        btnPanel.add(addButton);

        formContainer.add(gridPanel);
        formContainer.add(btnPanel);
        addProgram.add(formContainer, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(programTable);
        scrollPane.setBorder(new LineBorder(GRID_COLOR));
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.add(searchPanel, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableContainer, addProgram);
        splitPane.setResizeWeight(0.85);
        splitPane.setDividerSize(4);
        splitPane.setBorder(null);

        this.add(splitPane, BorderLayout.CENTER);

        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                loadData();
            }
        });

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
        field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(GRID_COLOR, 1), BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        group.add(label, BorderLayout.NORTH);
        group.add(field, BorderLayout.CENTER);
        return field;
    }

    private void setupLogic(JButton addButton) {
        sorter = new TableRowSorter<>(tablemodel1);
        programTable.setRowSorter(sorter);
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
            String pcode = tfPCode.getText().trim().toUpperCase();
            String pname = tfPName.getText().trim();
            String ccode = tfCCode.getText().trim().toUpperCase();

            if (pcode.isEmpty() || pname.isEmpty() || ccode.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields!");
                return;
            }

            saveData(pcode, pname, ccode);
            loadData();
        });
    }

    private void saveData(String pcode, String pname, String ccode) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("sourcecode/csvfiles/Program.csv", true)))) {
            out.println(pcode + "," + pname + "," + ccode);
            JOptionPane.showMessageDialog(null, "Program Added Successfully!");
            tfPCode.setText(""); tfPName.setText(""); tfCCode.setText("");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving: " + e.getMessage());
        }
    }

    public void loadData() {
        tablemodel1.setRowCount(0);
        Set<String> validColleges = new HashSet<>();

        try (BufferedReader brC = new BufferedReader(new FileReader("sourcecode/csvfiles/College.csv"))) {
            String line;
            while ((line = brC.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 0) validColleges.add(data[0].trim().toUpperCase());
            }
        } catch (IOException e) { }

               try (BufferedReader brP = new BufferedReader(new FileReader("sourcecode/csvfiles/Program.csv"))) {
            String line;
            while ((line = brP.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3) {
                    String collegeRef = data[2].trim().toUpperCase();
                    if (validColleges.contains(collegeRef)) {
                        tablemodel1.addRow(new Object[]{data[0], data[1], data[2], ""});
                    }
                }
            }
        } catch (IOException e) { }
    }
}

package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.HashSet;

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
        tfSearch = new JTextField(20);
        searchPanel.add(searchLabel);
        searchPanel.add(tfSearch);

        JPanel createPanel = new JPanel(new BorderLayout());
        createPanel.setBackground(Color.WHITE);
        createPanel.setBorder(new MatteBorder(2, 0, 0, 0, PRIMARY_NAVY));

        JLabel titleLabel = new JLabel("ADD NEW PROGRAM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(PRIMARY_NAVY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 20, 5, 0));
        createPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));

        JPanel grid = new JPanel(new GridLayout(1, 3, 15, 0));
        grid.setOpaque(false);
        tfPCode = createStyledField("Program Code:");
        tfPName = createStyledField("Program Name:");
        tfCCode = createStyledField("College Code:");
        grid.add(tfPCode.getParent());
        grid.add(tfPName.getParent());
        grid.add(tfCCode.getParent());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        btnPanel.setOpaque(false);
        JButton addButton = new JButton("Add Program");
        addButton.setBackground(ACCENT_BLUE);
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(130, 30));
        btnPanel.add(addButton);

        formContainer.add(grid);
        formContainer.add(btnPanel);
        createPanel.add(formContainer, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(programTable);
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(searchPanel, BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listPanel, createPanel);
        splitPane.setResizeWeight(0.85);
        this.add(splitPane, BorderLayout.CENTER);

        setupLogic(addButton);
        loadData();
    }

    private JTextField createStyledField(String labelText) {
        JPanel group = new JPanel(new BorderLayout(0, 2));
        group.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 25));
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

            if (pcode.isEmpty() || pname.isEmpty() || ccode.isEmpty()) return;

            if (doesProgramExist(pcode)) {
                JOptionPane.showMessageDialog(this, "This program already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            saveData(pcode, pname, ccode);
            loadData();
        });
    }

    private boolean doesProgramExist(String pcode) {
        File file = new File("sourcecode/csvfiles/Program.csv");
        if (!file.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length > 0 && data[0].trim().toUpperCase().equals(pcode)) {
                    return true;
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return false;
    }

    private void saveData(String pcode, String pname, String ccode) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("sourcecode/csvfiles/Program.csv", true)))) {
            out.println(pcode + "," + pname + "," + ccode);
            JOptionPane.showMessageDialog(null, "Program Added Successfully!");
            tfPCode.setText(""); tfPName.setText(""); tfCCode.setText("");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void loadData() {
        tablemodel1.setRowCount(0);
        
        HashSet<String> activeColleges = new HashSet<>();
        File collegeFile = new File("sourcecode/csvfiles/College.csv");
        if (collegeFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(collegeFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] data = line.split(",");
                    if (data.length > 0) activeColleges.add(data[0].trim().toUpperCase());
                }
            } catch (IOException e) { e.printStackTrace(); }
        }

        File programFile = new File("sourcecode/csvfiles/Program.csv");
        if (!programFile.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(programFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length >= 3) {
                    String targetCollege = data[2].trim().toUpperCase();
                    // Dynamically displays "NULL" if the code isn't in College.csv.
                    // If it is added back, it shows the code normally.
                    String displayedCollege = activeColleges.contains(targetCollege) ? data[2].trim() : "NULL";
                    tablemodel1.addRow(new Object[]{data[0].trim(), data[1].trim(), displayedCollege, ""});
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
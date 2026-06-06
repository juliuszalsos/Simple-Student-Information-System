package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.HashSet;

public class studentinterface extends JPanel {
    private JTextField tfId, tfFirstName, tfLastName;
    private JComboBox<String> cbProgram, cbYear; // Changed from JTextField tfProgram, tfYear
    private JComboBox<String> ddGender;
    private JTable studentTable;
    private DefaultTableModel tablemodel;
    private JTextField tfSearch;
    private TableRowSorter<DefaultTableModel> sorter;

    private final Color PRIMARY_NAVY = new Color(44, 62, 80);
    private final Color ACCENT_BLUE = new Color(52, 152, 219);
    private final Color BG_LIGHT = new Color(245, 247, 250);
    private final Color GRID_COLOR = new Color(210, 210, 210);

    public studentinterface() {
        this.setLayout(new BorderLayout());
        this.setBackground(BG_LIGHT);

        String[] columns = {"ID", "First Name", "Last Name", "Program", "Year", "Gender", ""};
        tablemodel = new DefaultTableModel(columns, 0);
        studentTable = new JTable(tablemodel);
        studentTable.setRowHeight(32); 
        studentTable.setShowGrid(true);
        studentTable.setGridColor(GRID_COLOR);
        studentTable.setSelectionBackground(new Color(210, 230, 250));
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JTableHeader header = studentTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 35));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(235, 238, 242));
        header.setBorder(new LineBorder(GRID_COLOR));

        TableColumn actionColumn = studentTable.getColumnModel().getColumn(6);
        studenteditor actionEditor = new studenteditor(studentTable, tablemodel);
        actionColumn.setCellRenderer(actionEditor);
        actionColumn.setCellEditor(actionEditor);
        actionColumn.setMaxWidth(45);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        searchPanel.setBackground(PRIMARY_NAVY);
        
        JLabel searchLabel = new JLabel("Search Student: ");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        tfSearch = new JTextField(20);
        tfSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT_BLUE, 1), 
                BorderFactory.createEmptyBorder(3, 5, 3, 5)));

        searchPanel.add(searchLabel);
        searchPanel.add(tfSearch);

        JPanel createPanel = new JPanel(new BorderLayout());
        createPanel.setBackground(Color.WHITE);
        createPanel.setBorder(new MatteBorder(2, 0, 0, 0, PRIMARY_NAVY));

        JLabel titleLabel = new JLabel("REGISTER STUDENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(PRIMARY_NAVY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 20, 5, 0));
        createPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));

        JPanel grid1 = new JPanel(new GridLayout(1, 6, 10, 0));
        grid1.setOpaque(false);
        
        JPanel pId = createStyledFieldGroup("ID:", tfId = new JTextField());
        JPanel pFN = createStyledFieldGroup("First Name:", tfFirstName = new JTextField());
        JPanel pLN = createStyledFieldGroup("Last Name:", tfLastName = new JTextField());

        // Create Dropdown for Programs
        cbProgram = new JComboBox<>();
        JPanel pPR = createStyledComboBoxGroup("Program:", cbProgram);

        // Create Dropdown for Year Levels (1, 2, 3, 4)
        cbYear = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        JPanel pYR = createStyledComboBoxGroup("Year:", cbYear);

        JPanel genGroup = new JPanel(new BorderLayout(0, 2));
        genGroup.setOpaque(false);
        JLabel genLabel = new JLabel("Gender:");
        genLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        genLabel.setForeground(PRIMARY_NAVY);
        ddGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        ddGender.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ddGender.setBackground(Color.WHITE);
        genGroup.add(genLabel, BorderLayout.NORTH);
        genGroup.add(ddGender, BorderLayout.CENTER);

        grid1.add(pId);
        grid1.add(pFN);
        grid1.add(pLN);
        grid1.add(pPR);
        grid1.add(pYR);
        grid1.add(genGroup);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        btnPanel.setOpaque(false);
        JButton addButton = new JButton("Add Student");
        addButton.setBackground(ACCENT_BLUE);
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setFocusPainted(false);
        addButton.setPreferredSize(new Dimension(130, 30));
        btnPanel.add(addButton);

        formContainer.add(grid1);
        formContainer.add(btnPanel);
        createPanel.add(formContainer, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(new LineBorder(GRID_COLOR));
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(searchPanel, BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listPanel, createPanel);
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

    private JPanel createStyledFieldGroup(String labelText, JTextField field) {
        JPanel group = new JPanel(new BorderLayout(0, 2));
        group.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(PRIMARY_NAVY);
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        field.setPreferredSize(new Dimension(0, 25));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(GRID_COLOR, 1), 
                BorderFactory.createEmptyBorder(1, 4, 1, 4)));
                
        group.add(label, BorderLayout.NORTH);
        group.add(field, BorderLayout.CENTER);
        return group; 
    }

    // Helper method to keep UI styling consistent for the new dropdown components
    private JPanel createStyledComboBoxGroup(String labelText, JComboBox<String> comboBox) {
        JPanel group = new JPanel(new BorderLayout(0, 2));
        group.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(PRIMARY_NAVY);
        
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(0, 25));
                
        group.add(label, BorderLayout.NORTH);
        group.add(comboBox, BorderLayout.CENTER);
        return group; 
    }

    // Automatically scans Program.csv and updates the dropdown selections dynamically
    public void populateProgramDropdown() {
        cbProgram.removeAllItems();
        File file = new File("sourcecode/csvfiles/Program.csv");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length > 0) {
                    cbProgram.addItem(data[0].trim().toUpperCase());
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void setupLogic(JButton addButton) {
        sorter = new TableRowSorter<>(tablemodel);
        studentTable.setRowSorter(sorter);
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
            String id = tfId.getText().trim();
            Object selectedProg = cbProgram.getSelectedItem();
            Object selectedYear = cbYear.getSelectedItem();
            
            if (!id.matches("^(201[8-9]|202[0-6])-([0-1][0-9]{3}|2[0-4][0-9]{2}|2500)$")) {
                JOptionPane.showMessageDialog(this, "Invalid ID Format! Use: YYYY-NNNN");
                return;
            }

            if (selectedProg == null || selectedYear == null || tfFirstName.getText().trim().isEmpty() || tfLastName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be completed!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (doesStudentIDExist(id)) {
                JOptionPane.showMessageDialog(this, "This student ID already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            saveData(id, tfFirstName.getText().trim(), tfLastName.getText().trim(), 
                     selectedProg.toString(), selectedYear.toString(), (String)ddGender.getSelectedItem());
            loadData();
        });
    }

    private boolean doesStudentIDExist(String id) {
        File file = new File("sourcecode/csvfiles/Student.csv");
        if (!file.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length > 0 && data[0].trim().equals(id)) {
                    return true;
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return false;
    }

    private void saveData(String id, String fn, String ln, String pr, String yr, String gn) {
        File csvFile = new File("sourcecode/csvfiles/Student.csv");
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(csvFile, true)))) {
            out.println(id + "," + fn + "," + ln + "," + pr + "," + yr + "," + gn);
            JOptionPane.showMessageDialog(null, "Student Registered!");
            tfId.setText(""); tfFirstName.setText(""); tfLastName.setText("");
        } catch (IOException e) { 
            JOptionPane.showMessageDialog(this, "Error saving entry: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    public void loadData() {
        tablemodel.setRowCount(0);
        populateProgramDropdown(); // Sync choices whenever data loads
        
        HashSet<String> activePrograms = new HashSet<>();
        File programFile = new File("sourcecode/csvfiles/Program.csv");
        if (programFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(programFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] data = line.split(",");
                    if (data.length > 0) activePrograms.add(data[0].trim().toUpperCase());
                }
            } catch (IOException e) { e.printStackTrace(); }
        }

        File studentFile = new File("sourcecode/csvfiles/Student.csv");
        if (!studentFile.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(studentFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; 
                String[] data = line.split(",");
                if (data.length >= 6) {
                    String targetProg = data[3].trim().toUpperCase();
                    String displayedProgram = activePrograms.contains(targetProg) ? data[3].trim() : "NOT ENROLLED";
                    tablemodel.addRow(new Object[]{
                        data[0].trim(), data[1].trim(), data[2].trim(), 
                        displayedProgram, data[4].trim(), data[5].trim(), ""
                    });
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class studentinterface extends JPanel {
    private JTextField tfId, tfFirstName, tfLastName, tfProgram, tfYear;
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
        
        tfId = createStyledField("ID:");
        tfFirstName = createStyledField("First Name:");
        tfLastName = createStyledField("Last Name:");
        tfProgram = createStyledField("Program:");
        tfYear = createStyledField("Year:");

        JPanel genGroup = new JPanel(new BorderLayout(0, 2));
        genGroup.setOpaque(false);
        JLabel genLabel = new JLabel("Gender:");
        genLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        ddGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        ddGender.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ddGender.setBackground(Color.WHITE);
        genGroup.add(genLabel, BorderLayout.NORTH);
        genGroup.add(ddGender, BorderLayout.CENTER);

        grid1.add(tfId.getParent());
        grid1.add(tfFirstName.getParent());
        grid1.add(tfLastName.getParent());
        grid1.add(tfProgram.getParent());
        grid1.add(tfYear.getParent());
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
            if (!id.matches("^20([01]\\d|2[0-6])-(?!0000)\\d{4}$")) {
                JOptionPane.showMessageDialog(this, "Invalid ID! (Format: 20XX-XXXX)");
                return;
            }
            saveData(id, tfFirstName.getText().trim(), tfLastName.getText().trim(), 
                     tfProgram.getText().trim(), tfYear.getText().trim(), (String)ddGender.getSelectedItem());
            loadData();
        });
    }

    private void saveData(String id, String fn, String ln, String pr, String yr, String gn) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("sourcecode/csvfiles/Student.csv", true)))) {
            out.println(id + "," + fn + "," + ln + "," + pr + "," + yr + "," + gn);
            JOptionPane.showMessageDialog(null, "Student Registered!");
            tfId.setText(""); tfFirstName.setText(""); tfLastName.setText("");
            tfProgram.setText(""); tfYear.setText("");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void loadData() {
        tablemodel.setRowCount(0);
        Set<String> validColleges = new HashSet<>();
        Set<String> validPrograms = new HashSet<>();

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
                    String pCode = data[0].trim().toUpperCase();
                    String cRef = data[2].trim().toUpperCase();
                    if (validColleges.contains(cRef)) {
                        validPrograms.add(pCode);
                    }
                }
            }
        } catch (IOException e) { }

            try (BufferedReader brS = new BufferedReader(new FileReader("sourcecode/csvfiles/Student.csv"))) {
            String sLine;
            while ((sLine = brS.readLine()) != null) {
                String[] sData = sLine.split(",");
                if (sData.length >= 6) {
                    String studentProg = sData[3].trim().toUpperCase();
                    if (validPrograms.contains(studentProg)) {
                        tablemodel.addRow(new Object[]{sData[0], sData[1], sData[2], sData[3], sData[4], sData[5], ""});
                    }
                }
            }
        } catch (IOException e) { }
    }
}
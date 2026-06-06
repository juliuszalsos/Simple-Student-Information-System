package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;

public class collegeinterface extends JPanel {
    private JTextField tfCollegeCode, tfCollegeName;
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
        collegeTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

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
        searchPanel.add(searchLabel);
        searchPanel.add(tfSearch);

        JPanel createPanel = new JPanel(new BorderLayout());
        createPanel.setBackground(Color.WHITE);
        createPanel.setBorder(new MatteBorder(2, 0, 0, 0, PRIMARY_NAVY));

        JLabel titleLabel = new JLabel("ADD NEW COLLEGE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(PRIMARY_NAVY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 20, 5, 0));
        createPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));

        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 0));
        grid.setOpaque(false);
        tfCollegeCode = createStyledField("College Code:");
        tfCollegeName = createStyledField("College Name:");
        grid.add(tfCollegeCode.getParent());
        grid.add(tfCollegeName.getParent());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        btnPanel.setOpaque(false);
        JButton addButton = new JButton("Add College");
        addButton.setBackground(ACCENT_BLUE);
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setPreferredSize(new Dimension(130, 30));
        btnPanel.add(addButton);

        formContainer.add(grid);
        formContainer.add(btnPanel);
        createPanel.add(formContainer, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(collegeTable);
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
            String code = tfCollegeCode.getText().trim().toUpperCase();
            String name = tfCollegeName.getText().trim();

            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty!");
                return;
            }

            if (doesCollegeExist(code)) {
                JOptionPane.showMessageDialog(this, "this college already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            saveData(code, name);
            loadData();
            
            // --- FIXED: Instantly trigger background filtering refresh loops ---
            refreshDependentTabs();
        });
    }

    // Crawls container components to instantly wake up and refresh dependent interfaces
    private void refreshDependentTabs() {
        Window topWindow = SwingUtilities.getWindowAncestor(this);
        if (topWindow instanceof JFrame) {
            JFrame mainFrame = (JFrame) topWindow;
            refreshChildPanels(mainFrame.getContentPane());
        }
    }

    private void refreshChildPanels(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof programinterface) {
                ((programinterface) comp).loadData();
            } else if (comp instanceof studentinterface) {
                ((studentinterface) comp).loadData();
            } else if (comp instanceof Container) {
                refreshChildPanels((Container) comp);
            }
        }
    }

    private boolean doesCollegeExist(String code) {
        File file = new File("sourcecode/csvfiles/College.csv");
        if (!file.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length > 0 && data[0].trim().toUpperCase().equals(code)) {
                    return true;
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return false;
    }

    private void saveData(String code, String name) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("sourcecode/csvfiles/College.csv", true)))) {
            out.println(code + "," + name);
            JOptionPane.showMessageDialog(null, "College Added Successfully!");
            tfCollegeCode.setText("");
            tfCollegeName.setText("");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void loadData() {
        tablemodel2.setRowCount(0);
        File file = new File("sourcecode/csvfiles/College.csv");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length >= 2) {
                    tablemodel2.addRow(new Object[]{data[0].trim(), data[1].trim(), ""});
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}


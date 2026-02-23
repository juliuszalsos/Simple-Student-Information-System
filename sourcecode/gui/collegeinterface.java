package gui;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class collegeinterface extends JPanel{
    private JTextField tfCCode, tfCName;
    private JTable collegeTable;
    private DefaultTableModel tablemodel2;

    public collegeinterface(){
        this.setLayout(new BorderLayout());

        String[] columns = {"College Code", "College Name", ""};
        tablemodel2 = new DefaultTableModel(columns, 0); 
        collegeTable = new JTable(tablemodel2);
        collegeTable.setRowHeight(35);
        TableColumn actionColumn = collegeTable.getColumnModel().getColumn(2);
        collegeeditor actionEditor = new collegeeditor(collegeTable, tablemodel2);
        actionColumn.setCellRenderer(actionEditor);
        actionColumn.setCellEditor(actionEditor);
        actionColumn.setPreferredWidth(40);
        actionColumn.setMaxWidth(40);

        JPanel collegePanel = new JPanel(new BorderLayout());
        collegePanel.setBackground(Color.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(collegeTable);
        collegePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel addCollege = new JPanel(new BorderLayout()); 
        addCollege.setBackground(Color.LIGHT_GRAY);
        addCollege.setPreferredSize(new Dimension(800, 100));
        addCollege.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        addCollege.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("ADD YOUR COLLEGE");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        addCollege.add(titleLabel, BorderLayout.NORTH);

        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.LIGHT_GRAY);

        JPanel gridPanel1 = new JPanel(new GridLayout(1, 3, 5, 5));
        gridPanel1.setBackground(Color.LIGHT_GRAY);
        JPanel ccodeGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ccodeGroup.setOpaque(false); 
        ccodeGroup.add(new JLabel("College Code: "));
        tfCCode = new JTextField(15); 
        ccodeGroup.add(tfCCode);
        gridPanel1.add(ccodeGroup);

        JPanel cnameGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cnameGroup.setOpaque(false);
        cnameGroup.add(new JLabel("College Name:"));
        tfCName = new JTextField(15);
        cnameGroup.add(tfCName);
        gridPanel1.add(cnameGroup);


        JPanel gridPanel2 = new JPanel(new GridLayout(1, 1, 5, 5));
        gridPanel2.setBackground(Color.LIGHT_GRAY);
        JPanel addCollegetoCSV = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addCollegetoCSV.setOpaque(false);
        JButton addButton = new JButton("Add College");
        addButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        addButton.setPreferredSize(new Dimension(140, 20));
        addCollegetoCSV.add(addButton);
        gridPanel2.add(addCollegetoCSV);

        formContainer.add(gridPanel1);
        formContainer.add(gridPanel2);

        
        
            addButton.addActionListener(e -> {
            String ccode = tfCCode.getText().trim();
            String cname = tfCName.getText().trim();

            if (ccode.isEmpty() || cname.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all required fields!");
            return;
            }


            saveData(ccode, cname);
            loadData();
        });
        addCollege.add(formContainer, BorderLayout.CENTER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, collegePanel, addCollege);
        splitPane.setResizeWeight(0.95);
        splitPane.setDividerSize(2);
        
       
        this.add(splitPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
        splitPane.setDividerLocation(0.9);

        loadData();
        });
    }

        private void saveData(String ccode, String cname) {
    try (java.io.FileWriter fw = new java.io.FileWriter("sourcecode/csvfiles/College.csv", true);
         java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
         java.io.PrintWriter out = new java.io.PrintWriter(bw)) {
        
       out.println(ccode + "," + cname);
        
        JOptionPane.showMessageDialog(null, "College Added Successfully!");  
        tfCCode.setText("");
        tfCName.setText("");
    } catch (java.io.IOException e) {
        JOptionPane.showMessageDialog(null, "Error saving to file: " + e.getMessage());
    }
        tfCCode.setText("");
        tfCName.setText("");

    }
     private void loadData() {
    tablemodel2.setRowCount(0);

    String line;
    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("sourcecode/csvfiles/College.csv"))) {
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            tablemodel2.addRow(data);
        }
    } catch (java.io.IOException e) {
        System.out.println("No existing database found or error reading file.");
        }
    }
}


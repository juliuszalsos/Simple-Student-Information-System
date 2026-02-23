package gui;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class programinterface extends JPanel{
    private JTextField tfPCode, tfPName;
    private JTable programTable;
    private DefaultTableModel tablemodel2;

    public programinterface(){
        this.setLayout(new BorderLayout());

        String[] columns = {"Program Code", "Program Name", ""};
        tablemodel2 = new DefaultTableModel(columns, 0); 
        programTable = new JTable(tablemodel2);
        programTable.setRowHeight(35);
        TableColumn actionColumn = programTable.getColumnModel().getColumn(2);
        programeditor actionEditor = new programeditor(programTable, tablemodel2);
        actionColumn.setCellRenderer(actionEditor);
        actionColumn.setCellEditor(actionEditor);
        actionColumn.setPreferredWidth(40);
        actionColumn.setMaxWidth(40);

        JPanel programPanel = new JPanel(new BorderLayout());
        programPanel.setBackground(Color.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(programTable);
        programPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel addProgram = new JPanel(new BorderLayout()); 
        addProgram.setBackground(Color.LIGHT_GRAY);
        addProgram.setPreferredSize(new Dimension(800, 100));

        JLabel titleLabel = new JLabel("ADD YOUR PROGRAM");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        addProgram.add(titleLabel, BorderLayout.NORTH);

        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.LIGHT_GRAY);

        JPanel gridPanel1 = new JPanel(new GridLayout(1, 3, 5, 5));
        gridPanel1.setBackground(Color.LIGHT_GRAY);
        JPanel pcodeGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pcodeGroup.setOpaque(false); 
        pcodeGroup.add(new JLabel("Program Code: "));
        tfPCode = new JTextField(15); 
        pcodeGroup.add(tfPCode);
        gridPanel1.add(pcodeGroup);

        JPanel pnameGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnameGroup.setOpaque(false);
        pnameGroup.add(new JLabel("Program Name:"));
        tfPName = new JTextField(15);
        pnameGroup.add(tfPName);
        gridPanel1.add(pnameGroup);

        JPanel gridPanel2 = new JPanel(new GridLayout(1, 1, 5, 5));
        gridPanel2.setBackground(Color.LIGHT_GRAY);
        JPanel addProgramtoCSV = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addProgramtoCSV.setOpaque(false);
        JButton addButton = new JButton("Add Program");
        addButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        addButton.setPreferredSize(new Dimension(140, 20));
        addProgramtoCSV.add(addButton);
        gridPanel2.add(addProgramtoCSV);

        formContainer.add(gridPanel1);
        formContainer.add(gridPanel2);

       addProgram.add(formContainer, BorderLayout.CENTER);
        
        addButton.addActionListener(e -> {
            String pcode = tfPCode.getText().trim();
            String pname = tfPName.getText().trim();

            if (pcode.isEmpty() || pname.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all required fields!");
                return;
            }

            saveData(pcode, pname);
            loadData();
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, programPanel, addProgram);
        splitPane.setResizeWeight(0.95);
        splitPane.setDividerSize(2);
        
        this.add(splitPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
        splitPane.setDividerLocation(0.9);

        loadData();
        });
    }

    private void saveData(String pcode, String pname) {
        try (java.io.FileWriter fw = new java.io.FileWriter("sourcecode/csvfiles/Program.csv", true);
             java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
             java.io.PrintWriter out = new java.io.PrintWriter(bw)) {
            
            out.println(pcode + "," + pname);
            
            JOptionPane.showMessageDialog(null, "Program Added Successfully!");  
            tfPCode.setText("");
            tfPName.setText("");
        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving to file: " + e.getMessage());
        }
    }

    private void loadData() {
        tablemodel2.setRowCount(0);
        String line;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("sourcecode/csvfiles/Program.csv"))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                tablemodel2.addRow(new Object[]{data[0], data[1], "..."});
            }
        } catch (java.io.IOException e) {
            System.out.println("No existing database found or error reading file.");
        }
    }
}

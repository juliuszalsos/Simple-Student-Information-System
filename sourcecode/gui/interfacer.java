package gui;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;



public class interfacer extends JPanel {
public static void launch(String[] args){} 
private JTextField tfId, tfFirstName, tfLastName, tfProgram, tfYear, tfGender;
    private JTable studentTable;
    private DefaultTableModel tablemodel;
    private String filePath = "src/data/Student.csv";

    public interfacer() {
        
        this.setLayout(new BorderLayout());


        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(Color.LIGHT_GRAY);
        listPanel.add(new JScrollPane(new JTable()), BorderLayout.CENTER);


        JPanel createPanel = new JPanel(); 
        createPanel.setBackground(Color.LIGHT_GRAY);
        createPanel.setPreferredSize(new Dimension(800, 100));
        createPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        
        createPanel.setLayout(new BorderLayout());


        JLabel titleLabel = new JLabel("REGISTER STUDENT");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        createPanel.add(titleLabel, BorderLayout.NORTH);


        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.LIGHT_GRAY);


        JPanel gridPanel1 = new JPanel(new GridLayout(1, 3, 5, 5));
        gridPanel1.setBackground(Color.LIGHT_GRAY);
        JPanel idGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idGroup.setOpaque(false); 
        idGroup.add(new JLabel("Student ID: "));
        tfId = new JTextField(15); 
        idGroup.add(tfId);
        gridPanel1.add(idGroup);

        JPanel fnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fnGroup.setOpaque(false);
        fnGroup.add(new JLabel("First Name:"));
        tfFirstName = new JTextField(15);
        fnGroup.add(tfFirstName);
        gridPanel1.add(fnGroup);

        JPanel lnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lnGroup.setOpaque(false);
        lnGroup.add(new JLabel("Last Name: "));
        tfLastName = new JTextField(15);
        lnGroup.add(tfLastName);
        gridPanel1.add(lnGroup);


        JPanel gridPanel2 = new JPanel(new GridLayout(1, 3, 5, 5));
        gridPanel2.setBackground(Color.LIGHT_GRAY);

        JPanel progGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        progGroup.setOpaque(false);
        progGroup.add(new JLabel("Program:    "));
        tfProgram = new JTextField(15);
        progGroup.add(tfProgram);
        gridPanel2.add(progGroup);

        JPanel yearGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        yearGroup.setOpaque(false);
        yearGroup.add(new JLabel("Year:            "));
        tfYear = new JTextField(15);
        yearGroup.add(tfYear);
        gridPanel2.add(yearGroup);

        JPanel genGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genGroup.setOpaque(false);
        genGroup.add(new JLabel("Gender:        "));
        tfGender = new JTextField(15);
        genGroup.add(tfGender);
        gridPanel2.add(genGroup);

        JPanel gridPanel3 = new JPanel(new GridLayout(1, 1, 5, 5));
        gridPanel3.setBackground(Color.LIGHT_GRAY);
        JPanel addStudent = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addStudent.setOpaque(false);
        JButton addButton = new JButton("Add Student");
        addButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        addButton.setPreferredSize(new Dimension(140, 20));
        addStudent.add(addButton);
        gridPanel3.add(addStudent);


        formContainer.add(gridPanel1);
        formContainer.add(gridPanel2);
        formContainer.add(gridPanel3);


        createPanel.add(formContainer, BorderLayout.CENTER);
        
        addButton.addActionListener(e -> {
            String id = tfId.getText().trim();
            String firstName = tfFirstName.getText().trim();
            String lastName = tfLastName.getText().trim();
            String program = tfProgram.getText().trim();
            String year = tfYear.getText().trim();
            String gender = tfGender.getText().trim();

            if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all required fields!");
            return;
            }


            saveToDatabase(id, firstName, lastName, program, year, gender);
        });



        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listPanel, createPanel);
        splitPane.setResizeWeight(0.8);
        
       
        this.add(splitPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
        splitPane.setDividerLocation(0.8);
        });
    }
    private void saveToDatabase(String id, String fn, String ln, String pr, String yr, String gn) {
    try (java.io.FileWriter fw = new java.io.FileWriter("src/data/Student.csv", true);
         java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
         java.io.PrintWriter out = new java.io.PrintWriter(bw)) {
        
        // Format: ID,FirstName,LastName,Program,Year,Gender
        out.println(id + "," + fn + "," + ln + "," + pr + "," + yr + "," + gn);
        
        JOptionPane.showMessageDialog(null, "Student Added Successfully!");
        
        // Clear the fields for the next entry
        tfId.setText("");
        tfFirstName.setText("");
        tfLastName.setText("");
        tfProgram.setText("");
        tfYear.setText("");
        tfGender.setText("");

    } catch (java.io.IOException e) {
        JOptionPane.showMessageDialog(null, "Error saving to file: " + e.getMessage());
    }
}
}
import gui.studentinterface;
import gui.programinterface;
import gui.collegeinterface;
import javax.swing.*;
import info.csvbridge;

public class run {
    public static void main(String[] args) {
        csvbridge.initializeDatabase();
        JFrame frame = new JFrame("Simple Student Information System");
        JTabbedPane SSIS = new JTabbedPane();
        studentinterface studentPanel = new studentinterface();
        SSIS.addTab("Student", studentPanel);
        programinterface programPanel = new programinterface();
        SSIS.addTab("Program", programPanel);
        collegeinterface collegePanel = new collegeinterface();
        SSIS.addTab("College", collegePanel);
        frame.add(SSIS);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}

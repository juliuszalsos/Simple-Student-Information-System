import gui.studentinterface;
import gui.programinterface;
import gui.collegeinterface;
import javax.swing.*;
import java.awt.*;

public class run {
public static void main(String[] args) {
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
        
        
        /*frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        interfacer panel = new interfacer();        
        frame.add(panel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);*/
    }
}

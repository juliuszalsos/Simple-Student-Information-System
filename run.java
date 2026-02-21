import gui.interfacer;
import javax.swing.*;
import java.awt.*;

public class run {
public static void main(String[] args) {
        JFrame frame = new JFrame("Simple Student Information System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        interfacer panel = new interfacer();        
        frame.add(panel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}

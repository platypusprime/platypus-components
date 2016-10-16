package platypus.components;

import javax.swing.JFrame;
import javax.swing.UIManager;

import platypus.components.text.PDateChooser;

/**
 * Tests UI components under system look and feel.
 * 
 * @author Jingchen Xu
 */
public class Tester {

    /**
     * The main method
     * 
     * @param args command-line arguments (unused)
     * @throws Exception if an error occurs while setting look and feel
     */
    public static void main(String[] args) throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new PDateChooser());
        frame.setVisible(true);
        frame.pack();
    }

}

package platypus.components.text;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A number selection interface with a text field whose numerical value can be
 * incremented and decremented by up and down buttons.
 * <p>
 * The initial value of the text box and size of the increment produced by the
 * buttons can be set.
 * 
 * @author Jingchen Xu
 */
public class PNumberSpinner extends JPanel {

    private static final long serialVersionUID = -1213888359844823243L;

    private PRegexField numberField;
    private JButton upButton, down;
    private int increment;

    /**
     * Creates a number box with an initial value and a set numerical increment
     * for when the increment/decrement buttons are pressed.
     * 
     * @param value the initial value
     * @param inc the button increment
     */
    public PNumberSpinner(int value, int inc) {
        increment = inc;

        setLayout(new GridBagLayout());

        numberField = new PRegexField(PRegexField.DIGITS_ONLY);
        numberField.setText(Integer.toString(value));
        GridBagConstraints fieldConstraints = new GridBagConstraints(0, 0, 1, 2, .5, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(numberField, fieldConstraints);

        upButton = new JButton("▲");
        upButton.setFont(new Font("Sans Serif", Font.PLAIN, 5));
        upButton.setMargin(new Insets(0, 1, 0, 1));
        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                numberField.setText(
                        Integer.toString(Integer.parseInt(numberField.getText()) + increment));
            }
        });
        GridBagConstraints upConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 5, 0);
        add(upButton, upConstraints);

        down = new JButton("▼");
        down.setFont(new Font("Sans Serif", Font.PLAIN, 5));
        down.setMargin(new Insets(0, 1, 0, 1));
        down.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Integer.parseInt(numberField.getText()) >= increment)
                    numberField.setText(
                            Integer.toString(Integer.parseInt(numberField.getText()) - increment));
            }
        });
        GridBagConstraints downConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 5, 0);
        add(down, downConstraints);
    }

    /**
     * Returns the number stored in the number box.
     * 
     * @return the number displayed
     */
    public int getNum() {
        return Integer.parseInt(numberField.getText());
    }

    /**
     * Sets the number stored in the number box and displays it in the text
     * field.
     * 
     * @param i the number to be stored
     */
    public void setNum(int i) {
        numberField.setText(Integer.toString(i));
    }

}

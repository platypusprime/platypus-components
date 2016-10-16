package platypus.components.combo;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * A alternative spinner with its buttons placed to the left and right sides of
 * a text field displaying its current value. The value of the spinner can only
 * be modified using these buttons.
 *
 * @author Jingchen Xu
 */
public class PHorizontalSpinField extends JPanel {

    private static final long serialVersionUID = -2815629539622617414L;

    private String[] values;
    private int currentIndex;

    private JLabel label;
    private ArrayList<ActionListener> listeners;

    /**
     * Creates a numerical spinner starting at 0.
     * 
     */
    public PHorizontalSpinField() {
        this(0);
    }

    /**
     * Creates a numerical spinner with a specified initial value.
     * 
     * @param initialValue initial value of the spinner
     */
    public PHorizontalSpinField(int initialValue) {
        this(null);

        setIndex(initialValue);
    }

    /**
     * Creates a text spinner which cycles through an array of strings.
     * 
     * @param values the strings the spinner will cycle through
     */
    public PHorizontalSpinField(String[] values) {
        super();

        listeners = new ArrayList<ActionListener>();
        this.values = values;
        currentIndex = 0;

        setLayout(new BorderLayout());

        JButton decButton = new JButton("<");
        decButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decrement();
                notifyListeners(e, "-");
            }
        });
        decButton.setMargin(new Insets(0, 5, 0, 5));
        add(decButton, BorderLayout.LINE_START);

        label = new JLabel(getValue());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(label, BorderLayout.CENTER);

        JButton incButton = new JButton(">");
        incButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                increment();
                notifyListeners(e, "+");
            }
        });
        incButton.setMargin(new Insets(0, 5, 0, 5));
        add(incButton, BorderLayout.LINE_END);
    }

    /**
     * Get the index of the current value of the spinner. In the case of a
     * numerical spinner, this is also the value.
     * 
     * @return the index of the spinner
     */
    public int getIndex() {
        return currentIndex;
    }

    /**
     * Get the current value of the spinner. In the case of a numerical
     * spinner, a string representation is returned.
     * 
     * @return the value associated with the spinner
     */
    public String getValue() {

        if (values == null)
            return Integer.toString(currentIndex);
        else
            return values[currentIndex];
    }

    /**
     * Increases the current index by one.
     */
    public void increment() {

        if (values != null && currentIndex >= values.length - 1)
            currentIndex = 0;
        else
            currentIndex++;

        label.setText(getValue());
    }

    /**
     * Decreases the current index by one.
     */
    public void decrement() {

        if (values != null && currentIndex <= 0)
            currentIndex = values.length - 1;
        else
            currentIndex--;

        label.setText(getValue());
    }

    /**
     * Sets the current index.
     * 
     * @param i the new index to be set
     */
    public void setIndex(int i) {

        if (values != null && (i < 0 || i >= values.length))
            throw new IllegalArgumentException("Index out of bounds");

        currentIndex = i;
        label.setText(getValue());
    }

    /**
     * Adds an ActionListener to the spinner.
     * 
     * @param l the listener to be added
     */
    public void addActionListener(ActionListener l) {
        listeners.add(l);
    }

    /**
     * Removes an ActionListener from the spinner.
     * 
     * @param l the listener to be removed
     */
    public void removeActionListener(ActionListener l) {
        listeners.remove(l);
    }

    private void notifyListeners(ActionEvent e, String command) {

        ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                command, e.getWhen(), e.getModifiers());

        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                ActionListener tmp = listeners.get(i);
                tmp.actionPerformed(evt);
            }
        }
    }

}

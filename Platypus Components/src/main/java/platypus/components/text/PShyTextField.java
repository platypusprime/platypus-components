package platypus.components.text;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * A JTextField that is disabled by default but can be enabled for editing on
 * double-click. When the field loses focus or the enter key is pressed, it is
 * disabled again.
 *
 */
public class PShyTextField extends JTextField {

    private static final long serialVersionUID = 1706792888886021736L;

    /**
     * Creates an empty shy text field.
     */
    public PShyTextField() {
        super();
        setup();
    }

    /**
     * Creates a shy text field initialized with specified string.
     * 
     * @param text the text to be displayed
     */
    public PShyTextField(String text) {
        super(text);
        setup();
    }

    /**
     * Creates a shy text field initialized with the specified number of
     * columns.
     * 
     * @param columns the number of columns to calculate the preferred width
     */
    public PShyTextField(int columns) {
        super(columns);
        setup();
    }

    /**
     * Creates a shy text field initialized with the specified string and number
     * of columns.
     * 
     * @param text the text to be displayed
     * @param columns the number of columns to calculate the preferred width
     */
    public PShyTextField(String text, int columns) {
        super(text, columns);
        setup();
    }

    /**
     * Creates a shy text field using the given text storage model initialized
     * with the specified string and number of columns.
     * 
     * @param doc the text storage to use
     * @param text the text to be displayed
     * @param columns the number of columns to calculate the preferred width
     */
    public PShyTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
        setup();
    }

    private void setup() {

        setEnabled(false);
        setDisabledTextColor(getForeground());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // enable text field on double-click
                if (e.getClickCount() == 2) {
                    setEnabled(true);
                    grabFocus();
                }
            }
        });

        // disable upon losing focus or enter key pressed
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setEnabled(false);
            }
        });
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setEnabled(false);
            }
        });
    }

}

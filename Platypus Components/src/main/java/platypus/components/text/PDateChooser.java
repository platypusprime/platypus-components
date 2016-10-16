package platypus.components.text;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import platypus.components.button.PImageButton;
import platypus.components.combo.PHorizontalSpinField;

/**
 * Swing component for intuitive date selection. Consists of an editable text
 * label with the date and a button which displays a calendar for selecting the
 * date.
 * 
 * @author Jingchen Xu
 *
 */
public class PDateChooser extends JPanel implements ActionListener, DocumentListener {

    private static final long serialVersionUID = -1867392930456573885L;

    // constants
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd");
    static {
        DATE_FORMAT.setLenient(false);
    }
    private static final String ICON_PATH = "/icon/calendar_20x20.png";
    private static final Color BUTTON_IDLE_COLOR = UIManager.getColor("Label.background");
    private static final Color BUTTON_OVER_COLOR = BUTTON_IDLE_COLOR.darker();
    private static final Color BUTTON_DOWN_COLOR = BUTTON_OVER_COLOR.darker();
    private static final String[] MONTHS = { "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December" };
    private static final String[] DAYS = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };

    // UI components
    private PShyTextField textField;
    private PImageButton popupButton;
    private JPopupMenu popup;
    private PHorizontalSpinField monthSpinner;
    private PHorizontalSpinField yearSpinner;
    private JButton[] buttons;

    // state fields
    private boolean valueValid;

    /**
     * Creates a PDateChooser without a text label.
     */
    public PDateChooser() {
        this(null);
    }

    /**
     * Creates a PDateChooser with a text label
     * 
     * @param value the text to display on the label
     */
    public PDateChooser(String value) {
        super(new BorderLayout());

        // create and add text field
        textField = new PShyTextField(10);
        textField.getDocument().addDocumentListener(this);
        add(textField, BorderLayout.CENTER);

        // initialize state fields
        valueValid = false;

        // set initial value
        if (value != null)
            textField.setText(value);

        // create and add button to show drop-down
        popupButton = new PImageButton(ICON_PATH);
        popupButton.setColors(BUTTON_IDLE_COLOR, BUTTON_OVER_COLOR, BUTTON_DOWN_COLOR);
        popupButton.setFadeBehavior(10, 200);
        add(popupButton, BorderLayout.LINE_END);

        // set up popup
        createPopup();

        // set popup to appear under button on-click
        popupButton.addActionListener(this);
    }

    private void createPopup() {
        popup = new JPopupMenu();
        popup.setLayout(new BorderLayout());

        // get selected date, if it exists
        Calendar cal = Calendar.getInstance();
        Date selectedDate = getDate();
        if (selectedDate != null)
            cal.setTime(selectedDate);

        JPanel upperPanel = new JPanel(new BorderLayout(5, 0));

        // create and add spinners
        monthSpinner = new PHorizontalSpinField(MONTHS);
        monthSpinner.setIndex(cal.get(Calendar.MONTH));
        monthSpinner.setPreferredSize(new Dimension(120, 20));
        monthSpinner.addActionListener(this);
        upperPanel.add(monthSpinner, BorderLayout.CENTER);
        yearSpinner = new PHorizontalSpinField(cal.get(Calendar.YEAR));
        yearSpinner.addActionListener(this);
        upperPanel.add(yearSpinner, BorderLayout.LINE_END);

        // create and add day of the week labels
        JPanel dayOfWeekLabels = new JPanel(new GridLayout(0, 7));
        for (String day : DAYS) {
            JLabel label = new JLabel(day);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            dayOfWeekLabels.add(label);
        }
        upperPanel.add(dayOfWeekLabels, BorderLayout.PAGE_END);

        popup.add(upperPanel, BorderLayout.PAGE_START);

        // instantiate and add buttons for each day
        JPanel datePanel = new JPanel(new GridLayout(0, 7));
        buttons = new JButton[7 * 6];
        for (int i = 0; i < 7 * 6; i++) {
            buttons[i] = new JButton();
            buttons[i].setMargin(new Insets(1, 1, 1, 0));
            buttons[i].setHorizontalAlignment(SwingConstants.CENTER);
            buttons[i].addActionListener(this);
            datePanel.add(buttons[i]);
        }
        popup.add(datePanel, BorderLayout.CENTER);

        // update popup before each appearance
        popup.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                setDate(getText());
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // do nothing
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // do nothing
            }
        });
    }

    /**
     * Getter for the text displayed on the text label.
     * 
     * @return the text on the label
     */
    public String getText() {
        return textField.getText();
    }

    /**
     * Getter for the currently selected date. This is represented by the text
     * currently displayed on the label.
     * 
     * @return the currently selected date, or null if no such value exists
     */
    public Date getDate() {

        try {
            return DATE_FORMAT.parse(getText());
        } catch (ParseException e) {
            System.err.println("PDateChooser.getDate - could not parse date");
        } catch (NullPointerException e) {
            System.err.println("PDateChooser.getDate - null value");
        }

        return null;
    }

    /**
     * Setter for the date.
     * 
     * @param s the date to set. Should be in format yyyy-MM-dd.
     */
    public void setDate(String s) {

        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(DATE_FORMAT.parse(s));
            textField.setText(s);
            monthSpinner.setIndex(cal.get(Calendar.MONTH));
            yearSpinner.setIndex(cal.get(Calendar.YEAR));
            updateButtons();
        } catch (ParseException e) {
            System.err.println("PDateChooser.setDate - could not parse date");
            textField.setText("");
        } catch (NullPointerException e) {
            System.err.println("PDateChooser.setDate - null argument");
            textField.setText("");
        }
    }

    /**
     * Setter for the date.
     * 
     * @param date the date to set
     */
    public void setDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        textField.setText(DATE_FORMAT.format(date));
        monthSpinner.setIndex(cal.get(Calendar.MONTH));
        yearSpinner.setIndex(cal.get(Calendar.YEAR));
        updateButtons();
    }

    private void validateText() {

        if (textField.getText().matches("\\d{4}-\\d{2}-\\d{2}")) {
            try {
                DATE_FORMAT.parse(textField.getText());
                textField.setForeground(UIManager.getColor("TextField.foreground"));
                valueValid = true;

            } catch (ParseException e) {
                textField.setForeground(Color.RED);
                valueValid = false;
            }
        } else {
            textField.setForeground(Color.RED);
            valueValid = false;
        }
        textField.setDisabledTextColor(textField.getForeground());
    }

    /**
     * Checks whether the currently displayed string represents a valid date.
     * 
     * @return true if valid, false otherwise
     */
    public boolean isValueValid() {
        return valueValid;
    }

    private void updateButtons() {

        // get selected date, or current date if text does not parse
        Calendar selected = Calendar.getInstance();
        Date selectedDate = getDate();
        if (selectedDate != null)
            selected.setTime(selectedDate);

        // flip the date back to the first Sunday before month start
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, yearSpinner.getIndex());
        cal.set(Calendar.MONTH, monthSpinner.getIndex());
        cal.set(Calendar.DATE, 1);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
            cal.add(Calendar.DATE, -1);

        // create each button one by one
        for (JButton b : buttons) {
            // display the day of the month on the button
            b.setText(Integer.toString(cal.get(Calendar.DATE)));
            // store the corresponding date as an action command
            b.setActionCommand(DATE_FORMAT.format(cal.getTime()));

            // grey out days in other months
            if (cal.get(Calendar.MONTH) != monthSpinner.getIndex()
                    || cal.get(Calendar.YEAR) != yearSpinner.getIndex())
                b.setForeground(Color.GRAY);

            // highlight selected day red, or current day if none selected
            else if (cal.get(Calendar.YEAR) == selected.get(Calendar.YEAR)
                    && cal.get(Calendar.MONTH) == selected.get(Calendar.MONTH)
                    && cal.get(Calendar.DATE) == selected.get(Calendar.DATE))
                b.setForeground(Color.RED);

            // color everything else normally
            else
                b.setForeground(UIManager.getColor("Button.foreground"));

            // advance calendar for next button
            cal.add(Calendar.DATE, 1);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == popupButton) {

            // show popup
            int x = popupButton.getPreferredSize().width - popup.getPreferredSize().width;
            int y = popupButton.getPreferredSize().height;
            popup.show(popupButton, x, y);

        } else if (e.getSource() == monthSpinner) {

            // update year spinner if necessary
            if (e.getActionCommand().equals("+") && monthSpinner.getIndex() == 0) {
                yearSpinner.increment();
            } else if (e.getActionCommand().equals("-") && monthSpinner.getIndex() == 11) {
                yearSpinner.decrement();
            }

            updateButtons();

        } else if (e.getSource() == yearSpinner) {
            updateButtons();

        } else {
            textField.setText(e.getActionCommand());
            popup.setVisible(false);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        validateText();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        validateText();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        validateText();
    }
}

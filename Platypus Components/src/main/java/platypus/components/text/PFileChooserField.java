package platypus.components.text;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import platypus.components.button.PImageButton;

/**
 * A text field with a browse button, which opens a file chooser interface. The
 * output of this is fed into the text field.
 * 
 * @author Jingchen Xu
 */
public class PFileChooserField extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String ICON_PATH = "/platypus/components/resources/open.png";

    private JTextField outputField;
    private PImageButton browseButton;
    private JFileChooser fc;

    /**
     * Creates a <code>PFileChooserField</code> with specified initial location
     * and file selection mode for the file chooser.
     * 
     * @param defaultLocation the initial location
     * @param fileSelectionMode the file selection mode
     */
    public PFileChooserField(File defaultLocation, int fileSelectionMode) {

        setLayout(new GridBagLayout());

        outputField = new JTextField(15);
        outputField.setEditable(false);
        add(outputField, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        if (defaultLocation != null)
            fc = new JFileChooser(defaultLocation);
        else
            fc = new JFileChooser();

        fc.setFileSelectionMode(fileSelectionMode);
        Action details = fc.getActionMap().get("viewTypeDetails");
        details.actionPerformed(null);

        browseButton = new PImageButton(ICON_PATH, 4);
        browseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (fc.showOpenDialog(outputField) == JFileChooser.APPROVE_OPTION) {
                    outputField.setText(fc.getSelectedFile().getAbsolutePath());
                }
            }
        });
        add(browseButton, new GridBagConstraints(1, 0, 1, 1, 0, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 3, 0, 0), 0, 0));

    }

    /**
     * Creates a <code>PFileChooserField</code> with the default initial
     * location and specified file selection mode for the file chooser.
     * 
     * @param fileSelectionMode the file selection mode
     */
    public PFileChooserField(int fileSelectionMode) {
        this(null, fileSelectionMode);
    }

    /**
     * Creates a <code>PFileChooserField</code> with specified initial location
     * and file-only file selection mode for the file chooser.
     * 
     * @param defaultLocation the initial location
     */
    public PFileChooserField(File defaultLocation) {
        this(defaultLocation, JFileChooser.FILES_ONLY);
    }

    /**
     * Creates a <code>PFileChooserField</code> with default initial location
     * and file-only file selection mode.
     */
    public PFileChooserField() {
        this(null, JFileChooser.FILES_ONLY);
    }

    /**
     * Sets the content of the text field with the path of a file.
     * 
     * @param file the file whose path will be displayed
     */
    public void setPath(File file) {
        outputField.setText(file.getAbsolutePath());
    }

    /**
     * Sets the content of the text field with a given string.
     * 
     * @param path the string to be displayed
     */
    public void setText(String path) {
        outputField.setText(path);
    }

    /**
     * Returns the text currently displayed in the text field.
     * 
     * @return the string in the text field
     */
    public String getText() {
        return outputField.getText();
    }

    /**
     * Returns the file represented by the path displayed in the text field.
     * 
     * @return the file represented by the string in the text field
     */
    public File getFile() {
        return new File(outputField.getText());
    }

    /**
     * Adds an <code>ActionListener</code> to this button.
     * 
     * @param listener the <code>ActionListener</code> to be added
     */
    public void addActionListener(ActionListener listener) {
        browseButton.addActionListener(listener);
    }
}

package platypus.components.text;

import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;

/**
 * A text field which restricts input based on a regular expression. Input into
 * the text field which does not match the regex will not be allowed to pass
 * into the underlying document and will not appear in the text field.
 * 
 * @author Jingchen Xu
 */
public class PRegexField extends JTextField {

	private static final long serialVersionUID = 1L;

	public static final String NO_RESTRICTION = "";
	public static final String DIGITS_ONLY = "[\\D]";

	protected String regex;

	/**
	 * Creates a text field with specified regular expression filter.
	 * 
	 * @param regex a regular expression specifying restricted inputs
	 */
	public PRegexField(String regex) {
		super();
		applyRegex(regex);
	}

	/**
	 * Creates a text field with specified character length and regular
	 * expression filter.
	 * 
	 * @param length length of text field
	 * @param regex a regular expression specifying restricted inputs
	 */
	public PRegexField(int length, String regex) {
		super(length);
		applyRegex(regex);
	}

	/**
	 * Creates a text field with initial content an regular expression filter.
	 * The filter is not applied to the initial text.
	 * 
	 * @param text the initial content of the text field
	 * @param regex a regular expression specifying restricted inputs
	 */
	public PRegexField(String text, String regex) {
		super(text);
		applyRegex(regex);
	}

	private void applyRegex(String newregex) {
		regex = newregex;

		if (regex.length() > 0) {
			PlainDocument doc = new PlainDocument();
			doc.setDocumentFilter(new DocumentFilter() {

				@Override
				public void insertString(FilterBypass fb, int off, String str,
						AttributeSet attr)
						throws BadLocationException {
					fb.insertString(off, str.replaceAll(regex, ""), attr);
				}

				@Override
				public void replace(FilterBypass fb, int off, int len,
						String str, AttributeSet attr)
						throws BadLocationException {
					fb.replace(off, len, str.replaceAll(regex, ""), attr);
				}
			});

			this.setDocument(doc);
		}
	}
}

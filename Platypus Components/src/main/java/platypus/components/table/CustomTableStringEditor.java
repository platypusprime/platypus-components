package platypus.components.table;

import java.awt.Component;
import java.awt.Font;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

/**
 * A slightly more customizable table cell editor for string content.
 *
 * @author Jingchen Xu
 */
public class CustomTableStringEditor extends AbstractCellEditor implements
		TableCellEditor {

	private static final long serialVersionUID = 1811970172124098261L;

	private JTextField field;

	/**
	 * Creates a string editor with centered 12pt sans-serif text.
	 */
	public CustomTableStringEditor() {
		this(SwingConstants.CENTER, new Font("sanserif", Font.PLAIN, 12));
	}

	/**
	 * Creates a string editor with specified alignment and font.
	 * 
	 * @param alignment horizontal alignment of editor text
	 * @param font font to use for editor text
	 */
	public CustomTableStringEditor(int alignment, Font font) {
		super();
		field = new JTextField();
		field.setHorizontalAlignment(alignment);
		field.setFont(font);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected,
			int row, int column) {
		field.setText((String) value);
		return field;
	}

	@Override
	public Object getCellEditorValue() {
		return field.getText();
	}

}

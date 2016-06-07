package platypus.components.table;

import java.awt.Component;
import java.awt.Font;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

public class CustomStringEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 1811970172124098261L;

	private JTextField field;

	public CustomStringEditor() {
		super();
		field = new JTextField();
		field.setHorizontalAlignment(SwingConstants.CENTER);
		field.setFont(new Font("sanserif", Font.PLAIN, 12));
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
			int row, int column) {
		field.setText((String) value);
		return field;
	}

	@Override
	public Object getCellEditorValue() {
		return field.getText();
	}

}
package platypus.components.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 * A table cell renderer with customizable text alignment.
 * 
 * @author Jingchen Xu
 */
public class CustomStringRenderer implements TableCellRenderer {

    private static final Color UNSELECTED_COLOR = UIManager.getColor("Table.background");
    private static final Color SELECTED_COLOR = UIManager.getColor("Table.selectionBackground");
    private static final Color FOCUS_BORDER_COLOR = SELECTED_COLOR.darker();
    private static final Font LABEL_FONT = UIManager.getFont("Table.font");

    private JLabel label; // display component

    /**
     * Constructs a cell renderer with the specified alignment.
     * 
     * @param alignment
     */
    public CustomStringRenderer(int alignment) {
        super();

        this.label = new JLabel();

        label.setOpaque(true);
        label.setHorizontalAlignment(alignment);
        label.setFont(LABEL_FONT);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        // content
        if (value instanceof String)
            label.setText((String) value);
        else
            label.setText("");

        label.setOpaque(true);
        label.setBackground(isSelected ? SELECTED_COLOR : UNSELECTED_COLOR);
        label.setBorder(BorderFactory.createLineBorder(FOCUS_BORDER_COLOR, hasFocus ? 1 : 0));

        return label;
    }

}

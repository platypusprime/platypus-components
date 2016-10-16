package platypus.components;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * Provides utility methods for Swing components.
 * 
 * @author Jingchen Xu
 *
 */
public class PComponentUtils {

    private PComponentUtils() {}

    /**
     * Enables undo/redo functionality for a component. The specified Document
     * will undo or redo the latest change when CTRL-Z or CTRL-Y is pressed
     * while the component is in focus.
     * 
     * @param doc the document to track changes on
     * @param component the component associated with doc. Keyboard shortcuts
     *            will trigger while this component is in focus.
     * @return the UndoManager that is added to doc
     */
    public static UndoManager enableUndoManagement(Document doc,
            JComponent component) {

        final UndoManager manager = new UndoManager();
        doc.addUndoableEditListener(manager);

        // map undo to CTRL-Z
        Action undoAction = new AbstractAction() {
            private static final long serialVersionUID = 8573395919242317325L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    manager.undo();
                } catch (CannotUndoException e) {
                    // TODO play alert sound
                }
            }
        };
        component.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                InputEvent.CTRL_DOWN_MASK), undoAction);

        // map redo to CTRL-Y
        Action redoAction = new AbstractAction() {
            private static final long serialVersionUID = 8340075601425828378L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    manager.redo();
                } catch (CannotRedoException e) {
                    // TODO play alert sound
                }
            }
        };
        component.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
                InputEvent.CTRL_DOWN_MASK), redoAction);

        return manager;
    }

}

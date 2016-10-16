package platypus.components.combo;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import sl.shapes.RoundPolygon;
import sl.shapes.StarPolygon;

/**
 * A rating GUI using stars to represent score. String labels can be
 * assigned to individual scores.
 * 
 * @author Jingchen Xu
 */
public class PRatingBox extends JPanel {

    private static final long serialVersionUID = -5761248500062226827L;

    private int value = -1;
    private Star[] stars;

    private String[] strings;
    private JLabel label;

    /**
     * Creates a rating box with blank labels.
     * 
     * @param n the number of stars
     * @param starRadius the size of the stars
     */
    public PRatingBox(int n, int starRadius) {

        setLayout(new GridBagLayout());

        // create and add stars
        stars = new Star[n];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(starRadius, i, this);
            stars[i].setSelected(false);

            GridBagConstraints c = new GridBagConstraints(i, 0, 1, 1, 0, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0);
            add(stars[i], c);
        }

        // add the score string label
        label = new JLabel("");
        GridBagConstraints c = new GridBagConstraints(0, 1, n, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0);
        add(label, c);

    }

    /**
     * Creates a rating box with rating labels.
     * 
     * @param strings the rating titles, in ascending order
     * @param starRadius the size of the stars
     */
    public PRatingBox(String[] strings, int starRadius) {
        this(strings.length, starRadius);
        this.strings = strings;
    }

    /**
     * Adds an <code>ActionListener</code> to the rating box.
     * 
     * @param listener the <code>ActionListener</code> to be added
     */
    public void addActionListener(ActionListener listener) {

        for (Star star : stars)
            star.addActionListener(listener);
    }

    /**
     * Changes the size of the stars.
     * 
     * @param radius the new size of the stars
     */
    public void setRadii(int radius) {

        for (Star star : stars)
            star.setRadius(radius);
        ((Window) SwingUtilities.getRoot(this)).pack();
    }

    /**
     * Returns the rating stored in the rating box.
     * 
     * @return the stored rating
     */
    public int getValue() {
        return value + 1;
    }

    /**
     * Changes both the GUI and internal value to reflect index change. Called
     * on click.
     * 
     * @param index the index of the star clicked
     */
    public void clickStar(int index) {

        if (value != index) {
            hoverStar(index);
            value = index;
        } else {
            hoverStar(-1);
            value = -1;
        }
    }

    /**
     * Changes the GUI to reflect index change. Called on hover.
     * 
     * @param index the index of the star hovered over
     */
    public void hoverStar(int index) {

        for (int i = 0; i < stars.length; i++)
            stars[i].setSelected(stars[i].getIndex() <= index);

        if (index >= 0 && strings != null && strings[index] != null)
            label.setText(strings[index]);
        else
            label.setText("");

        repaint();
    }

    /**
     * Changes the GUI to reflect the stored index. Called upon mouse leaving
     * the component.
     */
    public void hoverStar() {
        hoverStar(value);
    }
}

/**
 * A single star for the <code>PRatingBox</code> UI.
 */
class Star extends JComponent {

    private static final long serialVersionUID = -7699989916503850489L;

    private final PRatingBox container;
    private final int index;

    private RoundPolygon shape;
    private Dimension size;

    private boolean isSelected;
    private boolean mouseEntered = false;
    private boolean mousePressed = false;

    private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();

    /**
     * Creates a star for a rating box.
     * 
     * @param radius the radius of the star in pixels
     * @param index the position of the star in the container
     * @param container the rating box this star is assigned to
     */
    public Star(int radius, int index, PRatingBox container) {
        super();

        this.index = index;
        setRadius(radius);
        this.container = container;

        setFocusable(true);

        // enableInputMethods(true);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseEntered = true;
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                Star.this.container.hoverStar(getIndex());
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseEntered = false;
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Star.this.container.hoverStar();
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
                Star.this.container.clickStar(getIndex());
                notifyListeners(e);
                Star.this.container.repaint();
            }

        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // paint fill
        g2d.setColor(isSelected() ? Color.GRAY : Color.WHITE);
        g2d.fill(shape);

        // paint outline
        if (mousePressed)
            g2d.setColor(Color.YELLOW);
        else if (mouseEntered)
            g2d.setColor(Color.LIGHT_GRAY);
        else
            g2d.setColor(Color.BLACK);
        g2d.draw(shape);

    }

    /**
     * Getter for this star's index.
     * 
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Getter for this star's rating box.
     * 
     * @return the rating box
     */
    public PRatingBox getBox() {
        return container;
    }

    /**
     * Adds an action listener to this component.
     * 
     * @param listener the listener to add
     */
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(MouseEvent e) {

        ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                new String(), e.getWhen(), e.getModifiers());

        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++)
                listeners.get(i).actionPerformed(evt);
        }
    }

    /**
     * Setter for this star's radius
     * 
     * @param radius the new radius, in pixels
     */
    public void setRadius(int radius) {

        size = new Dimension(2 * radius, 2 * radius);
        shape = new RoundPolygon(new StarPolygon(radius,
                radius, radius, radius / 2, 5, Math.PI * 3 / 2), 2);

        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    /**
     * Returns this star's selection state.
     * 
     * @return the selection state
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Sets this star's selection state
     * 
     * @param selected the new selection state
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    @Override
    public boolean contains(Point p) {
        return shape.contains(p);
    }
}

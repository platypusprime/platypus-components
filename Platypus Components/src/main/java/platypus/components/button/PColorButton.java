package platypus.components.button;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;

/**
 * A simple solid-color rounded-rectangular button which changes color on user
 * interaction. Colors and fade behavior can be specified.
 * 
 * @author Jingchen Xu
 */
public class PColorButton extends JComponent {

	private static final long serialVersionUID = -8798142451874096816L;

	// appearance configuration fields
	private Dimension size;
	private Color idleColor, mouseOverColor, mouseDownColor;

	// animation fields
	private int[] delta;
	private int fadeSteps;
	private long fadeInterval;
	private Thread animThread;

	// state fields
	private boolean mouseEntered = false;
	private boolean mousePressed = false;
	private boolean toggled = false;

	// functionality fields
	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
	private String actionCommand;

	/**
	 * Creates a light gray 50x50 button with no fade animation.
	 */
	public PColorButton() {
		this(new Dimension(50, 50), Color.LIGHT_GRAY);
	}

	/**
	 * Creates a static button with specified size and color.
	 * 
	 * @param size
	 *            button size
	 * @param color
	 *            button color
	 */
	public PColorButton(Dimension size, Color color) {
		this(size, 1, 0, color, null);
	}

	/**
	 * Creates a button of specified size and color which fades from one color
	 * to another when the cursor hovers over it. Fade animation occurs
	 * instantly when the button is clicked.
	 * 
	 * @param size
	 *            button size
	 * @param steps
	 *            granularity of fade
	 * @param interval
	 *            total time for fade, in ms
	 * @param idleColor
	 *            button color when not being interacted with
	 * @param mouseOverColor
	 *            button color when pointer is over it
	 */
	public PColorButton(Dimension size, int steps, int interval,
			Color idleColor,
			Color mouseOverColor) {
		this(size, steps, interval, idleColor, mouseOverColor, null);
	}

	/**
	 * Creates a button of specified size and color which fades from one color
	 * to another when the cursor hovers over it and changes color when clicked.
	 * Fade animation occurs instantly when the button is clicked.
	 * 
	 * @param size
	 *            button size
	 * @param steps
	 *            granularity of fade
	 * @param interval
	 *            total time for fade, in ms
	 * @param idleColor
	 *            button color when not being interacted with
	 * @param mouseOverColor
	 *            button color when pointer is over it
	 * @param mouseDownColor
	 *            button color when mouse is pressed on the button
	 */
	public PColorButton(Dimension size, int steps, long interval,
			Color idleColor,
			Color mouseOverColor, Color mouseDownColor) {
		super();

		this.size = size;
		setColors(idleColor, mouseOverColor, mouseDownColor);
		fadeSteps = steps;
		fadeInterval = interval;
		delta = new int[3];
		resetDeltas();

		enableInputMethods(true);
		setFocusable(true);
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				mouseEntered = true;
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				repaint();

				if (PColorButton.this.mouseOverColor != null) {
					// start mouse-over animation
					animThread = new Thread() {

						@Override
						public void run() {
							for (int i = 1; i <= fadeSteps; i++) {
								try {
									Thread.sleep(fadeInterval / fadeSteps);
								} catch (InterruptedException e) {
									return; // stop the animation
								}
								PColorButton.this.calculateDeltas(i);
								PColorButton.this.repaint();
							}
						}
					};
					animThread.start();
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				mouseEntered = false;

				if (animThread != null) // stop animation if it exists
					animThread.interrupt();
				resetDeltas(); // reset fade

				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
				toggled = !toggled; // toggle state

				if (animThread != null) // stop animation if it exists
					animThread.interrupt();
				calculateDeltas(fadeSteps); // bring fade to completion

				notifyListeners(e);
				repaint();
			}
		});

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// calculate fill color
		Color c;
		if (mousePressed && mouseDownColor != null)
			c = mouseDownColor;
		else if (mousePressed && mouseOverColor != null)
			c = mouseOverColor;
		else if (mouseEntered)
			c = new Color(idleColor.getRed() + delta[0],
					idleColor.getGreen() + delta[1],
					idleColor.getBlue() + delta[2]);
		else
			c = idleColor;

		// button fill
		g.setColor(c);
		g.fillRoundRect(0, 0, size.width - 1, size.height - 1,
				(int) Math.sqrt(size.width), (int) Math.sqrt(size.height));

		// button outline
		g.setColor(idleColor.darker());
		g.drawRoundRect(0, 0, size.width - 1, size.height - 1,
				(int) Math.sqrt(size.width), (int) Math.sqrt(size.height));

	}

	private void calculateDeltas(int i) {
		if (mouseOverColor != null) {
			delta[0] = (mouseOverColor.getRed() - idleColor.getRed())
					* i / fadeSteps;
			delta[1] = (mouseOverColor.getGreen() - idleColor.getGreen())
					* i / fadeSteps;
			delta[2] = (mouseOverColor.getBlue() - idleColor.getBlue())
					* i / fadeSteps;
		}
	}

	private void resetDeltas() {
		delta[0] = 0;
		delta[1] = 0;
		delta[2] = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#setSize(java.awt.Dimension)
	 */
	@Override
	public void setSize(Dimension size) {
		this.size = size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#getSize()
	 */
	@Override
	public Dimension getSize() {
		return new Dimension(size.width, size.height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		return getSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getMinimumSize()
	 */
	@Override
	public Dimension getMinimumSize() {
		return getSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getMaximumSize()
	 */
	@Override
	public Dimension getMaximumSize() {
		return getSize();
	}

	/**
	 * Changes the fade behavior of the button.
	 * 
	 * @param steps
	 *            granularity of fade
	 * @param interval
	 *            total time for fade, in ms
	 */
	public void setFadeBehavior(int steps, long interval) {
		this.fadeSteps = steps;
		this.fadeInterval = interval;
	}

	/**
	 * Changes the color scheme of the button. Idle color must be non-null, but
	 * the other arguments may be null.
	 * 
	 * @param idleColor
	 *            the default color of the button
	 * @param mouseOverColor
	 *            the color of the button if the cursor hovers over it
	 * @param mouseDownColor
	 *            the color of the button while it is being clicked
	 */
	public void setColors(Color idleColor, Color mouseOverColor,
			Color mouseDownColor) {

		if (idleColor == null)
			throw new NullPointerException("idleColor cannot be null");

		this.idleColor = idleColor;
		this.mouseOverColor = mouseOverColor;
		this.mouseDownColor = mouseDownColor;
		repaint();
	}

	/**
	 * Returns whether the button has been toggled. The button's toggle state is
	 * flipped each time it is clicked.
	 * 
	 * @return the button's toggle state
	 */
	public final boolean isToggled() {
		return toggled;
	}

	/**
	 * Sets the toggle state of the button.
	 * 
	 * @param toggled
	 *            the new toggled state
	 */
	public final void setToggled(boolean toggled) {
		this.toggled = toggled;
		repaint();
	}

	/**
	 * Sets the <code>ActionCommand</code> for this button
	 * 
	 * @param command
	 *            the <code>ActionCommand</code> to be set
	 */
	public final void setActionCommand(String command) {
		actionCommand = command;
	}

	/**
	 * Returns the <code>ActionCommand</code> of this button.
	 * 
	 * @return the <code>ActionCommand</code> of this button
	 */
	public final String getActionCommand() {
		return actionCommand == null ? "" : actionCommand;
	}

	/**
	 * Adds an {@code ActionListener} to the button.
	 * 
	 * @param listener
	 *            the {@code ActionListener} to be added
	 */
	public final void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	private void notifyListeners(MouseEvent e) {

		// generate new event
		ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
				getActionCommand(), e.getWhen(), e.getModifiers());

		// send it off to all the listeners
		synchronized (listeners) {
			for (int i = 0; i < listeners.size(); i++) {
				ActionListener tmp = listeners.get(i);
				tmp.actionPerformed(evt);
			}
		}
	}

}

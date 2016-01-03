// http://www.mkyong.com/java/how-to-resize-an-image-in-java/
// http://stackoverflow.com/questions/19506927/how-to-get-scaled-instance-of-a-bufferedimage
// https://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
// http://www.codebeach.com/2008/02/using-custom-cursors-in-java.html

package platypus.components.image;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * An image display with zoom and pan functionalities. Zoom and pan can be
 * controlled using the scroll wheel and mouse, respectively.
 * 
 * @author Jingchen Xu
 */
public class PImagePane extends JPanel {

	private static final long serialVersionUID = 7723289694309037850L;

	private BufferedImage img;

	private Object interpolationMode = RenderingHints.VALUE_INTERPOLATION_BILINEAR;

	private int xOffset, yOffset; // offset from center
	private int dragStartX, dragStartY;

	// scaling factor
	private double scale;
	private double minScale;

	/**
	 * Creates a blank image pane.
	 */
	public PImagePane() {
		super();

		MouseAdapter mouseAdapter = new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				// change cursor to drag cursor if appropriate
				if (scale > minScale)
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// reset cursor
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// record drag starting coords
				dragStartX = e.getX();
				dragStartY = e.getY();
			}

			@Override
			public void mouseDragged(MouseEvent e) {

				// calculate drag delta
				xOffset += e.getX() - dragStartX;
				yOffset += e.getY() - dragStartY;

				constrainToLimits();
				repaint();

				// reset drag starting coords
				dragStartX = e.getX();
				dragStartY = e.getY();
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {

				if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

					double prevScale = scale;

					scale += -.05 * e.getWheelRotation();
					constrainToLimits();

					// scale offset
					xOffset = (int) (xOffset * scale / prevScale);
					yOffset = (int) (yOffset * scale / prevScale);

					repaint();
				}
			}
		};

		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		addMouseWheelListener(mouseAdapter);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				constrainToLimits();
			}
		});
	}

	/**
	 * Changes the image to be displayed on the pane. The image is scaled to fit
	 * the pane.
	 * 
	 * @param newImg the image to be loaded
	 */
	public void setImage(BufferedImage newImg) {

		img = newImg;

		// reset offsets
		xOffset = 0;
		yOffset = 0;

		if (newImg != null) {
			// scale to fit window
			double xFitScale = (double) getWidth() / (double) img.getWidth();
			double yFitScale = (double) getHeight() / (double) img.getHeight();
			scale = Math.min(xFitScale, yFitScale);	// choose the smaller scale
			minScale = Math.min(scale, 1.0);	// set minimum scale
		}

		repaint();
	}

	/**
	 * Loads a new image from a file onto the pane. The image is scaled to fit
	 * the pane.
	 * 
	 * @param f the File representing the image file
	 */
	public void setImage(File f) {
		try {
			BufferedImage bi = ImageIO.read(f);
			setImage(bi);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Changes the location of a zoomed image with respect to the panel.
	 * 
	 * @param x horizontal offset from center of image and center of pane
	 * @param y vertical offset from center of image and center of pane
	 */
	public void setOffset(int x, int y) {
		this.xOffset = x;
		this.yOffset = y;
		constrainToLimits();
		repaint();
	}

	/**
	 * Changes the zoom factor of the displayed image. If an illegal zoom factor
	 * is given, the closest legal value is set instead.
	 * 
	 * @param scale the new zoom factor
	 */
	public void setScale(double scale) {
		this.scale = scale;
		constrainToLimits();
		repaint();
	}

	private void constrainToLimits() {

		if (img != null) {
			// update minimum scale
			double xFitScale = (double) getWidth() / (double) img.getWidth();
			double yFitScale = (double) getHeight() / (double) img.getHeight();
			minScale = Math.min(Math.min(xFitScale, yFitScale), 1.0);
			// apply scale limits
			scale = Math.max(scale, minScale);

			// apply offset limits
			double xlim = ((img.getWidth() * scale) - (double) getWidth()) / 2;
			double ylim = ((img.getHeight() * scale) - (double) getHeight()) / 2;

			// check left/right edges
			if (xlim > 0 && xOffset > xlim)
				xOffset = (int) (xlim);
			else if (xlim > 0 && xOffset < -xlim)
				xOffset = (int) (-xlim);
			else if (xlim <= 0)
				xOffset = 0;

			// check top/bottom edges
			if (ylim > 0 && yOffset > ylim)
				yOffset = (int) ylim;
			else if (ylim > 0 && yOffset < -ylim)
				yOffset = (int) (-ylim);
			else if (ylim <= 0)
				yOffset = 0;

			// change cursor
			if (scale == minScale)
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			else
				// TODO check that mouse is inside pane
				setCursor(new Cursor(Cursor.MOVE_CURSOR));

		} else {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

	}

	/**
	 * Sets the interpolation hint value. Allowable values for this are:
	 * <ul>
	 * <li>VALUE_INTERPOLATION_NEAREST_NEIGHBOR (preserve hard edges)</li>
	 * <li>VALUE_INTERPOLATION_BILINEAR (performance)</li>
	 * <li>VALUE_INTERPOLATION_BICUBIC (quality)</li>
	 * </ul>
	 * 
	 * @param mode the interpolation hint value to use
	 */
	public void setInterpolationMode(Object mode) {

		if (mode != RenderingHints.VALUE_INTERPOLATION_BICUBIC
				&& mode != RenderingHints.VALUE_INTERPOLATION_BILINEAR
				&& mode != RenderingHints.VALUE_INTERPOLATION_BILINEAR)
			throw new IllegalArgumentException("Invalid interpolation hint");

		interpolationMode = mode;
	}

	@Override
	public void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;

		// paint background the default color
		g.setColor(UIManager.getColor("Panel.background"));
		g.fillRect(0, 0, this.getSize().width, this.getSize().height);

		// paint image
		if (img != null) {

			int windowWidth = getWidth();
			int windowHeight = getHeight();

			int scaledWidth = (int) (img.getWidth() * scale);
			int scaledHeight = (int) (img.getHeight() * scale);

			int totalXOffset = (windowWidth - scaledWidth) / 2 + xOffset;
			int totalYOffset = (windowHeight - scaledHeight) / 2 + yOffset;

			// BILINEAR FOR PERFORMANCE, BICUBIC FOR QUALITY
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					interpolationMode);

			g2.drawImage(img, totalXOffset, totalYOffset, scaledWidth,
					scaledHeight, null);
		}

	}

}

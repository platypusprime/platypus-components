package platypus.components.image;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * An <code>ImageIcon</code> with adjustable bounds. The image
 * contained will resize with the bounds while maintaining its
 * original aspect ratio.
 * 
 * @deprecated Please use <code>PImagePane</code> instead
 * @author Jingchen Xu
 */
public class PResizableIcon extends ImageIcon {

	private static final long serialVersionUID = -6528821016151807496L;

	private int width, height;

	public PResizableIcon(String fileName) {
		super(fileName);
	}

	public PResizableIcon(String fileName, int w, int h) {
		this(fileName);
		setSize(w, h);
	}

	public PResizableIcon(String fileName, Dimension d) {
		this(fileName);
		setSize(d);
	}

	public PResizableIcon(URL url) {
		super(url);
	}

	public PResizableIcon(URL url, int w, int h) {
		this(url);
		setSize(w, h);
	}

	public PResizableIcon(URL url, Dimension d) {
		this(url);
		setSize(d);
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (getImage() != null) {
			g.drawImage(getImage(), x, y, getIconWidth(), getIconHeight(), c);
		}
	}

	public void setSize(int w, int h) {
		width = w;
		height = h;
	}

	public void setSize(Dimension d) {
		width = d.width;
		height = d.height;
	}

	public int getIconWidth() {
		return width;
	}

	public int getIconHeight() {
		return height;
	}
}

package platypus.components.button;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;

/**
 * A button which displays an icon over a single-color background. The icon can
 * be set to be static or toggled after each click.
 * <p>
 * When using the color-fade feature, it is recommended that icons with
 * transparent backgrounds are used.
 * 
 * @author Jingchen Xu
 */
public class PImageButton extends PColorButton {

    private static final long serialVersionUID = -6764200541346159986L;

    private ImageIcon defaultIcon, toggledIcon;
    private int buffer;

    /**
     * Creates a button with a static icon and no buffer.
     * 
     * @param iconPath the file path of the icon
     */
    public PImageButton(String iconPath) {
        this(iconPath, 0);
    }

    /**
     * Creates a button with a static icon.
     * 
     * @param iconPath the file path of the icon
     * @param buffer the size of the buffer around the icon
     */
    public PImageButton(String iconPath, int buffer) {
        super();

        defaultIcon = new ImageIcon(getClass().getResource(iconPath));
        toggledIcon = null;
        this.buffer = buffer;
        setSize(new Dimension(defaultIcon.getIconWidth() + 2 * buffer,
                defaultIcon.getIconHeight() + 2 * buffer));
    }

    /**
     * Creates a button that toggles its icon on click. Preferably, the two
     * icons should match in size. The size of the button will be determined by
     * the first icon.
     * 
     * @param iconPath1 the file path to the default icon
     * @param iconPath2 the file path to the secondary icon.
     * @param buffer the size of the buffer around the icon.
     */
    public PImageButton(String iconPath1, String iconPath2, int buffer) {
        this(iconPath1, buffer);

        toggledIcon = new ImageIcon(getClass().getResource(iconPath2));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isToggled() && toggledIcon != null)
            g.drawImage(toggledIcon.getImage(), buffer, buffer, new Color(0, 0, 0, 0), null);
        else
            g.drawImage(defaultIcon.getImage(), buffer, buffer, new Color(0, 0, 0, 0), null);
    }

}

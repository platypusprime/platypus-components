// http://stackoverflow.com/questions/13948122/drawing-a-bounding-rectangle-to-select-what-area-to-record

package platypus.components.modal;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * A component which allows a user to "snip" a rectangular region of their
 * display and retrieve its coordinates/dimensions.
 *
 * @author Jingchen Xu
 */
public class PSnipper {

	/**
	 * Shows a modal snipper window.
	 * 
	 * @return a rectangle encapsulating the snip
	 */
	public static Rectangle snip() {

		final Object lock = new Object();
		PSnipper snip = new PSnipper(lock);

		// block until snip is complete
		Thread t = new Thread() {
			@Override
			public void run() {
				synchronized (lock) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// retrieve and return result
		Rectangle result = snip.getResult();
		return result;
	}

	private SnipItPane snipItPane;

	private PSnipper(final Object lock) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				// make frame to cover display(s)
				JFrame frame = new JFrame();
				frame.setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setBounds(getVirtualBounds());

				// make frame transparent
				frame.setUndecorated(true);
				//AWTUtilities.setWindowOpaque(frame, false);
				frame.setBackground(new Color(0, 0, 0, 0));

				// create interface layer
				snipItPane = new SnipItPane(lock);
				frame.add(snipItPane);

				// show frame
				frame.setVisible(true);
			}
		});
	}

	private static Rectangle getVirtualBounds() {

		Rectangle bounds = new Rectangle(0, 0, 0, 0);

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice lstGDs[] = ge.getScreenDevices();
		for (GraphicsDevice gd : lstGDs)
			bounds.add(gd.getDefaultConfiguration().getBounds());

		return bounds;
	}

	private Rectangle getResult() {
		return snipItPane.getValue();
	}

	private class SnipItPane extends JPanel {

		private static final long serialVersionUID = -5735850600016967885L;

		private SelectionPane selectionPane;

		private Point mouseAnchor;
		private Rectangle value;

		public SnipItPane(Object lock) {

			setOpaque(false);
			setLayout(null);
			selectionPane = new SelectionPane(lock);
			add(selectionPane);

			MouseAdapter adapter = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					mouseAnchor = e.getPoint();
					selectionPane.setLocation(mouseAnchor);
					selectionPane.setSize(0, 0);
				}

				@Override
				public void mouseDragged(MouseEvent e) {

					// update mouse position
					Point dragPoint = e.getPoint();

					// calculate rectangle properties
					int x = mouseAnchor.x;
					int y = mouseAnchor.y;
					int width = dragPoint.x - mouseAnchor.x;
					int height = dragPoint.y - mouseAnchor.y;

					// handle flips
					if (width < 0) {
						x = dragPoint.x;
						width *= -1;
					}
					if (height < 0) {
						y = dragPoint.y;
						height *= -1;
					}

					// update on-screen rectangle
					selectionPane.setBounds(x, y, width, height);
					value = new Rectangle(x, y, width, height);
					selectionPane.revalidate();
					repaint();
				}
			};
			addMouseListener(adapter);
			addMouseMotionListener(adapter);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g.create();

			Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
			Area area = new Area(bounds);
			area.subtract(new Area(selectionPane.getBounds()));

			g2d.setColor(new Color(192, 192, 192, 64));
			g2d.fill(area);

		}

		public Rectangle getValue() {
			return value;
		}
	}

	private class SelectionPane extends JPanel {

		private static final long serialVersionUID = 3143890758520799569L;

		private JButton button;
		private JLabel label;

		public SelectionPane(final Object lock) {
			button = new JButton("OK");
			setOpaque(false);

			label = new JLabel("Rectangle");
			label.setOpaque(true);
			label.setBorder(new EmptyBorder(4, 4, 4, 4));
			label.setBackground(Color.GRAY);
			label.setForeground(Color.WHITE);
			setLayout(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			add(label, gbc);

			gbc.gridy++;
			add(button, gbc);

			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					synchronized (lock) {
						lock.notify();
					}
					// dispose of parent
					SwingUtilities.getWindowAncestor(SelectionPane.this)
							.dispose();
				}
			});
			button.setMargin(new Insets(2, 5, 2, 5));

			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					if (getWidth() < 100 || getHeight() < 30)
						label.setVisible(false);
					else {
						label.setText(String.format("(%d, %d, %d, %d)",
								getX(), getY(), getWidth(), getHeight()));
						label.setVisible(true);
					}
				}
			});

		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();

			float dash1[] = { 10.0f };
			BasicStroke dashed = new BasicStroke(3.0f, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
			g2d.setColor(Color.BLACK);
			g2d.setStroke(dashed);
			g2d.drawRect(0, 0, getWidth() - 3, getHeight() - 3);
			g2d.dispose();
		}
	}
}

package platypus.components.modal;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A generic modal input dialog pop-up. Aside from basic OK and Cancel buttons,
 * its interface must be entirely specified by its implementation.
 *
 * @author Jingchen Xu
 */
public abstract class PAbstractBlockingDialog<T> {

	/**
	 * Returns the custom input UI to be used for the dialog. This will be
	 * placed in the Center position of the dialog's BorderLayout, above the OK
	 * and Cancel buttons.
	 * 
	 * @return the input UI to be used for the dialog
	 */
	public abstract JPanel getInputPane();

	/**
	 * Retrieves output data from the current UI state.
	 * 
	 * @return the dialog output data
	 */
	public abstract T getOutput();

	/**
	 * Shows the dialog and blocks until it is completed or canceled/closed.
	 * 
	 * @param owner the owner of the dialog. Set to null for no modality
	 * @param title title of the dialog
	 * @return user input from the dialog
	 */
	public T show(final JFrame owner, String title) {

		final Object lock = new Object();
		final Object[] output = new Object[1];

		// set up dialog
		final JDialog dialog = new JDialog(owner, title);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setModal(true);

		// use window listener to unblock thread on manual close
		dialog.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				synchronized (lock) {
					lock.notify();
				}
			}
		});

		dialog.add(getInputPane(), BorderLayout.CENTER);

		// create and add button bar
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 3)); // buffer
		buttonPane.add(Box.createHorizontalGlue());	// right-align buttons

		// store user input on 'OK' press
		JButton okButton = new JButton("OK");
		buttonPane.add(okButton);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO save user input
				output[0] = getOutput();

				// kill dialog
				dialog.setVisible(false);
				dialog.dispose();

				// unblock thread
				synchronized (lock) {
					lock.notify();
				}
			}
		});

		// separate buttons with 3px gap
		buttonPane.add(Box.createHorizontalStrut(3));

		// close window without storing input on 'Cancel' press
		JButton cnclButton = new JButton("Cancel");
		buttonPane.add(cnclButton);
		cnclButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// kill dialog
				dialog.setVisible(false);
				dialog.dispose();

				// unblock thread
				synchronized (lock) {
					lock.notify();
				}
			}
		});

		// add panel to bottom of dialog
		dialog.add(buttonPane, BorderLayout.PAGE_END);

		// render dialog on the EDT
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dialog.pack();
				dialog.setLocationRelativeTo(owner);
				dialog.setVisible(true);

			}
		});

		block(lock); // block until window is closed

		@SuppressWarnings("unchecked")
		T typedOutput = (T) output[0];
		return typedOutput;
	}

	private static void block(final Object lock) {

		// create and start a thread that puts the lock on wait
		Thread blockThread = new Thread() {
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
		blockThread.start();

		// wait for outside code to notify the lock
		try {
			blockThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

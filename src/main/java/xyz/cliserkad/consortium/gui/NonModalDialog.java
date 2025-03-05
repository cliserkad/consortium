package xyz.cliserkad.consortium.gui;

import javax.swing.*;
import java.util.concurrent.FutureTask;

/** blocks the calling thread until the dialog is closed */
public class NonModalDialog {

	public enum BaseOption {
		CANCEL
	}

	/**
	 * Shows a dialog with the given message and options.
	 * The dialog is non-modal; other gui elements can be interacted with while the dialog is open.
	 *
	 * @param options the options to show
	 * @param message the message to show
	 * @return a FutureTask with the selected option or null if the dialog was closed without selecting an option
	 */
	public static <Option extends Enum<Option>> FutureTask<Option> showDialog(final Class<Option> options, final String message) {
		JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, options.getEnumConstants(), options.getEnumConstants()[0]);

		pane.setComponentOrientation(JOptionPane.getRootFrame().getComponentOrientation());
		JDialog dialog = pane.createDialog(JOptionPane.getRootFrame(), message);

		FutureTask<Option> output = new FutureTask<>(() -> {
			Object selectedValue = pane.getValue();
			if(selectedValue == null || selectedValue == JOptionPane.UNINITIALIZED_VALUE) {
				return null;
			} else {
				for(Option option : options.getEnumConstants()) {
					if(option == selectedValue) {
						return option;
					}
				}
			}
			throw new IllegalStateException("Unexpected value: " + selectedValue);
		});
		pane.addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, ignored -> {
			output.run();
		});

		dialog.setModal(false);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.pack();
		dialog.setLocationRelativeTo(JOptionPane.getRootFrame());
		dialog.setVisible(true);
		dialog.requestFocus();

		return output;
	}

}

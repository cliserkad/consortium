package xyz.cliserkad.consortium.gui;

import xyz.cliserkad.consortium.Main;

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/** blocks the calling thread until the dialog is closed */
public class NonModalDialog {

	private JOptionPane pane;
	private JDialog dialog;

	public enum ConfirmCanel {

		CONFIRM,
		CANCEL;

		public String toString() {
			return Main.prettifyEnumName(name());
		}

	}

	public enum YesNo {

		YES,
		NO;

		public String toString() {
			return Main.prettifyEnumName(name());
		}

	}

	public static <T> FutureTask<T> showDialog(final Object content, final String message, final T... options) {
		return new NonModalDialog().showDialog1(content, message, options);
	}

	public static <T> FutureTask<T> showDialog(final String message, final T... options) {
		return new NonModalDialog().showDialog1(message, message, options);
	}

	private <T> FutureTask<T> showDialog1(final Object content, final String message, final T... options) {
		return genericDialog(() -> {
			final Object selectedValue = pane.getValue();
			if(selectedValue == null || selectedValue == JOptionPane.UNINITIALIZED_VALUE) {
				return null;
			} else {
				return (T) selectedValue;
			}
		}, content, message, options);
	}

	public static FutureTask<Boolean> showConfirmationDialog(final Object content, final String message) {
		return new NonModalDialog().showConfirmCancelDialog1(content, message);
	}

	public static FutureTask<Boolean> showConfirmationDialog(final String message) {
		return new NonModalDialog().showConfirmCancelDialog1(message, message);
	}

	private FutureTask<Boolean> showConfirmCancelDialog1(final Object content, final String message) {
		return genericDialog(() -> {
			final Object selectedValue = pane.getValue();
			if(selectedValue == null || selectedValue == JOptionPane.UNINITIALIZED_VALUE) {
				return false;
			} else {
				return selectedValue == ConfirmCanel.CONFIRM;
			}
		}, content, message, ConfirmCanel.class.getEnumConstants());
	}

	public static FutureTask<Boolean> showYesNoDialog(final Object content, final String message) {
		return new NonModalDialog().showYesNoDialog1(content, message);
	}

	public static FutureTask<Boolean> showYesNoDialog(final String message) {
		return new NonModalDialog().showYesNoDialog1(message, message);
	}

	private FutureTask<Boolean> showYesNoDialog1(final Object content, final String message) {
		return genericDialog(() -> {
			final Object selectedValue = pane.getValue();
			if(selectedValue == null || selectedValue == JOptionPane.UNINITIALIZED_VALUE) {
				return false;
			} else {
				return selectedValue == YesNo.YES;
			}
		}, content, message, YesNo.class.getEnumConstants());
	}

	private <Return, Option> FutureTask<Return> genericDialog(final Callable<Return> callback, final Object content, final String message, final Option... options) {
		pane = new JOptionPane(content, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, options, options[0]);

		pane.setComponentOrientation(JOptionPane.getRootFrame().getComponentOrientation());
		dialog = pane.createDialog(JOptionPane.getRootFrame(), message);

		FutureTask<Return> output = new FutureTask<>(callback);

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

	private <Return, Option> FutureTask<Return> genericDialog(final Callable<Return> callback, final String message, final Option... options) {
		return genericDialog(callback, message, message, options);
	}

	public static <Option extends Enum<Option>> FutureTask<Option> showDialog(final Object content, final Class<Option> options, final String message) {
		return new NonModalDialog().showDialog1(content, options, message);
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
		return new NonModalDialog().showDialog1(message, options, message);
	}

	private <Option extends Enum<Option>> FutureTask<Option> showDialog1(final Object content, final Class<Option> options, final String message) {
		return genericDialog(() -> {
			final Object selection = pane.getValue();
			if(selection == null || selection == JOptionPane.UNINITIALIZED_VALUE) {
				return null;
			} else {
				for(Option option : options.getEnumConstants()) {
					if(selection == option) {
						return option;
					}
				}
			}
			throw new IllegalStateException("Unexpected value: " + selection);
		}, content, message, options.getEnumConstants());
	}

}

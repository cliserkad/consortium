package xyz.cliserkad.consortium;

import javax.swing.*;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {
	private final JTextArea textArea;

	public TextAreaOutputStream(final JTextArea desintation) {
		this.textArea = desintation;
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() {
	}

	@Override
	public void write(byte[] buffer, int offset, int length) {
		final String text = new String(buffer, offset, length);
		SwingUtilities.invokeLater(() -> textArea.append(text));
	}

	@Override
	public void write(int b) {
		write(new byte[] { (byte) b }, 0, 1);
	}

}

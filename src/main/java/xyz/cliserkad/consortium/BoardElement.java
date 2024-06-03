package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;

public class BoardElement extends JPanel {
	private final BoardPosition position;

	public BoardElement(final BoardPosition position) {
		super(new GridBagLayout());
		this.position = position;

		setBorder(BorderFactory.createLineBorder(Color.black));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 1;
		constraints.gridheight = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;

		JLabel label = new JLabel(position.name());
		label.setBackground(position.color);
		label.setOpaque(true);
		add(label, constraints);
	}

	public BoardPosition getPosition() {
		return position;
	}
}

package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;

public class BoardElement extends JPanel {
	public final BoardPosition position;

	public BoardElement(final BoardPosition position) {
		super(new GridBagLayout());
		this.position = position;
		setBackground(position.color);
		setBorder(BorderFactory.createLineBorder(Color.black));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;

		JLabel label = new JLabel(position.name());
		label.setForeground(Color.BLACK);
		label.setBackground(Color.WHITE);
		label.setOpaque(true);
		add(label, constraints);
	}

	public void addPlayer(Player player) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.SOUTH;

		constraints.gridx = player.playerIndex;
		constraints.gridy = 2;
		add(player, constraints);
		validate();
		repaint();
	}

	public void removePlayer(Player player) {
		remove(player);
		validate();
		repaint();
	}

}

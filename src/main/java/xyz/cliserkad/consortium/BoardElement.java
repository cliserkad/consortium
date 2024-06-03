package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;

public class BoardElement extends JPanel {
	public final BoardPosition position;
	public Player owner;

	private JLabel nameLabel;
	private JLabel ownerLabel;
	private JLabel costLabel;

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

		nameLabel = new JLabel(position.name(), SwingConstants.CENTER);
		nameLabel.setForeground(Color.BLACK);
		nameLabel.setBackground(Color.WHITE);
		nameLabel.setOpaque(true);
		constraints.gridy = 1;
		add(nameLabel, constraints);

		if(position.isOwnable) {
			ownerLabel = new JLabel("NOT OWNED ", SwingConstants.CENTER);
			ownerLabel.setForeground(Color.RED);
			ownerLabel.setBackground(Color.WHITE);
			ownerLabel.setOpaque(true);
			constraints.gridy = 2;
			add(ownerLabel, constraints);

			costLabel = new JLabel("Cost: " + position.cost, SwingConstants.CENTER);
			costLabel.setForeground(Color.BLACK);
			costLabel.setBackground(Color.WHITE);
			costLabel.setOpaque(true);
			constraints.gridy = 3;
			add(costLabel, constraints);
		}
	}

	public boolean setOwner(Player player) {
		if(position.isOwnable) {
			owner = player;
			ownerLabel.setForeground(Color.BLACK);
			ownerLabel.setText(player.getIcon());
			validate();
			repaint();
		}
		return position.isOwnable;
	}

	public void addPlayer(Player player) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.SOUTH;

		constraints.gridx = player.playerIndex;
		constraints.gridy = 4;
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

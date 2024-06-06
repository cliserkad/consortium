package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;

/**
 * Holds game state for each BoardPosition
 */
public class BoardElement extends JPanel {
	public final BoardPosition position;
	public Player owner;
	public int improvementAmt;

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

		if(position.logic instanceof Purchasable purchasable) {
			ownerLabel = new JLabel("NOT OWNED ", SwingConstants.CENTER);
			ownerLabel.setForeground(Color.RED);
			ownerLabel.setBackground(Color.WHITE);
			ownerLabel.setOpaque(true);
			constraints.gridy = 2;
			add(ownerLabel, constraints);

			costLabel = new JLabel("Cost: " + purchasable.cost(), SwingConstants.CENTER);
			costLabel.setForeground(Color.BLACK);
			costLabel.setBackground(Color.WHITE);
			costLabel.setOpaque(true);
			constraints.gridy = 3;
			add(costLabel, constraints);
		}
	}

	public boolean setOwner(Player player) {
		if(position.isPurchasable()) {
			owner = player;
			ownerLabel.setForeground(Color.BLACK);
			ownerLabel.setText(player.getIcon());
			validate();
			repaint();
		}
		return position.isPurchasable();
	}

	public void addPlayer(Player player, Main main) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.SOUTH;

		position.logic.onLand(player, main);

		constraints.gridx = player.playerIndex;
		constraints.gridy = 4;
		add(player, constraints);

		setBackground(position.color);
		validate();
		repaint();
	}

	public void removePlayer(Player player) {
		setBackground(position.color);
		remove(player);
		validate();
		repaint();
	}

}

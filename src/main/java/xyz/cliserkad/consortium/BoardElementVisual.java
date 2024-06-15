package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardElementVisual extends JPanel implements GameStateReceiver {
	private BoardElement element;

	private JLabel nameLabel;
	private JLabel ownerLabel;
	private JLabel costLabel;

	private List<PlayerVisual> playerVisuals = new ArrayList<>();

	public BoardElementVisual(BoardElement element) {
		super(new GridBagLayout());

		this.element = element;

		setBackground(element.position.color);
		setBorder(BorderFactory.createLineBorder(Color.black));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;

		nameLabel = new JLabel(" " + element.position.niceName + " ", SwingConstants.CENTER);
		nameLabel.setForeground(Color.BLACK);
		nameLabel.setBackground(Color.WHITE);
		nameLabel.setOpaque(true);
		constraints.gridy = 1;
		constraints.gridwidth = 4;
		add(nameLabel, constraints);

		if(element.position.logic instanceof Purchasable purchasable) {
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

	public BoardPosition getPosition() {
		return element.position;
	}

	@Override
	public boolean update(GameState gameState) {
		element = gameState.getBoardElement(element.position);

		final int targetComponentCount;
		if(element.position.logic instanceof Purchasable) {
			targetComponentCount = 3;
			if(element.owner != null) {
				ownerLabel.setForeground(Color.BLACK);
				ownerLabel.setText(element.owner.getIcon());
			}
		} else {
			targetComponentCount = 1;
		}
		while(getComponentCount() > targetComponentCount) {
			remove(getComponentCount() - 1);
		}

		for(Player player : gameState.getPlayers()) {
			if(player.getPosition() == element.position) {
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridwidth = 1;
				constraints.gridheight = 1;
				constraints.fill = GridBagConstraints.NONE;
				constraints.anchor = GridBagConstraints.SOUTHWEST;
				constraints.gridx = player.playerIndex;
				constraints.gridy = 4;
				playerVisuals.add(new PlayerVisual(player));
				add(new PlayerVisual(player), constraints);
			}
		}

		validate();
		repaint();
		return true;
	}

}

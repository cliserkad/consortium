package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardElementVisual extends JPanel implements GameStateReceiver {
	public static final String[] IMPROVEMENT_LABELS = {"🔐", "", "🏠", "🏠🏡", "🏠🏡🏠", "🏠🏡🏠🏡", "🏨"};

	private BoardElement element;

	private JLabel nameLabel;
	private JLabel purchaseLabel;
	private JLabel improvementLabel;

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
			purchaseLabel = new JLabel("Cost: " + purchasable.cost(), SwingConstants.CENTER);
			purchaseLabel.setForeground(Color.BLACK);
			purchaseLabel.setBackground(Color.WHITE);
			purchaseLabel.setOpaque(true);
			constraints.gridy = 2;
			add(purchaseLabel, constraints);

			improvementLabel = new JLabel(IMPROVEMENT_LABELS[1], SwingConstants.CENTER);
			improvementLabel.setForeground(Color.BLACK);
			improvementLabel.setBackground(Color.WHITE);
			improvementLabel.setOpaque(true);
			constraints.gridy = 3;
			add(improvementLabel, constraints);
		}
	}

	public String improvementString() {
		if(element.position.logic instanceof Purchasable) {
			return IMPROVEMENT_LABELS[element.improvementAmt + 1];
		} else {
			return "";
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
				purchaseLabel.setText("Owner: " + element.owner.getIcon());
				improvementLabel.setText(improvementString());
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
				constraints.gridy = targetComponentCount + 1;
				playerVisuals.add(new PlayerVisual(player));
				add(new PlayerVisual(player), constraints);
			}
		}

		validate();
		repaint();
		return true;
	}

}

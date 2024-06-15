package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;

public class PlayerVisual extends JPanel implements GameStateReceiver {
	private Player player;

	private final JLabel moneyDisplay;

	public PlayerVisual(Player player) {
		super(new GridBagLayout());
		this.player = player;

		setBorder(BorderFactory.createLineBorder(Color.black));
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;

		JLabel playerIDDisplay = new JLabel("░  " + player.getIcon() + "  ░");
		playerIDDisplay.setForeground(Color.BLACK);
		playerIDDisplay.setBackground(Color.WHITE);
		playerIDDisplay.setOpaque(true);
		playerIDDisplay.setHorizontalTextPosition(SwingConstants.CENTER);
		add(playerIDDisplay, constraints);

		constraints.gridy = 1;
		moneyDisplay = new JLabel("$" + player.getMoney());
		moneyDisplay.setForeground(Color.GREEN);
		moneyDisplay.setBackground(Color.WHITE);
		moneyDisplay.setOpaque(true);
		playerIDDisplay.setHorizontalTextPosition(SwingConstants.CENTER);
		add(moneyDisplay, constraints);
	}

	public int getPlayerIndex() {
		return player.playerIndex;
	}

	@Override
	public boolean update(GameState gameState) {
		player = gameState.getPlayers()[player.playerIndex];
		moneyDisplay.setText("$" + player.getMoney());
		repaint();
		return true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		moneyDisplay.setText("$ " + player.getMoney());
		super.paintComponent(g);
	}

}

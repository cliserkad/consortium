package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Player extends JPanel {
	public static final String[] PLAYER_ICONS = { "🎩", "🐈", "💰", "😎" };

	private static int numPlayers = 0;

	private int money;
	public final int playerIndex;
	private BoardPosition position;
	private List<BoardPosition> propertiesOwned;

	private final JLabel moneyDisplay;

	public Player() {
		super(new GridBagLayout());

		position = BoardPosition.GO;
		propertiesOwned = new ArrayList<>();
		money = 1800;
		playerIndex = numPlayers++;

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

		JLabel playerIDDisplay = new JLabel("Player: " + playerIndex);
		playerIDDisplay.setForeground(Color.BLACK);
		playerIDDisplay.setBackground(Color.WHITE);
		playerIDDisplay.setOpaque(true);
		add(playerIDDisplay, constraints);

		constraints.gridy = 1;
		moneyDisplay = new JLabel("Money: " + getMoney());
		moneyDisplay.setForeground(Color.BLACK);
		moneyDisplay.setBackground(Color.WHITE);
		moneyDisplay.setOpaque(true);
		add(moneyDisplay, constraints);
	}

	@Override
	protected void paintComponent(Graphics g) {
		moneyDisplay.setText("Money: " + getMoney());
		super.paintComponent(g);
	}

	public BoardPosition getPosition() {
		return position;
	}

	/**
	 * Sets the player's position on the board and updates the board elements
	 */
	public void setPosition(BoardPosition position, Main main) {
		for(BoardElement element : main.getBoardElements()) {
			if(element.position == this.position) {
				element.removePlayer(this);
				element.repaint();
			}
		}
		this.position = position;
		for(BoardElement element : main.getBoardElements()) {
			if(element.position == position) {
				element.addPlayer(this, main);
				element.repaint();
			}
		}
		repaint();
	}

	public int getMoney() {
		return money;
	}

	public void addMoney(int amount) {
		money += amount;
		repaint();
	}

	public String getIcon() {
		return PLAYER_ICONS[playerIndex];
	}

}

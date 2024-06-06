package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
	public static final int BOARD_X_SIZE = 11;
	public static final int BOARD_X_SIZE_LESS_ONE = BOARD_X_SIZE - 1;
	public static final int BOARD_Y_SIZE = 11;
	public static final int BOARD_Y_SIZE_LESS_ONE = BOARD_Y_SIZE - 1;
	public static final String WINDOW_TITLE = "Consortium";

	public static final Random RANDOM = new Random();


	private List<Player> players;
	private List<BoardElement> boardElements;

	public Main(final int playerCount) {
		JFrame frame = new JFrame(WINDOW_TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1920, 1080);
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;

		boardElements = new ArrayList<>();
		for(BoardPosition position : BoardPosition.values()) {
			constraints.gridx = position.getBoardCoords().x;
			constraints.gridy = position.getBoardCoords().y;
			final BoardElement boardElement = new BoardElement(position);
			boardElements.add(boardElement);
			panel.add(boardElement, constraints);
		}

		players = new ArrayList<>();
		constraints.gridy = 5;
		for(int i = 0; i < playerCount; i++) {
			players.add(new Player());
			constraints.gridx = 3 + i;
			panel.add(players.get(i), constraints);
		}

		JButton button = new JButton("CLICK TO ADVANCE");
		button.addActionListener(actionEvent -> {
			players.get(0).setPosition(players.get(0).getPosition().next(), this);
		});
		constraints.gridy = 6;
		constraints.gridx = 3;
		panel.add(button, constraints);

		frame.add(panel);
		frame.setVisible(true);

	}

	public List<BoardElement> getBoardElements() {
		return boardElements;
	}

	public static void main(String[] args) throws InterruptedException {
		new Main(1);
	}

}
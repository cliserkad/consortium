package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.Random;

public class Main implements ActionListener {
	public static final int BOARD_X_SIZE = 11;
	public static final int BOARD_X_SIZE_LESS_ONE = BOARD_X_SIZE - 1;
	public static final int BOARD_Y_SIZE = 11;
	public static final int BOARD_Y_SIZE_LESS_ONE = BOARD_Y_SIZE - 1;
	public static final int DICE_MAX = 12;
	public static final int DICE_MIN = 2;
	public static final String WINDOW_TITLE = "Consortium";
	public static final String PROMPT_TURN = "Prompt Turn";
	public static final String VISUALIZE_GAME_STATE = "Visualize Game State";
	public static final Random RANDOM = new Random();

	private final JFrame frame;
	private GameState gameState;

	public Main(final int playerCount) {
		frame = new JFrame(WINDOW_TITLE);
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

		gameState = new GameState(playerCount);

		for(BoardElement element : gameState.getBoardElements()) {
			constraints.gridx = element.position.getBoardCoords().x;
			constraints.gridy = element.position.getBoardCoords().y;
			panel.add(element, constraints);
		}

		constraints.gridy = 1;
		constraints.gridx = 1;
		constraints.gridwidth = 9;
		constraints.gridheight = 5;
		JTextArea printOutput = new JTextArea();
		printOutput.setFont(new Font("Arial Unicode MS", Font.PLAIN, 20));
		printOutput.setEditable(false);
		System.setOut(new PrintStream(new TextAreaOutputStream(printOutput)));
		panel.add(
			new JScrollPane(
				printOutput,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		), constraints);
		constraints.gridwidth = 1;
		constraints.gridheight = 1;

		constraints.gridy = 6;
		for(Player player : gameState.getPlayers()) {
			constraints.gridx = 3 + player.playerIndex;
			panel.add(player, constraints);
		}

		frame.add(panel);
		frame.setVisible(true);

		// Game loop
		Timer timer = new Timer(3500, this);
		timer.setRepeats(true);
		timer.setInitialDelay(5000);
		timer.setActionCommand(PROMPT_TURN);
		timer.start();
	}

	public static void main(String[] args) throws InterruptedException {
		new Main(2);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		gameState.nextTurn();
	}

}
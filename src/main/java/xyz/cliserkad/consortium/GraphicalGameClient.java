package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class GraphicalGameClient implements GameClient {

	public static final String WINDOW_TITLE = "Consortium";

	public static final int MIN_BID = 10;

	private final JFrame frame;
	private final JPanel panel;
	private boolean isInitialized = false;
	private List<BoardElementVisual> boardElementVisuals = new ArrayList<>();
	private Player avatar;

	public GraphicalGameClient() {
		System.err.println("GraphicalGameClient constructor called on Thread" + Thread.currentThread().getId());
		frame = new JFrame(WINDOW_TITLE + " - " + "Player ?");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1920, 1080);
		panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;

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

		update(new GameState(new Player[] { new Player(this) }));

		frame.add(panel);
		frame.setVisible(true);
	}

	public void kill() {
		frame.setVisible(false);
		frame.dispose();
		System.exit(1);
	}

	private void setTitle(int playerIndex) {
		frame.setTitle(WINDOW_TITLE + " - Player " + playerIndex + " " + Player.PLAYER_ICONS[playerIndex]);
	}

	@Override
	public PlayerAction poll(Player avatar, GameState gameState, Class<? extends PlayerAction> prompt) {
		update(gameState);
		setTitle(avatar.playerIndex);
		this.avatar = avatar;
		if(prompt == PurchaseAction.class && avatar.getPosition().logic instanceof Purchasable purchasable) {
			// Show a dialog box asking the player if they want to purchase the property
			final int dialogResult = JOptionPane.showConfirmDialog(frame, "Would you like to purchase " + avatar.getPosition().niceName + " for $" + purchasable.cost() + "?", "Purchase Property", JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION) {
				return new PurchaseAction(avatar.getPosition());
			} else {
				return null;
			}
		} else if(prompt == BidAction.class) {
			// withdraw from bidding automatically if the player doesn't have enough money to bid
			if(gameState.getAuction().bid + MIN_BID > avatar.getMoney()) {
				return null;
			}
			// Show a dialog box asking the player if they want to bid on the property
			final int dialogResult = JOptionPane.showConfirmDialog(frame, "Would you like to bid on " + gameState.getAuction().property.position.niceName + "?", "Bid on Property", JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION) {
				return new BidAction(gameState.getAuction().property.position, gameState.getAuction().bid + MIN_BID);
			} else {
				return null;
			}
		} else {
			System.out.println("Received unrecognized prompt " + prompt.getSimpleName());
			return null;
		}
	}

	@Override
	public void setPlayerID(Integer playerID) {
		setTitle(playerID);
	}

	@Override
	public void sendMessage(String message) {
		System.out.println(message);
	}

	@Override
	public boolean update(GameState gameState) {
		if(!isInitialized) {
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 1;
			constraints.fill = GridBagConstraints.BOTH;
			constraints.anchor = GridBagConstraints.CENTER;
			for(BoardElement element : gameState.getBoardElements()) {
				constraints.gridx = element.position.getBoardCoords().x;
				constraints.gridy = element.position.getBoardCoords().y;
				BoardElementVisual newVisual = new BoardElementVisual(element);
				boardElementVisuals.add(newVisual);
				panel.add(newVisual, constraints);
			}
			isInitialized = true;
		} else {
			for(BoardElementVisual visual : boardElementVisuals) {
				visual.update(gameState);
			}
		}
		frame.repaint();
		return true;
	}

}

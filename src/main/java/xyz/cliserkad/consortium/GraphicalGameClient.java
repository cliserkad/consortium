package xyz.cliserkad.consortium;

import xyz.cliserkad.util.Duo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static xyz.cliserkad.consortium.Main.INITIAL_WINDOW_SIZE_FACTOR;

public class GraphicalGameClient implements GameClient {

	public static final String END_TURN = "End Turn";
	public static final String TRADE = "Trade";
	public static final String DECLARE_BANKRUPTCY = "Declare Bankruptcy";
	public static final String IMPROVE_PROPERTY = "Improve Property";
	public static final String[] END_OF_TURN_ACTIONS = { END_TURN, TRADE, IMPROVE_PROPERTY, DECLARE_BANKRUPTCY };
	public static final List<String> END_OF_TURN_ACTIONS_LIST = List.of(END_OF_TURN_ACTIONS);
	public static final int MIN_BID = 10;

	private final JFrame frame;
	private final JPanel panel;
	private boolean isInitialized = false;
	private List<BoardElementVisual> boardElementVisuals = new ArrayList<>();
	private List<PlayerVisual> staticPlayerVisuals = new ArrayList<>();
	private Player avatar;
	// allow us to save a game state for debug purposes
	private boolean saveNextGameState = false;

	public GraphicalGameClient() {
		System.out.println("GraphicalGameClient constructor called on Thread" + Thread.currentThread().threadId());
		frame = new JFrame(Main.TITLE + " - " + "Player ?");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize((int) (screenSize.width * INITIAL_WINDOW_SIZE_FACTOR), (int) (screenSize.height * INITIAL_WINDOW_SIZE_FACTOR));
		panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 0.5;
		constraints.weighty = 0.5;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;

		constraints.gridy = 2;
		constraints.gridx = 2;
		constraints.gridwidth = 7;
		constraints.gridheight = 7;
		constraints.weighty = 0;
		JTextArea printOutput = new JTextArea();
		printOutput.setFont(new Font("Arial Unicode MS", Font.PLAIN, 20));
		printOutput.setEditable(false);
		System.setOut(new PrintStream(new TextAreaOutputStream(printOutput)));
		panel.add(new JScrollPane(printOutput, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), constraints);
		constraints.weighty = 0.5;

		constraints.gridy = 1;
		constraints.gridx = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0.5;
		JButton saveGameStateButton = new JButton("Save Game State");
		saveGameStateButton.addActionListener(e -> saveNextGameState = true);
		panel.add(saveGameStateButton, constraints);

		List<GameClient> self = new ArrayList<>();
		self.add(this);
		update(new GameState(self));

		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				kill();
			}
		});

		frame.add(panel);
		frame.setVisible(true);
	}

	public void kill() {
		frame.setVisible(false);
		frame.dispose();
		System.exit(1);
	}

	private void setTitle(int playerIndex) {
		frame.setTitle(Main.TITLE + " - Player " + playerIndex + " " + Player.PLAYER_ICONS[playerIndex]);
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
		} else if(prompt == EndTurnAction.class) {
			final int dialogResult = JOptionPane.showOptionDialog(frame, "End of Turn Options", "End of Turn Options", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, END_OF_TURN_ACTIONS, END_OF_TURN_ACTIONS[0]);

			if(dialogResult == END_OF_TURN_ACTIONS_LIST.indexOf(END_TURN)) {
				return new EndTurnAction();
			} else if(dialogResult == END_OF_TURN_ACTIONS_LIST.indexOf(TRADE)) {

				final int playerSelection = JOptionPane.showOptionDialog(frame, "Select Player to Trade With", "Select Player to Trade With", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, gameState.getPlayers(), gameState.getPlayers()[0]);

				if(playerSelection == JOptionPane.CLOSED_OPTION) {
					return new EndTurnAction();
				}

				Player tradee = gameState.getPlayers()[playerSelection];

				Duo<JScrollPane, JList<String>> avatarPositions = generatePositionList(avatar, gameState);
				Duo<JScrollPane, JList<String>> tradeePositions = generatePositionList(tradee, gameState);

				SpinnerNumberModel moneyOfferedModel = new SpinnerNumberModel(0, 0, Math.max(0, avatar.getMoney()), 10);
				SpinnerNumberModel moneyRequestedModel = new SpinnerNumberModel(0, 0, Math.max(0, tradee.getMoney()), 10);

				JSpinner moneyOfferedSpinner = new JSpinner(moneyOfferedModel);
				JSpinner moneyRequestedSpinner = new JSpinner(moneyRequestedModel);

				JPanel panel = new JPanel();
				panel.add(new JLabel("Properties Offered:"));
				panel.add(avatarPositions.a);
				panel.add(new JLabel("Properties Requested:"));
				panel.add(tradeePositions.a);
				panel.add(new JLabel("Money Offered :"));
				panel.add(moneyOfferedSpinner);
				panel.add(new JLabel("Money Requested: "));
				panel.add(moneyRequestedSpinner);
				panel.setVisible(true);

				final int dialogResult2 = JOptionPane.showConfirmDialog(frame, panel, "Select Properties to Trade", JOptionPane.OK_CANCEL_OPTION);

				if(dialogResult2 == JOptionPane.OK_OPTION) {
					List<BoardPosition> positionsOffered = new ArrayList<>();
					for(String name : avatarPositions.b.getSelectedValuesList()) {
						positionsOffered.add(gameState.getBoardElement(name).position);
					}
					List<BoardPosition> positionsRequested = new ArrayList<>();
					for(String name : tradeePositions.b.getSelectedValuesList()) {
						positionsRequested.add(gameState.getBoardElement(name).position);
					}

					Trade sample = new Trade(avatar, tradee, (Integer) moneyRequestedSpinner.getValue(), (Integer) moneyOfferedSpinner.getValue(), positionsRequested, positionsOffered);
					return new ProposeTradeAction(sample);
				} else {
					return new EndTurnAction();
				}
			} else if(dialogResult == END_OF_TURN_ACTIONS_LIST.indexOf(IMPROVE_PROPERTY)) {
				Duo<JScrollPane, JList<String>> avatarPositions = generatePositionList(avatar, gameState);

				JPanel panel = new JPanel();
				panel.add(avatarPositions.a);
				panel.setVisible(true);

				final int dialogResult2 = JOptionPane.showConfirmDialog(frame, panel, "Select Property to Improve", JOptionPane.YES_NO_CANCEL_OPTION);

				if(dialogResult2 == JOptionPane.YES_OPTION || dialogResult2 == JOptionPane.NO_OPTION) {
					BoardPosition position = gameState.getBoardElement(avatarPositions.b.getSelectedValue()).position;
					return new ImprovePropertyAction(position, dialogResult2 == JOptionPane.YES_OPTION);
				} else {
					System.out.println("Improvement cancelled");
					return new EndTurnAction();
				}
			} else if(dialogResult == END_OF_TURN_ACTIONS_LIST.indexOf(DECLARE_BANKRUPTCY)) {
				return new DeclareBankruptcyAction();
			} else {
				return new EndTurnAction();
			}
		} else if(prompt == AcceptTradeAction.class) {
			// Show a dialog box asking the player if they want to accept the trade
			final int dialogResult = JOptionPane.showConfirmDialog(frame, "Would you like to accept? " + gameState.getProposedTrade().toString(), "Trade Offer", JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION) {
				return new AcceptTradeAction(gameState.getProposedTrade(), true);
			} else {
				return new AcceptTradeAction(gameState.getProposedTrade(), false);
			}
		} else {
			System.out.println("Received unrecognized prompt " + prompt.getSimpleName());
			return null;
		}
	}

	private static Duo<JScrollPane, JList<String>> generatePositionList(Player player, GameState gameState) {
		DefaultListModel<String> positionListBuilder = new DefaultListModel<>();
		for(BoardElement element : gameState.getBoardElements()) {
			if(element.owner == player) {
				positionListBuilder.addElement(element.position.niceName);
			}
		}

		JList<String> positionList = new JList<>(positionListBuilder);
		positionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		positionList.setLayoutOrientation(JList.VERTICAL);
		positionList.setVisibleRowCount(-1);

		JScrollPane positionListPane = new JScrollPane(positionList);
		positionListPane.setPreferredSize(new Dimension(200, 200));

		return new Duo<>(positionListPane, positionList);
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
		if(saveNextGameState) {
			try {
				File file = new File(gameState.hashCode() + "_GameState.ser");
				FileOutputStream out = new FileOutputStream(file);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
				objectOutputStream.writeObject(gameState);
				System.out.println(file.getAbsolutePath() + " saved");
			} catch(Exception e) {
				e.printStackTrace();
			}
			saveNextGameState = false;
		}
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
			if(staticPlayerVisuals.size() != gameState.getPlayers().length) {
				// remove all visuals we've added before
				for(PlayerVisual visual : staticPlayerVisuals)
					panel.remove(visual);
				staticPlayerVisuals.clear();

				// add a new visual for each player
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridwidth = 1;
				constraints.gridheight = 1;
				constraints.weightx = 1;
				constraints.weighty = 1;
				constraints.gridx = 4;
				constraints.gridy = 9;
				constraints.fill = GridBagConstraints.BOTH;
				constraints.anchor = GridBagConstraints.CENTER;
				for(int i = 0; i < gameState.getPlayers().length; i++) {
					constraints.gridx = 2 + (i * 2);
					PlayerVisual visual = new PlayerVisual(gameState.getPlayers()[i]);
					panel.add(visual, constraints);
					staticPlayerVisuals.add(visual);
				}
			}
			for(PlayerVisual visual : staticPlayerVisuals) {
				visual.update(gameState);
			}
			for(BoardElementVisual visual : boardElementVisuals) {
				visual.update(gameState);
			}
		}
		frame.repaint();
		return true;
	}

}

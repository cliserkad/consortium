package xyz.cliserkad.consortium;

import xyz.cliserkad.consortium.gui.NonModalDialog;
import xyz.cliserkad.util.BestList;
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

	public enum EndTurnOption {
		END_TURN,
		TRADE,
		IMPROVE_PROPERTY,
		DECLARE_BANKRUPTCY;

		public String toString() {
			return Main.prettifyEnumName(name());
		}
	}

	public enum ImproveDegrade {
		IMPROVE,
		DEGRADE;

		public String toString() {
			return Main.prettifyEnumName(name());
		}
	}

	public enum BidOption {
		BID_10(10),
		BID_50(50),
		BID_100(100),
		WITHDRAW(0);

		public final int amt;

		BidOption(int amt) {
			this.amt = amt;
		}

		public String toString() {
			if(amt == 0) {
				return "Withdraw";
			}
			return Main.prettifyEnumName(name()) + " $" + amt;
		}
	}

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
		try {
			update(gameState);
			setTitle(avatar.playerIndex);
			this.avatar = avatar;
			if(prompt == PurchaseAction.class && avatar.getPosition().logic instanceof Purchasable purchasable) {
				// Show a dialog box asking the player if they want to purchase the property
				if(NonModalDialog.showYesNoDialog("Would you like to purchase " + avatar.getPosition().niceName + " for $" + purchasable.cost() + "?").get()) {
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
				final BidOption selection = NonModalDialog.showDialog(BidOption.class, "Bid on Property" + gameState.getAuction().property.position).get();
				if(selection == null || selection == BidOption.WITHDRAW) {
					return null;
				} else {
					return new BidAction(gameState.getAuction().property.position, gameState.getAuction().bid + selection.amt);
				}
			} else if(prompt == EndTurnAction.class) {
				return switch(NonModalDialog.showDialog(EndTurnOption.class, "End of Turn Options").get()) {
					case EndTurnOption endTurnOption -> {
						yield switch(endTurnOption) {
							case END_TURN -> {
								yield new EndTurnAction();
							}
							case TRADE -> {
								BestList<Player> playerList = new BestList<>(gameState.getPlayers());
								playerList.remove(avatar);
								final Player tradee = NonModalDialog.showDialog("Select Player to Trade With", playerList.toArray(new Player[] {})).get();
								if(tradee == null) {
									yield new EndTurnAction();
								}

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
								panel.add(new JLabel("Money Offered:"));
								panel.add(moneyOfferedSpinner);
								panel.add(new JLabel("Money Requested: "));
								panel.add(moneyRequestedSpinner);
								panel.setVisible(true);

								final boolean dialogResult2 = NonModalDialog.showConfirmationDialog(panel, "Are you sure you want to propose this trade?").get();

								if(dialogResult2) {
									List<BoardPosition> positionsOffered = new ArrayList<>();
									for(String name : avatarPositions.b.getSelectedValuesList()) {
										positionsOffered.add(gameState.getBoardElement(name).position);
									}
									List<BoardPosition> positionsRequested = new ArrayList<>();
									for(String name : tradeePositions.b.getSelectedValuesList()) {
										positionsRequested.add(gameState.getBoardElement(name).position);
									}

									Trade sample = new Trade(avatar, tradee, (Integer) moneyRequestedSpinner.getValue(), (Integer) moneyOfferedSpinner.getValue(), positionsRequested, positionsOffered);
									yield new ProposeTradeAction(sample);
								} else {
									yield new EndTurnAction();
								}
							}
							case IMPROVE_PROPERTY -> {
								Duo<JScrollPane, JList<String>> avatarPositions = generatePositionList(avatar, gameState);

								JPanel panel = new JPanel();
								panel.add(avatarPositions.a);
								panel.setVisible(true);

								final ImproveDegrade improveDegrade = NonModalDialog.showDialog(panel, ImproveDegrade.class, "Select Property to Improve").get();

								if(improveDegrade != null) {
									BoardPosition position = gameState.getBoardElement(avatarPositions.b.getSelectedValue()).position;
									yield new ImprovePropertyAction(position, improveDegrade == ImproveDegrade.IMPROVE);
								} else {
									System.out.println("Improvement cancelled");
									yield new EndTurnAction();
								}
							}
							case DECLARE_BANKRUPTCY -> {
								yield new DeclareBankruptcyAction();
							}
						};
					}
					case null -> {
						yield new EndTurnAction();
					}
				};
			} else if(prompt == AcceptTradeAction.class) {
				// Show a dialog box asking the player if they want to accept the trade
				return new AcceptTradeAction(gameState.getProposedTrade(), NonModalDialog.showYesNoDialog("Would you like to accept?\n" + gameState.getProposedTrade().toString(), "Trade Offer").get());
			} else {
				System.out.println("Received unrecognized prompt " + prompt.getSimpleName());
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace();
			e.printStackTrace(System.out);
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

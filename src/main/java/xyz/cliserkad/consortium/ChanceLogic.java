package xyz.cliserkad.consortium;

public class ChanceLogic implements PositionLogic {

	public static final ChanceLogic INSTANCE = new ChanceLogic();

	enum ChanceCard {

		ADVANCE_TO_BOARDWALK,
		ADVANCE_TO_GO,
		ADVANCE_TO_ILLINOIS_AVE,
		ADVANCE_ST_CHARLES,
		ADVANCE_TO_RAILROAD,
		ADVANCE_TO_UTILITY,
		BANK_ERROR,
		GET_OUT_OF_JAIL,
		GO_BACK_THREE,
		GO_TO_JAIL,
		GEN_REPAIRS,
		GO_TO_READING,
		POOR_TAX,
		CHAIRMAN_OF_BOARD,
		BUILDING_LOAN,
		ADVANCE_TO_VERMONT;

		public final String niceName = Main.prettifyEnumName(name());

	}

	@Override
	public String onLand(Player player, GameState gameState) {
		ChanceLogic.ChanceCard pulledCard = ChanceLogic.ChanceCard.values()[gameState.nextChanceCard()];
		switch(pulledCard) {
			case ADVANCE_TO_BOARDWALK -> gameState.movePlayer(player, BoardPosition.BOARDWALK, 0);
			case ADVANCE_TO_GO -> gameState.movePlayer(player, BoardPosition.GO, 0);
			case ADVANCE_TO_ILLINOIS_AVE -> gameState.movePlayer(player, BoardPosition.ILLINOIS_AVENUE, 0);
			case ADVANCE_ST_CHARLES -> gameState.movePlayer(player, BoardPosition.ST_CHARLES_PLACE, 0);
			case ADVANCE_TO_RAILROAD -> {
				final int distanceToReading = player.getPosition().findAbsDistanceTo(BoardPosition.READING_RAILROAD);
				final int distanceToPenn = player.getPosition().findAbsDistanceTo(BoardPosition.PENNSYLVANIA_RAILROAD);
				final int distanceToBnO = player.getPosition().findAbsDistanceTo(BoardPosition.B_AND_O_RAILROAD);
				final int distanceToShort = player.getPosition().findAbsDistanceTo(BoardPosition.SHORT_LINE);
				if(distanceToReading < distanceToPenn && distanceToReading < distanceToBnO && distanceToReading < distanceToShort) {
					gameState.movePlayer(player, BoardPosition.READING_RAILROAD, 0);
				} else if(distanceToPenn < distanceToBnO && distanceToPenn < distanceToShort) {
					gameState.movePlayer(player, BoardPosition.PENNSYLVANIA_RAILROAD, 0);
				} else if(distanceToBnO < distanceToShort) {
					gameState.movePlayer(player, BoardPosition.B_AND_O_RAILROAD, 0);
				} else {
					gameState.movePlayer(player, BoardPosition.SHORT_LINE, 0);
				}
			}
			case ADVANCE_TO_UTILITY -> {
				final int distanceToElectric = player.getPosition().findAbsDistanceTo(BoardPosition.ELECTRIC_COMPANY);
				final int distanceToWater = player.getPosition().findAbsDistanceTo(BoardPosition.WATER_WORKS);
				if(distanceToElectric < distanceToWater) {
					gameState.movePlayer(player, BoardPosition.ELECTRIC_COMPANY, 0);
				} else {
					gameState.movePlayer(player, BoardPosition.WATER_WORKS, 0);
				}
			}
			case BANK_ERROR -> player.addMoney(200);
			case GET_OUT_OF_JAIL -> gameState.broadcast("Nah, you're stuck in jail.");
			case GO_BACK_THREE -> gameState.broadcast("TODO: implement backwards movement");
			case GO_TO_JAIL -> gameState.sendToJail(player, "pulling a Chance card.");
			case GEN_REPAIRS -> {
				int houseCost = 40;
				int hotelCost = 115;
				int totalCost = 0;
				for(BoardElement element : gameState.getBoardElements()) {
					if(element.owner == player) {
						if(element.improvementAmt == 5) {
							totalCost += hotelCost;
						} else if(element.improvementAmt > 0) {
							totalCost += element.improvementAmt * houseCost;
						}
					}
				}
				player.addMoney(-totalCost);
			}
			case GO_TO_READING -> gameState.movePlayer(player, BoardPosition.READING_RAILROAD, 0);
			case POOR_TAX -> player.addMoney(-15);
			case CHAIRMAN_OF_BOARD -> {
				for(Player otherPlayer : gameState.getPlayers()) {
					if(otherPlayer != player) {
						player.transferMoney(otherPlayer, 50);
					}
				}
			}
			case BUILDING_LOAN -> player.addMoney(150);
			// TODO: fix double auction failure when ADVANCE_TO_VERMONT is pulled
			case ADVANCE_TO_VERMONT -> gameState.movePlayer(player, BoardPosition.VERMONT_AVENUE, 0);
		}
		return player.getIcon() + " pulled Chance Card \"" + pulledCard.niceName + "\"";
	}

	private ChanceLogic() {
	}

}

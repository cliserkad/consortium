package xyz.cliserkad.consortium;

public class CommunityChestLogic implements PositionLogic {
	public static final CommunityChestLogic INSTANCE = new CommunityChestLogic();

	enum CommunityCard {
		ADVANCE_TO_GO,
		BANK_ERROR,
		DOCTOR_FEE,
		STOCK_DIVIDEND,
		GET_OUT_OF_JAIL,
		GO_TO_JAIL,
		HOLIDAY_FUND,
		INCOME_TAX_REFUND,
		BIRTHDAY_PARTY,
		LIFE_INSURANCE,
		HOSPITAL_FEE,
		SCHOOL_TAX,
		CONSULTANCY_FEE,
		STREET_REPAIR,
		BEAUTY_CONTEST,
		INHERITANCE
	}

	@Override
	public String onLand(Player player, Main main) {
		CommunityCard pulledCard = CommunityCard.values()[main.nextCommunityCard()];
		switch(pulledCard) {
			case ADVANCE_TO_GO -> main.movePlayer(player, BoardPosition.GO, 0);
			case BANK_ERROR -> player.addMoney(200);
			case DOCTOR_FEE, SCHOOL_TAX -> player.addMoney(-50);
			case STOCK_DIVIDEND -> player.addMoney(50);
			case GET_OUT_OF_JAIL -> System.out.println("Nah, you're stuck in jail.");
			case GO_TO_JAIL -> main.movePlayer(player, BoardPosition.JAIL, 0);
			case HOLIDAY_FUND, LIFE_INSURANCE, INHERITANCE -> player.addMoney(100);
			case INCOME_TAX_REFUND -> player.addMoney(20);
			case BIRTHDAY_PARTY -> {
				for(Player otherPlayer : main.getPlayers()) {
					if(otherPlayer != player) {
						otherPlayer.transferMoney(player, 10);
					}
				}
			}
			case HOSPITAL_FEE -> player.addMoney(-100);
			case CONSULTANCY_FEE -> player.addMoney(25);
			case STREET_REPAIR -> System.out.println("You owe $40 per house and $115 per hotel.");
			case BEAUTY_CONTEST -> player.addMoney(10);
		};
		return player.getIcon() + " pulled Community Chest Card " + pulledCard.name();
	}

	private CommunityChestLogic() {}

}

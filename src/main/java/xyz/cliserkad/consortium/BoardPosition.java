package xyz.cliserkad.consortium;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static xyz.cliserkad.consortium.Main.*;

/**
 * Represents the static data for each position on the board
 */
public enum BoardPosition implements Serializable {
	GO(
		Color.WHITE,
		new GoLogic()
	),
	MEDITERRANEAN_AVENUE(
		  new Color(0x8B4513)
		, new StandardLogic(60, 50, new int[]{ 2, 10, 30, 90, 160, 250 })
	),
	COMMUNITY_CHEST_1(
		Color.WHITE,
		CommunityChestLogic.INSTANCE
	),
	BALTIC_AVENUE(
		MEDITERRANEAN_AVENUE.color
		, new StandardLogic(60, 50, new int[]{ 4, 20, 60, 180, 320, 450 })
	),
	INCOME_TAX(
		Color.WHITE,
		TaxLogic.INCOME_TAX
	),
	READING_RAILROAD(
		Color.BLACK,
		new RailRoadLogic()
	),
	ORIENTAL_AVENUE(
		new Color(0xB2FFE8)
		, new StandardLogic(100, 50, new int[] {6, 30, 90, 270, 400, 550 })
	),
	CHANCE_1(
		Color.WHITE,
		ChanceLogic.INSTANCE
	),
	VERMONT_AVENUE(
		ORIENTAL_AVENUE.color
		, new StandardLogic(100, 50, new int[]{ 6, 30, 90, 270, 400, 550 })
	),
	CONNECTICUT_AVENUE(
		ORIENTAL_AVENUE.color
		, new StandardLogic(120, 50, new int[]{ 8, 40, 100, 300, 450, 600 })
	),
	JAIL,
	ST_CHARLES_PLACE(
		new Color(0xB019AA)
		, new StandardLogic(140, 100, new int[]{ 10, 50, 150, 450, 625, 750 })
	),
	ELECTRIC_COMPANY(
		new Color(134, 134, 134)
		, new UtilityLogic()
	),
	STATES_AVENUE(
		ST_CHARLES_PLACE.color
		, new StandardLogic(140, 100, new int[]{ 10, 50, 150, 450, 625, 750 })
	),
	VIRGINIA_AVENUE(
		ST_CHARLES_PLACE.color
		, new StandardLogic(160, 100, new int[]{ 12, 60, 180, 500, 700, 900 })
	),
	PENNSYLVANIA_RAILROAD(
		READING_RAILROAD.color
		, new RailRoadLogic()
	),
	ST_JAMES_PLACE(
		new Color(0xC95C14)
		, new StandardLogic(180, 100, new int[]{ 14, 70, 200, 550, 750, 950 })
	),
	COMMUNITY_CHEST_2(
		Color.WHITE,
		CommunityChestLogic.INSTANCE
	),
	TENNESSEE_AVENUE(
		ST_JAMES_PLACE.color
		, new StandardLogic(180, 100, new int[]{ 14, 70, 200, 550, 750, 950 })
	),
	NEW_YORK_AVENUE(
		ST_JAMES_PLACE.color
		, new StandardLogic(200, 100, new int[]{ 16, 80, 220, 600, 800, 1000 })
	),
	FREE_PARKING,
	KENTUCKY_AVENUE(
		new Color(0xE50D1E)
		, new StandardLogic(220, 150, new int[]{ 18, 90, 250, 700, 875, 1050 })
	),
	CHANCE_2(
		Color.WHITE,
		ChanceLogic.INSTANCE
	),
	INDIANA_AVENUE(
		KENTUCKY_AVENUE.color
		, new StandardLogic(220, 150, new int[]{ 18, 90, 250, 700, 875, 1050 })
	),
	ILLINOIS_AVENUE(
		KENTUCKY_AVENUE.color
		, new StandardLogic(240, 150, new int[]{ 20, 100, 300, 750, 925, 1100 })
	),
	B_AND_O_RAILROAD(
		READING_RAILROAD.color
		, new RailRoadLogic()
	),
	ATLANTIC_AVENUE(
		new Color(0xE5DA16)
		, new StandardLogic(260, 150, new int[]{ 22, 110, 330, 800, 975, 1150 })
	),
	VENTNOR_AVENUE(
		ATLANTIC_AVENUE.color
		, new StandardLogic(260, 150, new int[]{ 22, 110, 330, 800, 975, 1150 })
	),
	WATER_WORKS(
		ELECTRIC_COMPANY.color
		, new UtilityLogic()
	),
	MARVIN_GARDENS(
		ATLANTIC_AVENUE.color
		, new StandardLogic(280, 150, new int[]{ 24, 120, 360, 850, 1025, 1200 })
	),
	GO_TO_JAIL,
	PACIFIC_AVENUE(
		new Color(0x1DE81D)
		, new StandardLogic(300, 200, new int[]{ 26, 130, 390, 900, 1100, 1275 })
	),
	NORTH_CAROLINA_AVENUE(
		PACIFIC_AVENUE.color
		, new StandardLogic(300, 200, new int[]{ 26, 130, 390, 900, 1100, 1275 })
	),
	COMMUNITY_CHEST_3(
		Color.WHITE,
		CommunityChestLogic.INSTANCE
	),
	PENSNSYLVANIA_AVENUE(
		PACIFIC_AVENUE.color
		, new StandardLogic(320, 200, new int[]{ 28, 150, 450, 1000, 1200, 1400 })
	),
	SHORT_LINE(
		READING_RAILROAD.color
		, new RailRoadLogic()
	),
	CHANCE_3(
		Color.WHITE,
		ChanceLogic.INSTANCE
	),
	PARK_PLACE(
		new Color(0x1D1DE8)
		, new StandardLogic(350, 200, new int[]{ 35, 175, 500, 1100, 1300, 1500 })
	),
	LUXURY_TAX(
		Color.WHITE,
		TaxLogic.LUXURY_TAX
	),
	BOARDWALK(
		PARK_PLACE.color
		, new StandardLogic(400, 200, new int[]{ 50, 200, 600, 1400, 1700, 2000 })
	);

	private static final long serialVersionUID = 20240615L;

	public final Color color;
	public final PositionLogic logic;
	public final String niceName;

	BoardPosition() {
		this(Color.WHITE, NoLogic.INSTANCE);
	}

	BoardPosition(final Color color, final PositionLogic logic) {
		this.color = color;
		this.logic = logic;
		this.niceName = niceName();
	}

	public List<BoardPosition> colorGroup() {
		List<BoardPosition> group = new ArrayList<>();
		for(BoardPosition position : values()) {
			if(position.color.equals(color)) {
				group.add(position);
			}
		}
		return group;
	}

	public int findAbsDistanceTo(BoardPosition target) {
		for(int steps = 0; steps < values().length; steps++) {
			if(next(steps) == target) {
				return steps;
			}
			if(next(-steps) == target) {
				return steps;
			}
		}
		throw new IllegalArgumentException("Target not found");
	}

	public BoardPosition next() {
		return next(1);
	}

	public BoardPosition next(int steps) {
		return values()[(ordinal() + steps) % values().length];
	}

	public boolean isPurchasable() {
		return logic.isPurchasable();
	}

	@Override
	public String toString() {
		return "BoardPosition: {" +
				"\n\tname: " + name() + "," +
				"\n\tcolor: " + color + "," +
				"\n}";
	}

	private String niceName() {
		final String name = name().replace("_", " ");
		StringBuilder out = new StringBuilder();
		out.append(Character.toUpperCase(name.charAt(0)));
		for(int i = 1; i < name.length(); i++) {
			if(!Character.isDigit(name.charAt(i))) {
				if(name.charAt(i - 1) == ' ') {
					out.append(Character.toUpperCase(name.charAt(i)));
				} else {
					out.append(Character.toLowerCase(name.charAt(i)));
				}
			}
		}
		return out.toString();
	}

	public Point2i getBoardCoords() {
		final int x;
		final int y;
		final int placeInRow = ordinal() % BOARD_X_SIZE_LESS_ONE;
		if(ordinal() < BOARD_X_SIZE_LESS_ONE) {
			x = BOARD_X_SIZE_LESS_ONE - placeInRow;
			y = BOARD_Y_SIZE_LESS_ONE;
		} else if(ordinal() < BOARD_X_SIZE_LESS_ONE * 2) {
			x = 0;
			y = BOARD_Y_SIZE_LESS_ONE - placeInRow;
		} else if(ordinal() < BOARD_X_SIZE_LESS_ONE * 3) {
			x = placeInRow;
			y = 0;
		} else {
			x = BOARD_X_SIZE_LESS_ONE;
			y = placeInRow;
		}
		return new Point2i(x, y);
	}

}

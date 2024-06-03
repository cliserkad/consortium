package xyz.cliserkad.consortium;

import java.awt.Color;

import static xyz.cliserkad.consortium.Main.*;

public enum BoardPosition {
	GO,
	MEDITERRANEAN_AVENUE(
		  new Color(0x8B4513)
		, new StandardLogic(60, 50, new int[]{ 2, 10, 30, 90, 160, 250 })
	),
	COMMUNITY_CHEST_1(),
	BALTIC_AVENUE(
		MEDITERRANEAN_AVENUE.color
		, new StandardLogic(60, 50, new int[]{ 4, 20, 60, 180, 320, 450 })
	),
	INCOME_TAX,
	READING_RAILROAD(
		  new Color(0x000000)
		, true
		, 200
		, -1
		, new int[]{ 25, 50, 100, 200, 0, 0 }
	),
	ORIENTAL_AVENUE(
		new Color(0xB2FFE8)
		, 100
		, new int[]{ 6, 30, 90, 270, 400, 550 }
	),
	CHANCE_1,
	VERMONT_AVENUE(
		ORIENTAL_AVENUE.color
		, 100
		, new int[]{ 6, 30, 90, 270, 400, 550 }
	),
	CONNECTICUT_AVENUE(
		ORIENTAL_AVENUE.color
		, 120
		, new int[]{ 8, 40, 100, 300, 450, 600 }
	),
	JAIL,
	ST_CHARLES_PLACE(
		new Color(0xB019AA)
		, 140
		, new int[]{ 10, 50, 150, 450, 625, 750 }
	),
	ELECTRIC_COMPANY(
		new Color(0, 0, 0, 0)
		, true
		, 150
		, -1
		, new int[]{ 0, 0, 0, 0, 0, 0 }
	),
	STATES_AVENUE(ST_CHARLES_PLACE.color),
	VIRGINIA_AVENUE(ST_CHARLES_PLACE.color),
	PENNSYLVANIA_RAILROAD(READING_RAILROAD.color),
	ST_JAMES_PLACE(new Color(0xC95C14)),
	COMMUNITY_CHEST_2,
	TENNESSEE_AVENUE(ST_JAMES_PLACE.color),
	NEW_YORK_AVENUE(ST_JAMES_PLACE.color),
	FREE_PARKING(
		new Color(0, 0, 0, 0),
		NoLogic.INSTANCE
	),
	KENTUCKY_AVENUE(new Color(0xE50D1E)),
	CHANCE_2,
	INDIANA_AVENUE(KENTUCKY_AVENUE.color),
	ILLINOIS_AVENUE(KENTUCKY_AVENUE.color),
	B_AND_O_RAILROAD(READING_RAILROAD.color),
	ATLANTIC_AVENUE(new Color(0xE5DA16)),
	VENTNOR_AVENUE(ATLANTIC_AVENUE.color),
	WATER_WORKS,
	MARVIN_GARDENS(ATLANTIC_AVENUE.color),
	GO_TO_JAIL,
	PACIFIC_AVENUE(new Color(0x1DE81D)),
	NORTH_CAROLINA_AVENUE(PACIFIC_AVENUE.color),
	COMMUNITY_CHEST_3,
	PENSNSYLVANIA_AVENUE(PACIFIC_AVENUE.color),
	SHORT_LINE(READING_RAILROAD.color),
	CHANCE_3,
	PARK_PLACE(new Color(0x1D1DE8)),
	LUXURY_TAX,
	BOARDWALK(PARK_PLACE.color);

	public final Color color;
	public final boolean isOwnable;


	public final PositionLogic logic;

	BoardPosition() {
		this(new Color(0, 0, 0, 0), new StandardLogic(0, 0, new int[]{ 0, 0, 0, 0, 0, 0 }));
	}

	BoardPosition(final Color color, final PositionLogic logic) {
		this.color = color;
		this.logic = logic;
	}

	public BoardPosition next() {
		if(ordinal() == values().length - 1) {
			return values()[0];
		}
		return BoardPosition.values()[ordinal() + 1];
	}

	@Override
	public String toString() {
		return "BoardPosition: {" +
				"\n\tname: " + name() + "," +
				"\n\tcolor: " + color + "," +
				"\n}";
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

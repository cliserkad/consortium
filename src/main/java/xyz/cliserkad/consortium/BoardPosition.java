package xyz.cliserkad.consortium;

import java.awt.Color;

import static xyz.cliserkad.consortium.Main.*;

public enum BoardPosition {
	GO,
	MEDITERRANEAN_AVENUE(new Color(0x8B4513)),
	COMMUNITY_CHEST_1(),
	BALTIC_AVENUE(MEDITERRANEAN_AVENUE.color),
	INCOME_TAX,
	READING_RAILROAD(Color.BLACK),
	ORIENTAL_AVENUE(new Color(0xB2FFE8)),
	CHANCE_1,
	VERMONT_AVENUE(ORIENTAL_AVENUE.color),
	CONNECTICUT_AVENUE(ORIENTAL_AVENUE.color),
	JAIL,
	ST_CHARLES_PLACE(new Color(0xB019AA)),
	ELECTRIC_COMPANY,
	STATES_AVENUE(ST_CHARLES_PLACE.color),
	VIRGINIA_AVENUE(ST_CHARLES_PLACE.color),
	PENNSYLVANIA_RAILROAD(READING_RAILROAD.color),
	ST_JAMES_PLACE(new Color(0xC95C14)),
	COMMUNITY_CHEST_2,
	TENNESSEE_AVENUE(ST_JAMES_PLACE.color),
	NEW_YORK_AVENUE(ST_JAMES_PLACE.color),
	FREE_PARKING,
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
	SHORT_LINE,
	CHANCE_3,
	PARK_PLACE(new Color(0x1D1DE8)),
	LUXURY_TAX,
	BOARDWALK(PARK_PLACE.color);

	public final Color color;

	BoardPosition() {
		this.color = new Color(0, 0, 0, 0);
	}

	BoardPosition(final Color color) {
		this.color = color;
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

package xyz.cliserkad.consortium;

import java.util.ArrayList;
import java.util.List;

public class Player {
	private BoardPosition position;
	private List<BoardPosition> propertiesOwned;
	private int money;

	public Player() {
		this.position = BoardPosition.GO;
		this.propertiesOwned = new ArrayList<>();
		this.money = 1800;
	}


}

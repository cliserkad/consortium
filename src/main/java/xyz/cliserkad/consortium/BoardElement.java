package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Holds game state for each BoardPosition
 */
public class BoardElement implements Serializable {
	private static final long serialVersionUID = 20240615L;

	public final BoardPosition position;
	public Player owner;
	public int improvementAmt;

	public BoardElement(final BoardPosition position) {
		this.position = position;
	}

	/**
	 * Sets the owner of this BoardElement
	 */
	public boolean setOwner(Player player) {
		if(position.isPurchasable()) {
			owner = player;
		}
		return position.isPurchasable();
	}

}

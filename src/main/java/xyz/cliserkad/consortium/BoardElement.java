package xyz.cliserkad.consortium;

import java.io.Serializable;

import static xyz.cliserkad.consortium.StandardLogic.MORTGAGE_IMPROVEMENT_AMOUNT;

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

	public boolean isMortgaged() {
		return improvementAmt == MORTGAGE_IMPROVEMENT_AMOUNT;
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

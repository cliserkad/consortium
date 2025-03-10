package xyz.cliserkad.consortium;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Auction implements Serializable {

	@Serial
	private static final long serialVersionUID = 20250309;

	public final BoardElement property;
	public final List<Player> bidders;
	public Player currentBidder;
	public List<BidAction> bids;
	public int bid;

	// TODO: fix purchasable interface
	public Auction(BoardElement property, List<Player> bidders, GameConfig config) {
		this.property = property;
		this.bidders = bidders;
		bids = new ArrayList<>();
		if(property.position.logic instanceof Purchasable purchasable) {
			bid = (int) (purchasable.cost() * config.startingBidFactor) - config.minimumBid;
		} else {
			System.err.println("Auction property is not purchasable.");
			bid = 0;
		}
	}

}

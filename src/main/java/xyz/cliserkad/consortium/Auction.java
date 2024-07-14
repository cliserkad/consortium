package xyz.cliserkad.consortium;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Auction implements Serializable {
	@Serial
	private static final long serialVersionUID = 20240714;

	public final BoardElement property;
	public final List<Player> bidders;
	public Player currentBidder;
	public List<BidAction> bids;
	public int bid;

	public Auction(BoardElement property, List<Player> bidders) {
		this.property = property;
		this.bidders = bidders;
		bids = new ArrayList<>();
		bid = 0;
	}

}

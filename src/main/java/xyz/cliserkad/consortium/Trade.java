package xyz.cliserkad.consortium;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Trade implements Serializable {

	@Serial
	private static final long serialVersionUID = 20240718L;

	Player proposer;
	Player acceptor;
	int moneyToProposer;
	int moneyToAcceptor;
	List<BoardPosition> positionsToProposer;
	List<BoardPosition> positionsToAcceptor;

	public Trade(Player proposer, Player acceptor, int moneyToProposer, int moneyToAcceptor, List<BoardPosition> positionsToProposer, List<BoardPosition> positionsToAcceptor) {
		this.proposer = proposer;
		this.acceptor = acceptor;
		this.moneyToProposer = moneyToProposer;
		this.moneyToAcceptor = moneyToAcceptor;
		this.positionsToProposer = positionsToProposer;
		this.positionsToAcceptor = positionsToAcceptor;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(!(o instanceof Trade trade))
			return false;
		return moneyToProposer == trade.moneyToProposer && moneyToAcceptor == trade.moneyToAcceptor && Objects.equals(proposer, trade.proposer) && Objects.equals(acceptor, trade.acceptor) && Objects.equals(positionsToProposer, trade.positionsToProposer) && Objects.equals(positionsToAcceptor, trade.positionsToAcceptor);
	}

	@Override
	public String toString() {
		return "Trade {" + "\n\tproposer=" + proposer + "\n\tacceptor=" + acceptor + "\n\tmoneyToProposer=" + moneyToProposer + "\n\tmoneyToAcceptor=" + moneyToAcceptor + "\n\tpositionsToProposer=" + positionsToProposer + "\n\tpositionsToAcceptor=" + positionsToAcceptor + "\n}";
	}

	@Override
	public int hashCode() {
		return Objects.hash(proposer, acceptor, moneyToProposer, moneyToAcceptor, positionsToProposer, positionsToAcceptor);
	}

	public GameState apply(GameState gameState) {
		Player proposer = gameState.getPlayers()[this.proposer.playerIndex];
		Player acceptor = gameState.getPlayers()[this.acceptor.playerIndex];
		proposer.addMoney(this.moneyToProposer);
		acceptor.addMoney(-this.moneyToProposer);
		acceptor.addMoney(this.moneyToAcceptor);
		proposer.addMoney(-this.moneyToAcceptor);
		for(BoardPosition position : this.positionsToProposer) {
			gameState.getBoardElement(position).setOwner(proposer);
		}
		for(BoardPosition position : this.positionsToAcceptor) {
			gameState.getBoardElement(position).setOwner(acceptor);
		}
		return gameState;
	}

}

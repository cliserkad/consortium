package xyz.cliserkad.consortium;

public record BidAction(BoardPosition position, int amount) implements PlayerAction {

}

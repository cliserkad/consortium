package xyz.cliserkad.consortium;

public record AcceptTradeAction(Trade trade, boolean accept) implements PlayerAction {

}

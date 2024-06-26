package xyz.cliserkad.consortium;

import java.io.Serializable;

/**
 * Represents an action that a player can take,
 * generated by a GameClient
 */
public sealed interface PlayerAction extends Serializable permits
	PurchaseAction,
	EndTurnAction
{ }

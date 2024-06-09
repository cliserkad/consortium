package xyz.cliserkad.consortium;

import javax.swing.*;

public class GraphicalPlayerController implements PlayerController {

	@Override
	public PlayerAction poll(Player avatar, Main main, Class<? extends PlayerAction>[] prompts) {
		if(avatar.getPosition().logic instanceof Purchasable purchasable) {
			// Show a dialog box asking the player if they want to purchase the property
			final int dialogResult = JOptionPane.showConfirmDialog(null, "Would you like to purchase " + avatar.getPosition().niceName + " for $" + purchasable.cost() + "?", "Purchase Property", JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION) {
				return new PurchaseAction(avatar.getPosition());
			}
		}
		return null;
	}

}

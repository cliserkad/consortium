package xyz.cliserkad.consortium;

import java.io.IOException;
import java.util.Random;

public class Main {

	public static final int BOARD_X_SIZE = 11;
	public static final int BOARD_X_SIZE_LESS_ONE = BOARD_X_SIZE - 1;
	public static final int BOARD_Y_SIZE = 11;
	public static final int BOARD_Y_SIZE_LESS_ONE = BOARD_Y_SIZE - 1;
	public static final int DICE_MAX = 12;
	public static final int DICE_MIN = 2;

	public static final Random RANDOM = new Random();

	public static void main(String[] args) throws IOException {
		NetworkedResponder<GameClient> responder = new NetworkedResponder<>(new GraphicalGameClient(), args[0], Integer.parseInt(args[1]));
		responder.start();
	}

	public static String prettifyEnumName(String input) {
		final String name = input.replace("_", " ");
		StringBuilder out = new StringBuilder();
		out.append(Character.toUpperCase(name.charAt(0)));
		for(int i = 1; i < name.length(); i++) {
			if(!Character.isDigit(name.charAt(i))) {
				if(name.charAt(i - 1) == ' ') {
					out.append(Character.toUpperCase(name.charAt(i)));
				} else {
					out.append(Character.toLowerCase(name.charAt(i)));
				}
			}
		}
		return out.toString();
	}

}
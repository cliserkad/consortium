package xyz.cliserkad.consortium;

import java.io.IOException;
import java.util.Random;

public class Main {

	public static final int BOARD_X_SIZE = 11;
	public static final int BOARD_X_SIZE_LESS_ONE = BOARD_X_SIZE - 1;
	public static final int BOARD_Y_SIZE = 11;
	public static final int BOARD_Y_SIZE_LESS_ONE = BOARD_Y_SIZE - 1;
	public static final int DICE_MAX = 6;
	public static final int DICE_MIN = 1;
	public static final String NULL_STRING = "null";
	public static final int MAX_IMPROVEMENT = 5;
	public static final int IMPROVEMENT_REFUND_DIVISOR = 2;
	public static final int JAIL_TURNS = 3;
	public static final int JAIL_DOUBLES = 3;

	public static final Random RANDOM = new Random();

	public static void main(String[] args) {
		if(args.length == 2) {
			try {
				NetworkedResponder<GameClient> responder = new NetworkedResponder<>(new GraphicalGameClient(), args[0], Integer.parseInt(args[1]));
				responder.start();
			} catch(NumberFormatException e) {
				System.err.println("Invalid port number: " + args[1]);
			} catch(IOException e) {
				System.err.println("Failed to connect to server at " + args[0] + ":" + args[1] + ": " + e.getMessage());
			}
		}
		new GameConnector();
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

	public static String nonNullToString(Object obj) {
		if(obj == null)
			return NULL_STRING;
		else
			return obj.toString();
	}

}
package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.Random;

public class Main {
	public static final int BOARD_X_SIZE = 11;
	public static final int BOARD_X_SIZE_LESS_ONE = BOARD_X_SIZE - 1;
	public static final int BOARD_Y_SIZE = 11;
	public static final int BOARD_Y_SIZE_LESS_ONE = BOARD_Y_SIZE - 1;
	public static final int DICE_MAX = 12;
	public static final int DICE_MIN = 2;

	public static final String PROMPT_TURN = "Prompt Turn";
	public static final String VISUALIZE_GAME_STATE = "Visualize Game State";
	public static final Random RANDOM = new Random();

}
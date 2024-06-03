package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;

public class Main {
	public static final int BOARD_X_SIZE = 11;
	public static final int BOARD_Y_SIZE = 11;


	public static void main(String[] args) {
		JFrame frame = new JFrame("Consortium");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1920, 1080);
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;

		for(int i = 0; i < 10; i++) {
			constraints.gridx = BOARD_X_SIZE - 1 - i;
			constraints.gridy = BOARD_Y_SIZE - 1;
			panel.add(new BoardElement(BoardPosition.values()[i]), constraints);
		}

		for(int i = 0; i < 10; i++) {
			constraints.gridx = 0;
			constraints.gridy = BOARD_Y_SIZE - 1 - i;
			panel.add(new BoardElement(BoardPosition.values()[i + 10]), constraints);
		}

		for(int i = 0; i < 10; i++) {
			constraints.gridx = i;
			constraints.gridy = 0;
			panel.add(new BoardElement(BoardPosition.values()[i + 20]), constraints);
		}

		for(int i = 0; i < 10; i++) {
			constraints.gridx = BOARD_X_SIZE - 1;
			constraints.gridy = i;
			panel.add(new BoardElement(BoardPosition.values()[i + 30]), constraints);
		}

		frame.add(panel);
		frame.setVisible(true);
	}

}
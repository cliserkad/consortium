package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import static xyz.cliserkad.consortium.GameServer.BASE_PORT;

public class GameConnector implements ActionListener {

	public static final String DEFAULT_HOST = "localhost";
	public static final String JAR_NAME = new File(GameConnector.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
	public static final String JAVA_COMMAND = "java";
	public static final String JAR_OPTION = "-jar";

	private JFrame frame;
	private JTextField ipField;
	private JTextField portField;

	public GameConnector() {
		frame = new JFrame("Consortium Game Connector");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.ipadx = 15;
		constraints.ipady = 15;

		constraints.gridy = 1;

		constraints.gridx = 1;
		panel.add(new JLabel("IP Address:"), constraints);

		constraints.gridx = 2;
		ipField = new JTextField(DEFAULT_HOST);
		panel.add(ipField, constraints);

		constraints.gridx = 3;
		panel.add(new JLabel("Port:"), constraints);

		constraints.gridx = 4;
		portField = new JTextField(BASE_PORT + "");
		panel.add(portField, constraints);

		constraints.gridx = 5;
		JButton startGameButton = new JButton("Start Game");
		startGameButton.addActionListener(this);
		panel.add(startGameButton, constraints);

		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		frame.removeAll();
		frame.setVisible(false);
		frame.dispose();
		Process process;
		try {
			final String[] command = { JAVA_COMMAND, JAR_OPTION, JAR_NAME, ipField.getText(), portField.getText() };
			process = Runtime.getRuntime().exec(command);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		try {
			process.waitFor();
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}

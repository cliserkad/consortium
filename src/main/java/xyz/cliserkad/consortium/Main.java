package xyz.cliserkad.consortium;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Random;

import static xyz.cliserkad.consortium.GameServer.readConfigFile;

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
	public static final double RED_LUMINANCE_FACTOR = 0.299;
	public static final double GREEN_LUMINANCE_FACTOR = 0.587;
	public static final double BLUE_LUMINANCE_FACTOR = 0.114;
	public static final double MAX_RED_LUMINANCE = RED_LUMINANCE_FACTOR * 255;
	public static final double MAX_GREEN_LUMINANCE = GREEN_LUMINANCE_FACTOR * 255;
	public static final double MAX_BLUE_LUMINANCE = BLUE_LUMINANCE_FACTOR * 255;
	public static final double MAX_LUMINANCE = MAX_RED_LUMINANCE + MAX_GREEN_LUMINANCE + MAX_BLUE_LUMINANCE;

	public static final Random RANDOM = new Random();
	public static final ResourcesDescriptor RESOURCES = describeResources();
	public static final float INITIAL_WINDOW_SIZE_FACTOR = 0.8f;
	public static final String TITLE = "Consortium " + Version.COMMIT_ID_ABBREV;

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println(TITLE + " built on " + Version.BUILD_TIME);
		System.setProperty("user.dir", RESOURCES.resourcesRoot.getAbsolutePath());
		if(args.length == 1 && args[0].equalsIgnoreCase("server")) {
			GameServer.main(args);
		} else {
			ConnectionConfig connectionConfig = readConfigFile(new ConnectionConfig(), ConnectionConfig.class);
			try {
				NetworkedResponder<GameClient> responder = new NetworkedResponder<>(new GraphicalGameClient(), connectionConfig.ipAddress, connectionConfig.port, true);
				responder.start();
			} catch(IOException e) {
				System.out.println("Failed to connect to server at " + connectionConfig.ipAddress + ":" + connectionConfig.port);
				System.out.println(e.getMessage());
			}
		}
		System.out.println("Exiting...");
	}

	public static ResourcesDescriptor describeResources() {
		final String classPath = "/" + Main.class.getName().replace('.', '/') + ".class";
		final File root;
		final File jarFile;
		URL classURL = Main.class.getResource(classPath);
		if(classURL.getProtocol().equals("jar")) {
			JarURLConnection jarConn = null;
			try {
				jarConn = (JarURLConnection) classURL.openConnection();
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			jarFile = new File(jarConn.getJarFileURL().getFile());
			root = jarFile.getParentFile();
		} else if(classURL.getProtocol().equals("file")) {
			root = new File(classURL.getFile().replace(classPath, ""));
			jarFile = null;
		} else {
			throw new IllegalStateException("Unsupported URL protocol: " + classURL.getProtocol());
		}
		return new ResourcesDescriptor(root, jarFile);
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

	public static Color textColorForBackground(Color backgroundColor) {
		if(isColorBright(backgroundColor))
			return Color.BLACK;
		else
			return Color.WHITE;
	}

	public static boolean isColorBright(Color color) {
		if(!color.getColorSpace().isCS_sRGB())
			System.out.println("Color is not in sRGB color space. Text color may not be calculated correctly.");
		return (RED_LUMINANCE_FACTOR * color.getRed() + GREEN_LUMINANCE_FACTOR * color.getGreen() + BLUE_LUMINANCE_FACTOR * color.getBlue()) / MAX_LUMINANCE > 0.5;
	}

}

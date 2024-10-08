package xyz.cliserkad.consortium;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
	public static final String GIT_INFO = "git.properties";
	public static final String VERSION = version();
	public static final String TITLE = "Consortium " + VERSION;

	public static void main(String[] args) throws IOException, InterruptedException {
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("server")) {
				GameServer.main(new String[] { args[1] });
			} else {
				try {
					NetworkedResponder<GameClient> responder = new NetworkedResponder<>(new GraphicalGameClient(), args[0], Integer.parseInt(args[1]));
					responder.start();
				} catch(NumberFormatException e) {
					System.err.println("Invalid port number: " + args[1]);
				} catch(IOException e) {
					System.err.println("Failed to connect to server at " + args[0] + ":" + args[1] + ": " + e.getMessage());
				}
			}
		}
		System.setProperty("user.dir", RESOURCES.resourcesRoot.getAbsolutePath());
		new GameConnector();
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

	public static String version() {
		String commitId = "COMMIT_ID_UNKNOWN";
		if(RESOURCES.jarFile != null) {
			try {
				String result = new BufferedReader(new InputStreamReader(resourcesAt(GIT_INFO).getFirst().openStream())).lines().collect(Collectors.joining("\n"));
				for(String line : result.split("\n")) {
					commitId = updateVersionIfInString(commitId, line);
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		} else {
			File gitProperties = new File(RESOURCES.resourcesRoot, GIT_INFO);
			if(gitProperties.exists()) {
				try {
					List<String> lines = Files.readAllLines(gitProperties.toPath());
					for(String line : lines) {
						commitId = updateVersionIfInString(commitId, line);
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		return commitId;
	}

	public static String updateVersionIfInString(String version, String line) {
		if(line.startsWith("git.commit.id.abbrev")) {
			return line.split("=")[1];
		} else {
			return version;
		}
	}

	public static List<URL> resourcesAt(String prefix) throws IOException {
		ZipInputStream zip = null;
		zip = new ZipInputStream(RESOURCES.jarFile.toURI().toURL().openStream());
		List<URL> resources = new ArrayList<>();
		while(true) {
			ZipEntry entry = zip.getNextEntry();
			if(entry == null) {
				break;
			}
			final String name = entry.getName();
			if(name.startsWith(prefix) && !entry.isDirectory()) {
				resources.add(Main.class.getClassLoader().getResource(name));
			}
		}
		zip.close();
		return resources;
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
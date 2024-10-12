package xyz.cliserkad.consortium;

public interface Pingable {

	public default String ping() {
		return "pong!";
	}

}

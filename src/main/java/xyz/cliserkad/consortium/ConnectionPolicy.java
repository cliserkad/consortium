package xyz.cliserkad.consortium;

public enum ConnectionPolicy {
	/** must not disconnect; must reconnect */
	REQUIRED,
	/** may disconnect; may reconnect */
	OPTIONAL,
	/** must disconnect; must not reconnect */
	FORBIDDEN;

	public boolean mayReconnect() {
		return this == OPTIONAL || this == REQUIRED;
	}
}

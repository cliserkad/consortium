package xyz.cliserkad.consortium;

import java.time.Instant;

public interface ConnectionMaintenance {

	default Instant ping() {
		return Instant.now();
	}

	String version(String version);

	void setConnectionPolicy(ConnectionPolicy policy, String rationale);

}

package xyz.cliserkad.consortium;

import java.time.Instant;
import java.util.UUID;

public interface ConnectionMaintenance {

	default Instant ping() {
		return Instant.now();
	}

	String version();

	void setConnectionPolicy(ConnectionPolicy policy, String rationale);

	UUID getSessionID();

	void setSessionID(UUID id);

}

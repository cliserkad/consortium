package xyz.cliserkad.consortium;

import java.io.Serial;
import java.io.Serializable;

public class MethodInvocation implements Serializable {

	@Serial
	private static final long serialVersionUID = 20240718L;

	public final String methodName;

	public MethodInvocation(String methodName) {
		this.methodName = methodName;
	}

}

package xyz.cliserkad.consortium;

import java.io.Serial;
import java.io.Serializable;

/**
 * A serializable version of the Duo class.
 * Adds type bounds to the generic types, for compile time checks.
 */
public class DuoSerial<TypeA extends Serializable, TypeB extends Serializable> extends Duo<TypeA, TypeB> implements Serializable {
	@Serial
	private static final long serialVersionUID = 20240727L;

	public DuoSerial(TypeA a, TypeB b) {
		super(a, b);
	}

	public DuoSerial(Duo<TypeA, TypeB> duo) {
		super(duo.a, duo.b);
	}

}

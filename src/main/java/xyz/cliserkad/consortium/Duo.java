package xyz.cliserkad.consortium;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * A class that holds two objects of any types.
 * @implNote May cause NotSerializableException
 * @see DuoSerial Guaranteed Serializable Duo
 */
public class Duo<TypeA, TypeB> implements Serializable {
	@Serial
	private static final long serialVersionUID = 20240727L;

	public final TypeA a;
	public final TypeB b;

	public Duo(final TypeA a, final TypeB b) {
		this.a = a;
		this.b = b;
	}

	public DuoSerial<? extends Serializable, ? extends Serializable> toSerializable() throws ImpossibleTransformException {
		if(a instanceof Serializable serializableA && b instanceof Serializable serializableB)
			return new DuoSerial<>(serializableA, serializableB);
		else if(!(a instanceof Serializable))
			throw new ImpossibleTransformException(a.getClass(), Serializable.class);
		else // b will not be serializable here
			throw new ImpossibleTransformException(b.getClass(), Serializable.class);
	}

	public boolean isSerializable() {
		return a instanceof Serializable && b instanceof Serializable;
	}

	@Override
	public String toString() {
		return "{\n"+ "a: \"" + a + "\";\n b:\"" + b + "\";\n}";
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(!(o instanceof Duo<?, ?> duo))
			return false;
		return Objects.equals(a, duo.a) && Objects.equals(b, duo.b);
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b);
	}

}

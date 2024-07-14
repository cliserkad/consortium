package xyz.cliserkad.consortium;

import java.util.Objects;

public class Duo<TypeA, TypeB> {
	public final TypeA a;
	public final TypeB b;

	public Duo(final TypeA a, final TypeB b) {
		this.a = a;
		this.b = b;
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

package xyz.cliserkad.consortium;

public class Point2i {

	public final int x;
	public final int y;

	public Point2i(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public Point2i(final Point2i other) {
		this.x = other.x;
		this.y = other.y;
	}

	@Override
	public boolean equals(Object object) {
		if(object == this) {
			return true;
		} else if(object instanceof Point2i p2) {
			return x == p2.x && y == p2.y;
		} else {
			return false;
		}
	}

	public String toString() {
		return "Point2i(" + x + ", " + y + ")";
	}

}

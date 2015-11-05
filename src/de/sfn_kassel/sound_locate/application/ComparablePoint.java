package de.sfn_kassel.sound_locate.application;

public class ComparablePoint implements Comparable<ComparablePoint> {
	final int x;
	final int y;

	public ComparablePoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(ComparablePoint comp) {
		return x > comp.x ? 1 : x < comp.x ? -1 : y > comp.y ? 1 : y < comp.y ? -1 : 0;
	}

	@Override
	public String toString() {
		return x + ", " + y;
	}
}

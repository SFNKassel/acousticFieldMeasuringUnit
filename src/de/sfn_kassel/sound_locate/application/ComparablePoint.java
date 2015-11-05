package de.sfn_kassel.sound_locate.application;

public class ComparablePoint implements Comparable<ComparablePoint>{
	final int x;
	final int y;
	
	public ComparablePoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(ComparablePoint comp) {
		if (x == comp.x && y == comp.y) {
			return 0;
		}
		return 1;
	}	
	
	@Override
	public String toString() {
		return x + ", " + y;
	}
}

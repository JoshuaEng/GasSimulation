package main;

import java.awt.Color;

/**
 * Enough to display and thats it
 * Immutable
 * @author Josh
 *
 */
public class DumbBall {

	public final Color color;
	public final PhysicalVector2D pos;
	public final double radius;
	DumbBall(PhysicalVector2D pos, double radius, Color color){
		this.pos = pos;
		this.radius = radius;
		this.color = color;
	}
}
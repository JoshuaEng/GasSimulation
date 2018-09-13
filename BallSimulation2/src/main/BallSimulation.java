package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class BallSimulation {
	private ArrayList<Ball> balls = new ArrayList<Ball>();
	ArrayList<Collision> collisions = new ArrayList<Collision>();

	HashMap<Ball, Collision> nextCollisions = new HashMap<Ball, Collision>();
	public Ball nextBall = null;
	double minCollisionTime = Double.MAX_VALUE;

	BallSimulation(ArrayList<Ball> balls){
		this.balls = balls;
		setupFirstCollision();
	}

	double currentTime = 0;

	public int iterator = 0; 
	public Image getNextImage(double dt) {

		double endTime = dt + currentTime;
		if(dt < 0) {
			throw new IllegalArgumentException("No going back. At least not yet");
		}

		while(minCollisionTime + currentTime < endTime) {
			Collision last = nextCollisions.get(nextBall);
		
			CollisionLogic.update(balls, last, currentTime);
			iterator++;


			currentTime = minCollisionTime + currentTime;
			minCollisionTime = Double.MAX_VALUE;
			second = false;
			nextCollisionUpdate(last.b1);

			if(last.b2 != Constants.HorizontalWall && last.b2 != Constants.VerticalWall) {
				nextCollisionUpdate(last.b2);
			}

			if(minCollisionTime < 0) System.out.println("uh oh");


		}

	
		CollisionLogic.moveAllBalls(endTime - currentTime, balls);
		minCollisionTime -= endTime - currentTime;
		currentTime = endTime;

		ArrayList<DumbBall> imageSetup = new ArrayList<DumbBall>();
		for(Ball b : balls) {
			imageSetup.add(new DumbBall(b.getPos(), b.getRadius(), b.red, b.green, b.blue));
		}

		return new Image(imageSetup, endTime);
	}
	boolean second = false;

	private void setupFirstCollision() {

		for(Ball b1 : balls){

			Collision c = getNextCollisionWithBall(b1);
			nextCollisions.put(b1, c);
			if(c.absoluteTime - currentTime < minCollisionTime) {
				nextBall = b1;
				minCollisionTime = c.absoluteTime - currentTime;
			}


		}
	}


	private Collision getNextCollisionWithBall(Ball b1) {


		double shortestTime = Double.MAX_VALUE;
		Ball b2 = null;

		double xTime = getXWallTime(b1);
		double yTime = getYWallTime(b1);
		double wallCollisionTime =  xTime < yTime? xTime : yTime;
		if(wallCollisionTime < shortestTime){			
			shortestTime = wallCollisionTime;
			b2 = wallCollisionTime == xTime? Constants.HorizontalWall: Constants.VerticalWall;
		}

		for(Ball test : balls){
			if(b1 != test){
				double time = getMinCollisionTime(b1, test);
				if(time < shortestTime && time > 0){
					shortestTime = time;
					b2 = test;
				}
			}
		}


		return new Collision(b1, b2, currentTime + shortestTime);

	}

	private void nextCollisionUpdate(Ball lastCollision1) {
		
		Collision temp = getNextCollisionWithBall(lastCollision1);
		nextCollisions.put(lastCollision1, temp);

		for(Entry<Ball, Collision> entry : nextCollisions.entrySet()) {
			if(entry.getKey() != lastCollision1){
				if(entry.getValue().b2 == lastCollision1) {
					Collision c = getNextCollisionWithBall(entry.getKey()); 
					entry.setValue(c);
				}

				else {
					double time1 = getMinCollisionTime(entry.getKey(), lastCollision1);
					if(time1 + currentTime < entry.getValue().absoluteTime && time1 > 0) {
						entry.setValue(new Collision(entry.getKey(), lastCollision1, currentTime + time1));
					}
				}
			}

		}
		
		minCollisionTime = Double.MAX_VALUE;
		for(Entry<Ball, Collision> entry : nextCollisions.entrySet()) {
			if(entry.getValue().absoluteTime - currentTime < minCollisionTime) {
				nextBall = entry.getKey();
				minCollisionTime = entry.getValue().absoluteTime - currentTime;				
			}
		}

	}

	double count = 0;
	private Collision getNextCollisionPoint(){

		Ball ball1 = null;
		Ball ball2 = null;
		double shortestTime = Double.MAX_VALUE;
		for(Ball b1 : balls){

			double xTime = getXWallTime(b1);
			double yTime = getYWallTime(b1);
			double wallCollisionTime =  xTime < yTime? xTime : yTime;
			if(wallCollisionTime < shortestTime){			
				shortestTime = wallCollisionTime;
				ball1 = b1;
				ball2 = wallCollisionTime == xTime? Constants.HorizontalWall: Constants.VerticalWall;
			}

			for(Ball b2 : balls){
				if(b1 != b2){
					double time = getMinCollisionTime(b1, b2);
					if(time < shortestTime && time > 0){
						shortestTime = time;
						ball1 = b1;
						ball2 = b2;
					}
				}
			}
		}

		return new Collision(ball1, ball2, currentTime + shortestTime);
	}


	private static double getMinCollisionTime(Ball one, Ball two){

		double j = one.getVel().x - two.getVel().x;
		double k = one.getPos().x - two.getPos().x;
		double m = one.getVel().y - two.getVel().y;
		double n = one.getPos().y - two.getPos().y;
		double totalD = one.getRadius() + two.getRadius();
		double posibilityOne = quadratic(j * j + m * m, 2 * j * k + 2 * m *n, k * k + n * n - totalD * totalD, false);
		double posibilityTwo = quadratic(j * j + m * m, 2 * j * k + 2 * m *n, k * k + n * n - totalD * totalD, true);

		if(Double.isNaN(posibilityOne) && Double.isNaN(posibilityTwo)){
			return Double.MAX_VALUE; //Will never collide
		}
		return posibilityOne < posibilityTwo? posibilityOne : posibilityTwo;
	}

	private static double quadratic(double a, double b, double c, boolean positive){
		return (-b + (positive? 1 : -1) * Math.sqrt(b * b - 4 * a * c)) / (2 * a);
	}

	private static double getXWallTime(Ball b1){
		PhysicalVector2D pos = b1.getPos();
		PhysicalVector2D vel = b1.getVel();
		double tx;
		if(vel.x == 0){
			tx = Double.POSITIVE_INFINITY;
		}
		else if(vel.x < 0){
			tx = (pos.x - b1.getRadius()) / -vel.x;
		}
		else{
			tx = (Constants.xMax - b1.getRadius() - pos.x) / vel.x;
		}
		return tx;
	}

	private static double getYWallTime(Ball b1) {
		PhysicalVector2D pos = b1.getPos();
		PhysicalVector2D vel = b1.getVel();
		double ty;
		if(vel.y == 0){
			ty = Double.POSITIVE_INFINITY;
		}
		else if(vel.y < 0){
			ty = (pos.y - b1.getRadius()) / -vel.y;
		}
		else{
			ty = (Constants.yMax - b1.getRadius() - pos.y) / vel.y;
		}
		return ty;
	}


}

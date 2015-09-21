package simulation;
import java.util.ArrayList;

import localization.Position;
import simulation.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

import geometry.Edge;
import geometry.Triangle;
import geometry.Vertex;
import given.*;

/**
 * TestRobot interfaces to the (real or virtual) robot over a network
 * connection. It uses Java -> JSON -> HttpRequest -> Network -> DssHost32 ->
 * Lokarria(Robulab) -> Core -> MRDS4
 *
 * @author thomasj
 */
public class MyRobot {

	private static final double LOOK_AHEAD_DISTANCE = 50;
	private Path path;
	private RobotCommunication roboCom;
	private Position position;

	public MyRobot(String host, int port) {
		this.position = new Position(0, 0);
		roboCom = new RobotCommunication(host, port);
	}

	public void setPosition(Position position)
	{
		this.position = position;
	}

	public void run() throws Exception {

		boolean done = false;
		while(!done) {

			ArrayList<Edge> carrotPath = path.getCarrotPathFrom(Vertex.fromPosition(getPosition()), LOOK_AHEAD_DISTANCE);
			Vertex carrotPoint = carrotPath.get(carrotPath.size() - 1).end;

			double kp = 1;
			double orientation = getOrientation();
			double carrotAngle = getCarrotAngle(carrotPoint);
			double errorAngle  = (carrotAngle - orientation) * kp;
			
			if(errorAngle > 180) {
				errorAngle -= 360;
			} else if(errorAngle < -180) {
				errorAngle += 360;
			} else {}
			
		}


	}

	private double getCarrotAngle(Vertex carrotPoint) {

		Vertex origo = new Vertex(0, 0);
		Vertex pointOnXAxis 	 = new Vertex(Math.abs(carrotPoint.x), 0);

		Triangle triangle = new Triangle(origo, carrotPoint, pointOnXAxis);
		try {
			double angle = triangle.getAngleInVertex(origo);
			angle = (carrotPoint.y < 0 ? -angle : angle);
			System.out.println("our angle: " + angle);
			return angle;
		} catch (Exception e) {
			return 0;
		}
	}

	private Position getPosition()  {

		LocalizationResponse r = new LocalizationResponse();

		try {
			roboCom.getResponse(r);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return this.position;
		}
		
		double[] posArray = r.getPosition();
		Position p = new Position(posArray[0], posArray[1]);

		return p;
		

	}

	private double getOrientation()  {

		LocalizationResponse r = new LocalizationResponse();

		try {
			roboCom.getResponse(r);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return 0;
		}

		double[] bearing = (new Quaternion(r.getOrientation())).bearing();
		
		Vertex origo = new Vertex(0, 0);
		Vertex v1 = new Vertex(bearing[0], bearing[1]);
		Vertex v2 = new Vertex(Math.abs(bearing[0]), 0);
		
		Triangle triangle = new Triangle(origo, v1, v2);
		try {
			double angle = triangle.getAngleInVertex(origo);
			angle = (bearing[1] < 0 ? -angle : angle);
			System.out.println("our angle: " + angle);
			return angle;
		} catch (Exception e) {
			return 0;
		}

	}


	public void setPath(Path path) {
		this.path = path;
	}

	/**
	 * Extract the robot bearing from the response
	 *
	 * @param lr
	 * @return angle in degrees
	 */
	double getBearingAngle(LocalizationResponse lr) {
		double e[] = lr.getOrientation();

		double angle = 2 * Math.atan2(e[3], e[0]);
		return angle * 180 / Math.PI;
	}

	/**
	 * Extract the position
	 *
	 * @param lr
	 * @return coordinates
	 */
	double[] getPosition(LocalizationResponse lr) {
		return lr.getPosition();
	}

}

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
	private String host;
	private int port;
	private ObjectMapper mapper;
	private RobotCommunication roboCom;
	private Position position;

	public MyRobot(String host, int port) {
		this.host = host;
		this.port = port;
		this.position = new Position(0, 0);
		mapper = new ObjectMapper();
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

			double orientation = getOrientation();
			Vertex carrotPoint = carrotPath.get(carrotPath.size() - 1).end;
			double carrotAngle = getCarrotAngle(carrotPoint);
			double errorAngle;



		}


	}




	private double getCarrotAngle(Vertex carrotPoint) {

		Vertex origo = new Vertex(0, 0);
		Vertex v 	 = new Vertex(carrotPoint.x, 0);

		Triangle t = new Triangle(carrotPoint, origo, v);

		try {
			return t.getAngleInVertex(origo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

		Quaternion q = new Quaternion(r.getOrientation());

		System.out.println(String.format("x: %.1f, y: %.1f, z: %.1f", q.bearing()[0], q.bearing()[1], q.bearing()[2]));

		return q.bearing()[0];

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

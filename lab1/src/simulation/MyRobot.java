package simulation;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;

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
 * @author günzel och marcus
 */
public class MyRobot {

	private static final double LOOK_AHEAD_DISTANCE = 1.0;
	private static final double SPEED = 2.0;
	private static final int SEGEMENTS_PER_IT = 100;
	private static final int MAX_STEERING = 110;
	private Path path;
	private Path takenPath = new Path();
	private RobotCommunication roboCom;
	private Vertex position;
	private double avgEdgeLength;

	public MyRobot(String host, int port) {
		this.position = Vertex.fromPosition(new Position(0, 0));
		roboCom = new RobotCommunication(host, port);
	}

	public void setPosition(Vertex position) {
		this.position = position;
	}

	public void run() throws Exception {

		PrintWriter pw = new PrintWriter("cps.txt", "UTF-8");
		PrintWriter pw2 = new PrintWriter("robotPos.txt", "UTF-8");

		// int edgeIntervalEnd = path.getEdges().size() / 20;
		// double distanceTraveled = 0.0;
		avgEdgeLength = path.avgEdgeLength();

		LocalizationResponse r = new LocalizationResponse();
		roboCom.getResponse(r);
		// Vertex lastPosition = Vertex.fromPosition(getPosition(r));

		boolean done = false;
		while (!done) {

			r = new LocalizationResponse();
			try {
				roboCom.getResponse(r);

				position = Vertex.fromPosition(getPosition(r));

				ArrayList<Edge> carrotPath = path.getCarrotPathFrom(position,
						LOOK_AHEAD_DISTANCE, 0, 100);
				Vertex carrotPoint = carrotPath.get(carrotPath.size() - 1).end;
				pw.println(String.format(Locale.US, "%f %f", carrotPoint.x,
						carrotPoint.y));
				pw2.println(String.format(Locale.US, "%f %f", position.x,
						position.y));

				double orientation = getOrientation(r);
				double errorAngle = getErrorAngle(carrotPoint, orientation);

				if (errorAngle > 180) {
					errorAngle -= 360;
				} else if (errorAngle < -180) {
					errorAngle += 360;
				} else {

				}
				//
				// if(Math.abs(errorAngle) > MAX_STEERING) {
				// errorAngle = 0;
				// }

				double distance = position.distanceTo(carrotPoint);

				DifferentialDriveRequest dr = new DifferentialDriveRequest();

				double speed = Math.max(1,
						1.1 * Math.abs(Math.toRadians(errorAngle)));
				if (Math.abs(errorAngle) > 120) {
					speed = 0;
					errorAngle *= 1.15;
					
				}else{
					if (Math.abs(errorAngle) > 45){
						errorAngle *= 1.35;
					}
				}
				double driveTime = distance / SPEED;

				dr.setLinearSpeed(speed);


				dr.setAngularSpeed(Math.toRadians(errorAngle) / driveTime);
				roboCom.putRequest(dr);

				Thread.sleep((long) (driveTime * 350));
				// dr.setAngularSpeed(0);
				// dr.setLinearSpeed(0);
				//
				// roboCom.putRequest(dr);
				// Thread.sleep(100);

				// distanceTraveled = lastPosition.distanceTo(position);
				// int edgesTraveled = (int) (distanceTraveled / avgEdgeLength);
				// edgeIntervalEnd = edgesTraveled;
				//
				// lastPosition = position;

				boolean closeToFinish = path.getEnd().getDistanceTo(
						carrotPoint.toPosition()) < path.avgEdgeLength() * 2;

				if (isAt(path.getEnd()) && closeToFinish) {
					done = true;
					dr = new DifferentialDriveRequest();
					dr.setAngularSpeed(0);
					dr.setLinearSpeed(0);
					roboCom.putRequest(dr);
				}

			} catch (Exception e) {
				pw.close();
				pw2.close();
				done=true;
			}

		}

		pw.close();
		pw2.close();
	}

	private boolean isAt(Position end) {

		return position.toPosition().getDistanceTo(end) < avgEdgeLength * 20;
	}

	private double getErrorAngle(Vertex carrotPoint, double orientation) {

		double angle1 = Math.toDegrees(Math.atan2(carrotPoint.y - position.y,
				carrotPoint.x - position.x));

		return angle1 - orientation;
	}

	private Position getPosition(LocalizationResponse r) {

		double[] posArray = r.getPosition();
		Position p = new Position(posArray[0], posArray[1]);

		return p;

	}

	private double getOrientation(LocalizationResponse r) {

		double[] bearing = (new Quaternion(r.getOrientation())).bearing();

		Vertex origo = new Vertex(0, 0);
		Vertex v1 = new Vertex(bearing[0], bearing[1]);
		Vertex v2 = new Vertex(Math.abs(bearing[0]), 0);

		Triangle triangle = new Triangle(origo, v1, v2);
		try {
			double angle = triangle.getAngleInVertex(origo);
			angle = (bearing[1] < 0 ? -angle : angle);
			return angle;
		} catch (Exception e) {
			return 0;
		}

	}

	public void record() {
		boolean running = true;

		PrintWriter pw=null;
		while (running) {
			try {
				LocalizationResponse r = new LocalizationResponse();
				roboCom.getResponse(r); 
				Position p1 = getPosition(r);
				if(takenPath.getLastAddedVertex() != null) {
					if(takenPath.getLastAddedVertex().toPosition().getDistanceTo(p1) > 0.01) {
						takenPath.concatPath(new Vertex(p1.x, p1.y));
					}
				} else {
					takenPath.concatPath(new Vertex(p1.x, p1.y));
				}
				
					
				Thread.sleep(20);

			} catch (Exception e) {
//				e.printStackTrace();
				running = false;
			}
		}
		try {
			String json = takenPath.toJson();
			pw = new PrintWriter("path.json");
			pw.write(json);
			pw.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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

}

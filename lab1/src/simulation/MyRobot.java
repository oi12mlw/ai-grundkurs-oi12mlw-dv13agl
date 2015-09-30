package simulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;

import localization.Position;
import simulation.Path;
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

	private double lookAheadDistance;
	private double maxSpeed;
	
	private Path path;
	private Path takenPath = new Path();
	
	private RobotCommunication roboCom;
	private Vertex position;
	private double avgEdgeLength;

	public MyRobot(String host, int port) {
		this.position = Vertex.fromPosition(new Position(0, 0));
		roboCom = new RobotCommunication(host, port);
		setLookAheadDistance(1.00);
		setMaxSpeed(1.00);

	}

	public void run() throws Exception {

		PrintWriter carrotPointWriter = new PrintWriter("cps.txt", "UTF-8");
		PrintWriter robotPathWriter = new PrintWriter("robotPos.txt", "UTF-8");

		LocalizationResponse locResponse = new LocalizationResponse();
		roboCom.getResponse(locResponse);	

		boolean done = false;
		while (!done) {
			locResponse = new LocalizationResponse();
			try {
				
				roboCom.getResponse(locResponse);

				updatePosition(locResponse);
				
				Vertex carrotPoint = getCarrotPoint();
				logPath(carrotPointWriter, robotPathWriter, carrotPoint);

				double errorAngle = getErrorAngle(locResponse, carrotPoint);			
				
				double linearSpeed = maxSpeed;
				long driveTime = 50L;

				if (Math.abs(errorAngle) > 110) {
					linearSpeed = 0.3;
					driveTime += 1000;					
				}				

				double angularSpeed = Math.toRadians(errorAngle)*2.5;
					
				setSpeed(angularSpeed, linearSpeed);

				Thread.sleep(driveTime);

				if (isCloseToFinish(carrotPoint)) {
					done = true;
					stop();
				}

			} catch (Exception e) {
				System.err.println("Communication error");
				done = true;
			}
		}

		carrotPointWriter.close();
		robotPathWriter.close();
	}

	private boolean isCloseToFinish(Vertex carrotPoint) {
		return isAt(path.getEnd()) && path.getEnd().getDistanceTo(carrotPoint.toPosition()) < path.avgEdgeLength() * 2;
	}


	private void setSpeed(double angularSpeed, double linearSpeed)
			throws Exception {
		DifferentialDriveRequest diffDriveRequest = new DifferentialDriveRequest();
		diffDriveRequest.setLinearSpeed(linearSpeed);
		diffDriveRequest.setAngularSpeed(angularSpeed);
		roboCom.putRequest(diffDriveRequest);
	}

	private void logPath(PrintWriter carrotPointWriter,	PrintWriter robotPathWriter, Vertex carrotPoint) {
		
		carrotPointWriter.println(String.format(Locale.US, "%f %f", carrotPoint.x, carrotPoint.y));
		robotPathWriter.println(String.format(Locale.US, "%f %f", position.x, position.y));
		
	}

	private double getErrorAngle(LocalizationResponse r, Vertex carrotPoint) {
		
		double orientation = getOrientation(r);
		double carrotAngle = Math.toDegrees(Math.atan2(carrotPoint.y - position.y,carrotPoint.x - position.x));
		double errorAngle = carrotAngle - orientation;

		if (errorAngle > 180) {
			errorAngle -= 360;
		} else if (errorAngle < -180) {
			errorAngle += 360;
		} else {

		}
		
		return errorAngle;
	}

	private void updatePosition(LocalizationResponse r) {
		position = Vertex.fromPosition(getPosition(r));
	}

	private Vertex getCarrotPoint() {
		ArrayList<Edge> carrotPath = path.getCarrotPathFrom(position, lookAheadDistance, 0, 100);
		Vertex carrotPoint = carrotPath.get(carrotPath.size() - 1).end;
		return carrotPoint;
	}

	private void stop() throws Exception {
		DifferentialDriveRequest dr;
		dr = new DifferentialDriveRequest();
		dr.setAngularSpeed(0);
		dr.setLinearSpeed(0);
		roboCom.putRequest(dr);
	}

	private boolean isAt(Position end) {

		//return position.toPosition().getDistanceTo(end) < avgEdgeLength * 10;
		return position.toPosition().getDistanceTo(end) < 1;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
	public void setLookAheadDistance(double lookAheadDistance) {
		this.lookAheadDistance = lookAheadDistance;
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

	public void record(String pathName) {
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
			pw = new PrintWriter(pathName);
			pw.write(json);
			pw.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.err.println("DONE");
		System.err.println("Recorded " + takenPath.getEdges().size() + 1 + " coordinates");
		

	}

	
	public void setPath(Path path) {
		this.path = path;	
		avgEdgeLength = path.avgEdgeLength();
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

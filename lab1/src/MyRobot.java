import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javafx.geometry.Pos;

import com.fasterxml.jackson.databind.ObjectMapper;

import given.DifferentialDriveRequest;
import given.LocalizationResponse;
import given.RobotCommunication;

/**
 * TestRobot interfaces to the (real or virtual) robot over a network
 * connection. It uses Java -> JSON -> HttpRequest -> Network -> DssHost32 ->
 * Lokarria(Robulab) -> Core -> MRDS4
 * 
 * @author thomasj
 */
public class MyRobot {

	private PathNode[] path;
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

			PathNode[] closestSegment = getClosestSegment();
			double dToClosestSegment = getDistanceToSegment(closestSegment);
			//Position p = getCarrotPoint(closestSegment)
			
			done = true;
			
			
		}
		
		
	}

	double getDistanceToSegment(PathNode[] closestSegment) {
		return distanceToPath(closestSegment[0].pose.position, closestSegment[1].pose.position);
		
	}

	PathNode[] getClosestSegment() throws Exception {
		
		double dToPath = distanceToPath(path[0].pose.position, path[1].pose.position);
		double minDToPath = dToPath;
		PathNode[] closestSegment = new PathNode[2];
		closestSegment[0] = path[0];
		closestSegment[1] = path[1];
		
		for (int i = 0; i < path.length - 1; i++) {
			
			Position p0 = path[i].pose.position;
			Position p1 = path[i+1].pose.position;
			dToPath = distanceToPath(p0, p1);
							
			if (dToPath < minDToPath) {
				minDToPath = dToPath;
				closestSegment[0] = path[i];
				closestSegment[1] = path[i+1];
			}
		}
		
		return closestSegment;
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

	private double distanceToPath(Position p0, Position p1) {
		
		Position p = getPosition();
		
		double vX = p1.x - p0.x;
		double vY = p1.y - p0.y;
		
		double wX = p.x - p0.x;
		double wY = p.y - p0.y;
		
		double c1 = wX*vX + wY*vY;
		
		if (c1 <= 0) {
			return p.getDistanceTo(p0);
		}
		
		
		double c2 = vX*vX + vY*vY;
		if ( c2 <= c1) {
			return p.getDistanceTo(p1);
		}
		
		double b = c1/c2;
		
		Position pB = new Position(p0.x + b*vX,  p0.y + b*vY);

		
		return p.getDistanceTo(pB);
	}
	
		

	public void setPath(PathNode[] path) {
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

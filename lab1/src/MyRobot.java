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

	public MyRobot(String host, int port) {
		this.host = host;
		this.port = port;
		mapper = new ObjectMapper();
		roboCom = new RobotCommunication(host, port);
	}

	public void run() throws Exception {

		
		boolean done = false;
		while(!done) {

			double dToPath = 0;
			double maxDToPath = dToPath;
			
			for (int i = 0; i < path.length - 1; i++) {
				
				Position p0 = path[i].pose.position;
				Position p1 = path[i+1].pose.position;
				dToPath = distanceToPath(getPosition(), p0, p1);
				
				if (dToPath > maxDToPath) {
					maxDToPath = dToPath;
				}
			}
			
				
			
			
		}
		
		
	}



	
	private Position getPosition() throws Exception {
		
		LocalizationResponse r = new LocalizationResponse();
		
		roboCom.getResponse(r);
		
		Position p = new Position();
		
		double[] posArray = r.getPosition();
		
		p.x = posArray[0];
		p.y = posArray[1];
		
		return p;
		
	}

	private double distanceToPath(Position p, Position p0, Position p1) {
		
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
		
		Position pB = new Position();
		pB.x = p0.x + b*vX;
		pB.y = p0.y + b*vY;
		
		return p.getDistanceTo(pB);
	}
	
	public void followTheCarrot(){
		
		
		
		
		
		
		
		
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

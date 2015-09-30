package simulation;
import geometry.Edge;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import localization.PathNode;
import localization.PathParser;

/**
 *
 * @author dv13agl
 * @author oi12mlw
 */
public class Main {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {

		MyRobot robot = new MyRobot("http://127.0.0.1", 50000);
		if(args.length == 3) {
			
			String pathFile = args[0];
			double lookAheadDistance = Double.parseDouble(args[1]);
			double maxSpeed = Double.parseDouble(args[2]);

			run(robot, pathFile, lookAheadDistance, maxSpeed);	
			
		} else if (args.length == 1){
			System.err.println("No path file specified. Recording new path to " + args[0]);
			recordPath(robot, args[0]);
		}

		System.err.println("Exiting...");
		
	}

	private static void run(MyRobot robot, String pathFile, double lookAheadDistance, double maxSpeed) {
		
		System.err.print("Reading path " + pathFile +"... ");
		
	
	
		PathParser parser;
		try {
			parser = new PathParser(pathFile);
			PathNode[] pathNodes = parser.getPath();
			Path path =  Path.fromPathNodes(pathNodes);		
			writePath(path);
			robot.setPath(path);
		} catch (FileNotFoundException e) {
			System.err.println("Could not find path file");
			return;
		} catch (JsonParseException e) {
			System.err.println("Could not parse path file");
		} catch (JsonMappingException e) {
			System.err.println("Could not parse path file");
		} catch (IOException e) {
			System.err.println("Error reading path file");
		}
		
		System.err.println("Done!");
		
		
		System.err.println(String.format("Setting look-ahead-distance to %.1f", lookAheadDistance));
		System.err.println(String.format("Setting max speed to %.1f", maxSpeed));
		robot.setLookAheadDistance(lookAheadDistance);
		robot.setMaxSpeed(maxSpeed);
		
		countdown();
		
		System.err.println("RUNNING");
		double currentTime = System.nanoTime();
		try {
			robot.run();
		} catch (Exception e) {
			System.out.println("Unknown error");
		}
		double elapsed = System.nanoTime() - currentTime;
		
		System.err.println("DONE");
		System.err.println("Elapsed: " + elapsed / 1000000000.0 + " s");
	}

	private static void countdown() {

		System.err.println("Starting in...");
		try {
			System.err.println("3...");
			Thread.sleep(1000);
			System.err.println("2...");
			Thread.sleep(1000);
			System.err.println("1...");
			Thread.sleep(1000);
	
		} catch (Exception e) {
			
		}	
		
		
	}

	private static void recordPath(MyRobot robot, String pathName) {
		countdown();
		System.err.println("RECORDING");
		robot.record(pathName);
	}

	private static void writePath(Path path) throws FileNotFoundException, UnsupportedEncodingException {

		PrintWriter pw = new PrintWriter("path.txt");
		
		for(Edge e : path.getEdges()) {
			
			pw.println(String.format(Locale.US, "%.6f %.6f", e.start.x, e.start.y));
			
			
		}
		pw.close();
		
		
		
	}

}

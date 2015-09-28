package simulation;
import geometry.Edge;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

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
	public static void main(String[] args) throws Exception {

		//String pathFile = args[0];
		String pathFile = PathConstants.PATH;
		MyRobot robot = new MyRobot("http://127.0.0.1", 50000);

		Thread.sleep(2000);
		
		if(pathFile == null) {
			recordPath(robot);	
		} else {
			run(robot, pathFile);	
		}
	
	}

	private static void run(MyRobot robot, String pathFile) throws Exception {
		
		System.out.println("RUNNING");
		PathParser parser = new PathParser(pathFile);
		PathNode[] pathNodes = parser.getPath();
		Path path =  Path.fromPathNodes(pathNodes);
		writePath(path);
		robot.setPath(path);
		double currentTime = System.nanoTime();
		robot.run();
		double elapsed = System.nanoTime() - currentTime;
		
		System.out.println("DONE");
		System.out.println("Elapsed: " + elapsed / 1000000000.0);
	}

	private static void recordPath(MyRobot robot) {
		System.out.println("RECORDING");
		robot.record();
		System.out.println("DONE");
	}

	private static void writePath(Path path) throws FileNotFoundException, UnsupportedEncodingException {

		PrintWriter pw = new PrintWriter("path.txt");
		
		for(Edge e : path.getEdges()) {
			
			pw.println(String.format(Locale.US, "%.6f %.6f", e.start.x, e.start.y));
			
			
		}
		pw.close();
		
		
		
	}

}

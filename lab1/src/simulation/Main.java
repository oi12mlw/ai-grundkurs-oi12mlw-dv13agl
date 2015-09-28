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

		PathParser parser = new PathParser(args[0]);
		PathNode[] pathNodes = parser.getPath();

		Path path =  Path.fromPathNodes(pathNodes);

		//writePath(path);
		
		MyRobot robot = new MyRobot("http://127.0.0.1", 50000);
		robot.setPath(path);

		Thread.sleep(3000);
		double currentTime = System.nanoTime();
		
		robot.run();
		
		double elapsed = System.nanoTime() - currentTime;
		
		System.out.println("Elapsed: " + elapsed / 1000000000);
	}

	private static void writePath(Path path) throws FileNotFoundException, UnsupportedEncodingException {

		PrintWriter pw = new PrintWriter("path.txt");
		
		for(Edge e : path.getEdges()) {
			
			pw.println(String.format(Locale.US, "%.6f %.6f", e.start.x, e.start.y));
			
			
		}
		pw.close();
		
		
		
	}

}

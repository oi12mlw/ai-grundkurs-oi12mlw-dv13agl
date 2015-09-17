import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		PathNode[] path = parser.getPath();
		MyRobot robot = new MyRobot("http://127.0.0.1", 50000);
		robot.setPath(path);

		printToFile(path);
		//robot.run();
	}

	private static void printToFile(PathNode[] path) throws IOException {
		PrintWriter writer = new PrintWriter("path.txt", "UTF-8");

		for(PathNode p : path) {
			writer.println(p.pose.position.x + " " + p.pose.position.y);
		}

		writer.close();


	}

}

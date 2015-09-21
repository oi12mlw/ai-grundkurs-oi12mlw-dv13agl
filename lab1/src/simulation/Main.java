package simulation;
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

		MyRobot robot = new MyRobot("http://127.0.0.1", 50000);
		robot.setPath(path);

		robot.run();
	}

}

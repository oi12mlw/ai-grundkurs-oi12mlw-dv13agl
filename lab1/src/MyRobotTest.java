import static org.junit.Assert.*;

import org.junit.Test;


public class MyRobotTest {

	@Test
	public void test() throws Exception {
		MyRobot robot = new MyRobot("https:127.0.0.1", 5000);
		
		PathNode[] path = new PathNode[3];

		Position p0 = new Position(0, 0);
		Position p1 = new Position(2, 2);
		Position p2 = new Position(6, 2);
		Position pr = new Position(1, 5);

		path[0] = new PathNode();
		path[1] = new PathNode();
		path[2] = new PathNode();

		path[0].pose.position = p0;
		path[1].pose.position = p1;
		path[2].pose.position = p2;
		
		robot.setPath(path);
		robot.setPosition(pr);
		PathNode[] segment = robot.getClosestSegment();
		
		System.out.println("------RESULT------");

		System.out.println(segment[0].pose.position.toString());
		System.out.println(segment[1].pose.position.toString());
		
		double d = robot.getDistanceToSegment(segment);
		
		System.out.println(d);
		
		
	}

}

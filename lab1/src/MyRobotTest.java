import static org.junit.Assert.*;

import org.junit.Test;


public class MyRobotTest {

	@Test
	public void test() throws Exception {
		MyRobot robot = new MyRobot("https://127.0.0.1", 50000);

		PathNode[] path = new PathNode[3];

		Position p0 = new Position(2, 0);
		Position p1 = new Position(2 , 3);
		Position p2 = new Position(0, 6);
		Position pr = new Position(2, 0);

		path[0] = new PathNode();
		path[1] = new PathNode();
		path[2] = new PathNode();

		path[0].pose.position = p0;
		path[1].pose.position = p1;
		path[2].pose.position = p2;

		robot.setPath(path);
		robot.setPosition(pr);

		PathNode[] asd = robot.getClosestSegment();
		System.out.println("---Closest segment---");
		System.out.println("p0: " + asd[0].pose.position.toString());
		System.out.println("p1: " + asd[1].pose.position.toString());

		Position p = robot.getClosestPointOnSegement(asd);
		System.out.println("---Closest point---");
		System.out.println("p: " + p);


	}

}

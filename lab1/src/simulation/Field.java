package simulation;

import geometry.Edge;
import geometry.Vertex;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by marcus on 2015-09-19.
 */
public class Field {

    private Path path;
    private Vertex robotPosition = null;
    private boolean robotPositionLocked;

    public Field() {
        this.path = new Path();
    }

    public void setRobotPosition(Vertex robotPosition) {

        if(robotPositionLocked) return;

        this.robotPosition = robotPosition;
    }

    public Vertex getRobotPosition() {
        return this.robotPosition;
    }

    public Path getPath() {
        return this.path;
    }

    public void clear() {
        path.clear();
        robotPosition = null;
    }

    public void invertRobotPositionLock() {
        robotPositionLocked = !robotPositionLocked;
    }

    public boolean isRobotPositionLocked() {
        return robotPositionLocked;
    }

    public void setRobotPositionLocked(boolean robotPositionLocked) {
        this.robotPositionLocked = robotPositionLocked;
    }
}

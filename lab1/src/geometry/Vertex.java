package geometry;

/**
 * Created by marcus on 2015-09-18.
 */
public class Vertex {

    public double x;
    public double y;

    public Vertex(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Vertex) {
            return (((Vertex) obj).x == x && ((Vertex) obj).y == y);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public double distanceTo(Vertex v) {
        return Math.hypot(x - v.x, y- v.y);
    }

    public Vertex differenceTo(Vertex vertex) {
        return new Vertex(x - vertex.x, y - vertex.y);
    }


    public double dotProduct(Vertex vertex) {
        return x*vertex.x + y*vertex.y;
    }


    public Vertex scale(double scalar) {
        return new Vertex(x*scalar, y*scalar);
    }

    public Vertex concat(Vertex vertex) {
        return new Vertex(x + vertex.x, y + vertex.y);
    }
}

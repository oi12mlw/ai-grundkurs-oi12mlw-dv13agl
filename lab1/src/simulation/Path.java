package simulation;

import geometry.Edge;
import geometry.Vertex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import localization.Orientation;
import localization.PathNode;
import localization.Pose;
import localization.Position;


/**
 * Created by marcus on 2015-09-20.
 */
public class Path {

    private CopyOnWriteArrayList<Edge> edges;
    private Vertex pathStart = null;
    private Vertex lastAddedVertex = null;

    public Path() {
        edges = new CopyOnWriteArrayList<Edge>();
    }

    public Vertex getLastAddedVertex() {
        return lastAddedVertex;
    }

    public void concatPath(Vertex vertex) {
        if(lastAddedVertex == null) {
            lastAddedVertex = vertex;
            pathStart = vertex;
        } else {
            edges.add(new Edge(lastAddedVertex, vertex));
            lastAddedVertex =  vertex;
        }
    }

    private int getIndexOfClosestEdgeToVertex(Vertex vertex, int edgeIntervalStart, int edgeIntervalEnd) {
        if(!edges.isEmpty()) {

            Vertex closestVertexOnEdge = edges.get(0).getClosestVertexOnEdge(vertex);
            double minD = vertex.distanceTo(closestVertexOnEdge);
            int index = 0;

            if(edgeIntervalEnd > edges.size()) {
            	edgeIntervalEnd = edges.size();
            }
            
            for(int i = edgeIntervalStart; i < edgeIntervalEnd; i++) {

                closestVertexOnEdge = edges.get(i).getClosestVertexOnEdge(vertex);
                double d    = vertex.distanceTo(closestVertexOnEdge);
                minD        = (d <= minD ? d : minD);
                index       = (d <= minD ? i : index);

            }

            return index;
        }

        return -1;
    }
    
    
    
    public String toJson(){
    	
    	String json = "";
    	
    	PathNode[] pathNodes = new PathNode[edges.size()];
    	
    	for(int i = 0 ; i < edges.size(); i++) {
    		
    		Vertex v = edges.get(i).start;
    		pathNodes[i] = new PathNode();
    		pathNodes[i].pose = new Pose();
    		pathNodes[i].status = "4";
    		pathNodes[i].timestamp = "0";
    		pathNodes[i].pose.orientation = new Orientation();
    		pathNodes[i].pose.position = new Position(v.x, v.y);
    		
    		
    	}
    	
    	
    	//1. Convert Java object to JSON format
    	ObjectMapper mapper = new ObjectMapper();
    	
    	try {
			json = mapper.writeValueAsString(pathNodes);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return json;	
    	
    }
    
    

    public Edge getClosestEdgeToVertex(Vertex vertex, int edgeIntervalStart, int edgeIntervalEnd) {
        int index = getIndexOfClosestEdgeToVertex(vertex, edgeIntervalStart, edgeIntervalEnd);
        return (index >= 0 ? edges.get(index) : null);
    }

    public ArrayList<Edge> getCarrotPathFrom(Vertex vertex, double lookAheadDistance, int edgeIntervalStart, int edgeIntervalEnd) {
        ArrayList<Edge> carrotPath = new ArrayList<Edge>();

        int indexOfStartEdge = getIndexOfClosestEdgeToVertex(vertex, edgeIntervalStart, edgeIntervalEnd);
        if(indexOfStartEdge < 0) {
            return carrotPath;
        }
        Edge startEdge = edges.get(indexOfStartEdge);
        Vertex start = startEdge.getClosestVertexOnEdge(vertex);

        double remainingDistance = lookAheadDistance;

        // Loop until remaining distance is 0 (carrot point has been reached) or until the end of the path has been reached
        for(int i = indexOfStartEdge; i < edges.size() && remainingDistance > 0; i++) {

            Edge currentEdge = i == indexOfStartEdge ? new Edge(start, startEdge.end) : edges.get(i);
            Vertex end = currentEdge.getVertexAlongEdgeAtDistance(remainingDistance);

            // If the distance to the supposed end-point from the current start point exceeds the current edges end point
            if(start.distanceTo(end) > start.distanceTo(currentEdge.end)) {
                end = currentEdge.end;

                // if the current edge is the last one, we have arrived
                if(i == edges.size() - 1) { // if last edge of path
                    remainingDistance = 0;
                    // else subtract the distance from the current start point to the end of the current edge
                } else {
                    remainingDistance-= start.distanceTo(end);

                }
                // else we have arrived
            } else {
                if(!start.equals(end)) {
                    remainingDistance = 0;
                }
            }

            // Add the edge to the carrotPath
            carrotPath.add(new Edge(start, end));

            start = currentEdge.end;

        }
        
        removeBefore(indexOfStartEdge);

        return carrotPath;


    }

    private void removeBefore(int indexOfStartEdge) {
		
    	for(int i = 0; i < Math.abs(indexOfStartEdge); i++) {
    		edges.remove(0);	
    	}
    	
    	
		
	}

	public String[] getPathAsStringArray() {
        String[] strings = new String[edges.size()];

        for (int i = 0; i < edges.size(); i++) {
            strings[i] = edges.get(i).toString();
        }

        return strings;
    }


    public void clear() {
        edges.clear();
        pathStart = null;
        lastAddedVertex = null;
    }

    public CopyOnWriteArrayList<Edge> getEdges() {
        return edges;
    }

    public Vertex getStart() {
        return pathStart;
    }

	public static Path fromPathNodes(PathNode[] pathNodes) {

		Path path = new Path();

		for(int i = 0; i < pathNodes.length; i++) {

			Position p = pathNodes[i].pose.position;

			Vertex start = new Vertex(p.x, p.y);
			path.concatPath(start);

		}

		return path;

	}
	
	public double lengthOfPath() {
		
		double length = 0;
		
		for(Edge e : edges) {
			
			length += e.start.distanceTo(e.end);
		}
		
		return length;
	}
	
	public double avgEdgeLength() {
		return lengthOfPath() / edges.size();
	}

	public Position getEnd() {
		
		return lastAddedVertex.toPosition();
	}
}

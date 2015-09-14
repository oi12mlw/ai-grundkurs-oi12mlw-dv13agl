import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;


public class PathParser {

	BufferedReader reader;
	ObjectMapper mapper;
	
	public PathParser(String filePath) throws FileNotFoundException {
		
		reader = new BufferedReader(new FileReader(filePath));
		mapper = new ObjectMapper();
	}

	public ArrayList<PathNode> getPoseList() throws JsonParseException, JsonMappingException, IOException  {
		
		
		CollectionType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, PathNode.class);
		
		ArrayList<PathNode> pathList = mapper.readValue(reader, type);
		
//		Iterator<PathNode> it = pathList.iterator();
//		
//		while(it.hasNext()) {
//			PathNode p = it.next();
//			System.out.println("x: " + p.pose.position.x + " y: " + p.pose.position.y);
//		}
		
		return pathList;
		
	}
	
}

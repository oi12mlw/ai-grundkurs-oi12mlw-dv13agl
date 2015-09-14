import com.fasterxml.jackson.annotation.JsonProperty;


public class Pose {
	
	@JsonProperty("Orientation")
	public Orientation orientation;
	@JsonProperty("Position")
	public Position position;
	
}

import com.fasterxml.jackson.annotation.JsonProperty;


public class Position {


	@JsonProperty("X")
	public double x;

	@JsonProperty("Y")
	public double y;

	@JsonProperty("Z")
	public double z;
	
}

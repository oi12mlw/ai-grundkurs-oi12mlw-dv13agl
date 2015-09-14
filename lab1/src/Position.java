import com.fasterxml.jackson.annotation.JsonProperty;


public class Position {


	@JsonProperty("X")
	public double x;

	@JsonProperty("Y")
	public double y;

	@JsonProperty("Z")
	public double z;

	public double getAngleTo(Position dest) {
		
		return Math.atan2(dest.y-y, dest.x-x);

	}

	public double getDistanceTo(Position dest) {
		 
		return Math.sqrt(Math.pow(dest.x-x, 2)+Math.pow(dest.y-y,2));
	}
	
}

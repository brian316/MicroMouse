

public class Direction { 
	
	
	static String GobalNorth = "N";
	
	static String GlobalEast = "E";
	
	static String GlobalSouth = "S";
	
	static String GlobalWest = "W";
	
	static String mouseHead = "N";
	
	
	public static String getHeadDirection(boolean up, boolean right, boolean down, boolean left) {
		
		if(mouseHead.equals(GobalNorth) && up) {
			mouseHead = "N";
		} else if (mouseHead.equals(GobalNorth) && right) {
			mouseHead = "E";
		} else if (mouseHead.equals(GobalNorth) && left) {
			mouseHead = "W";
		} else if (mouseHead.equals(GobalNorth) && down) {
			mouseHead = "S";
		} else if (mouseHead.equals(GlobalEast) && up) {
			mouseHead = "N";
		} else if (mouseHead.equals(GlobalEast) && right) {
			mouseHead = "E";
		} else if (mouseHead.equals(GlobalEast) && left) {
			mouseHead = "W";
		} else if (mouseHead.equals(GlobalEast) && down) {
			mouseHead = "S";
		} else if (mouseHead.equals(GlobalSouth) && up) {
			mouseHead = "N";
		} else if (mouseHead.equals(GlobalSouth) && right) {
			mouseHead = "E";
		} else if (mouseHead.equals(GlobalSouth) && left) {
			mouseHead = "W";
		} else if (mouseHead.equals(GlobalSouth) && down) {
			mouseHead = "S";
		} else if (mouseHead.equals(GlobalWest) && up) {
			mouseHead = "N";
		} else if (mouseHead.equals(GlobalWest) && right) {
			mouseHead = "E";
		} else if (mouseHead.equals(GlobalWest) && left) {
			mouseHead = "W";
		} else if (mouseHead.equals(GlobalWest) && down) {
			mouseHead = "S";
		}
		return mouseHead;
	}
}

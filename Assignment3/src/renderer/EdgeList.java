package renderer;

/**
 * EdgeList should store the data for the edge list of a single polygon in your
 * scene. A few method stubs have been provided so that it can be tested, but
 * you'll need to fill in all the details.
 *
 * You'll probably want to add some setters as well as getters or, for example,
 * an addRow(y, xLeft, xRight, zLeft, zRight) method.
 */

public class EdgeList {
	public int startY;
	public int endY;
	public float edges[][] ; //0,1,2,3 left and right of x and z
	
	
	public EdgeList(int startY, int endY) {
		// TODO fill this in.
		this.startY = startY;
		this.endY = endY;
		this.edges = new float[endY-startY+1][4];
	}

	public int getStartY() {
		// TODO fill this in.
		return startY;
	}

	public int getEndY() {
		// TODO fill this in.
		return endY;
		//return 0;
	}
	
	public void setLeftX(int y, float edge) {
		edges[y-startY][0] = edge;
	}
	public void setRightX(int y, float edge) {
		edges[y-startY][1] = edge;
	}
	public void setLeftZ(int y, float edge) {
		edges[y-startY][2] = edge;
	}
	public void setRightZ(int y, float edge) {
		edges[y-startY][3] = edge;
	}
	

	public float getLeftX(int y) { //the minimum x
		// TODO fill this in.
		//return 0;
		return edges[y-startY][0];
	}
	public float getRightX(int y) { //the maximum x
		// TODO fill this in.
		//return 0;
		return edges[y-startY][1];
	}

	public float getLeftZ(int y) { //the minimum z
		// TODO fill this in.
		//return 0;
		return edges[y-startY][2];
	}

	public float getRightZ(int y) { //the maximum z
		// TODO fill this in.
		//return 0;
		return edges[y-startY][3];
	}
	
	//private something addRow(y, xLeft, xRight, zLeft, zRight)
}

// code for comp261 assignments

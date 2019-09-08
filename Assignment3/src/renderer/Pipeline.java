package renderer;

import java.awt.Color;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import renderer.Scene.Polygon;

/**
 * The Pipeline class has method stubs for all the major components of the
 * rendering pipeline, for you to fill in.
 * 
 * Some of these methods can get quite long, in which case you should strongly
 * consider moving them out into their own file. You'll need to update the
 * imports in the test suite if you do.
 */
public class Pipeline {

	/**
	 * Returns true if the given polygon is facing away from the camera (and so
	 * should be hidden), and false otherwise.
	 */
	public static boolean isHidden(Polygon poly) {
		// TODO fill this in.		
		//return false;
		//if(poly.vertices[])
		
		Vector3D[] vertices = poly.vertices;
		Vector3D normalOfPolygon= (vertices[1].minus(vertices[0])).crossProduct(vertices[2].minus(vertices[1]));
		//normal = (v2-v1).crossProduct(v3-v2)
		
		if(normalOfPolygon.z < 0) {
			return false; //it is not hidden if z or normal is negative 
		}
		else {
			return true;
		}
	}

	/**
	 * Computes the colour of a polygon on the screen, once the lights, their
	 * angles relative to the polygon's face, and the reflectance of the polygon
	 * have been accounted for.
	 * 
	 * @param lightDirection
	 *            The Vector3D pointing to the directional light read in from
	 *            the file.
	 * @param lightColor
	 *            The color of that directional light.
	 * @param ambientLight
	 *            The ambient light in the scene, i.e. light that doesn't depend
	 *            on the direction.
	 */
	public static Color getShading(Polygon poly, Vector3D lightDirection, Color lightColor, Color ambientLight) {		
		Vector3D[] VerticesPoly = poly.vertices; //creates the polygon
		Vector3D normalOfPolygon = (VerticesPoly[1].minus(VerticesPoly[0])).crossProduct(VerticesPoly[2].minus(VerticesPoly[1])); //gets the normal of the polygon
		Color reflectance = poly.getReflectance();
		
		float cosTheta = normalOfPolygon.cosTheta(lightDirection);

		int shadingR, shadingG, shadingB;

		if (cosTheta < 0) {
			cosTheta = 0; 
		}		

		//referencing the lecture slides
		float ambientLightRed = ambientLight.getRed() / 255f;
		float ambientLightBlue = ambientLight.getBlue() / 255f;
		float ambientLightGreen = ambientLight.getRed() / 255f;

		float lightColorRed = lightColor.getRed() / 255f;
		float lightColorBlue = lightColor.getBlue() / 255f;
		float lightColorGreen = lightColor.getGreen() / 255f;
		lightColor = new Color(lightColorRed, lightColorBlue, lightColorGreen);
		
		shadingR = (int) (ambientLightRed * reflectance.getRed() + (lightColorRed * reflectance.getRed() * cosTheta));
		shadingG = (int) (ambientLightGreen * reflectance.getGreen() + (lightColorGreen * reflectance.getGreen() * cosTheta));
		shadingB = (int) (ambientLightBlue * reflectance.getBlue() + (lightColorBlue * reflectance.getBlue() * cosTheta));

		if(shadingR>255) shadingR = 255;
		if(shadingG>255) shadingG = 255;
		if(shadingB>255) shadingB = 255;

		return new Color(shadingR, shadingG, shadingB);
	}
	

	/**
	 * This method should rotate the polygons and light such that the viewer is
	 * looking down the Z-axis. The idea is that it returns an entirely new
	 * Scene object, filled with new Polygons, that have been rotated.
	 * 
	 * @param scene
	 *            The original Scene.
	 * @param xRot
	 *            An angle describing the viewer's rotation in the YZ-plane (i.e
	 *            around the X-axis).
	 * @param yRot
	 *            An angle describing the viewer's rotation in the XZ-plane (i.e
	 *            around the Y-axis).
	 * @return A new Scene where all the polygons and the light source have been
	 *         rotated accordingly.
	 */
	public static Scene rotateScene(Scene scene, float xRot, float yRot) {
		// TODO fill this in.
		Transform rotationMatrix = Transform.newXRotation(xRot).compose(Transform.newYRotation(yRot));
		ArrayList<Polygon> polys = new ArrayList<>();		
		for(Polygon p : scene.getPolygons()) {
			Vector3D[] verts = p.getVertices();
			for(int i = 0; i<=2; i++) {
				verts[i] = rotationMatrix.multiply(verts[i]); //multiply each vertex by the rotation matrix 				
			}
			Polygon newPolygon = new Polygon(verts[0], verts[1], verts[2], p.getReflectance());
			polys.add(newPolygon); //add these new polygons to a list
			
			
		}
		
		Vector3D rotLight = rotationMatrix.multiply(scene.getLight()); //rotate the light source

		
		Scene nextScene = new Scene(polys,rotLight); //make a new scene after rotating
		Scene scaleScale = scaleScene(nextScene); //get that scene after rotating the scale it
		Scene translatedScene = translateScene(scaleScale); //then translate this scene finally
		
		return translatedScene;
		
		
		//return nextScene;
		//return null;
		
		
	}

	/**
	 * This should translate the scene by the appropriate amount.
	 * 
	 * @param scene
	 * @return
	 */
	public static Scene translateScene(Scene scene) {
		// TODO fill this in.
		//return null;
		float xMin = Float.MAX_VALUE;
		float yMin = Float.MAX_VALUE;
	
		for(Polygon p : scene.getPolygons()) { 
			for(Vector3D vert : p.vertices) {
				if(vert.x < xMin) {
					xMin = Math.round(vert.x); //find the min x value for all vertices in polygons
				}
				if(vert.y < yMin) {
					yMin = Math.round(vert.y); //find the min y value for all vertices in polygons
				}
			}
		}
		
		Transform translation = Transform.newTranslation(-xMin, -yMin, 0);
		ArrayList<Polygon> polys = new ArrayList<>();
		
		for(Polygon p : scene.getPolygons()) {
			Vector3D[] verts = p.getVertices(); //for all verticies of each polygon
			for(int i = 0; i<=2; i++) {
				verts[i] = translation.multiply(verts[i]); //multiply the vertix by the translation matrix
			}
			polys.add(new Polygon(verts[0], verts[1], verts[2], p.getReflectance())); //add these new polygons into  the next scene		
		}
		
		//Vector3D translatedLight = translation.multiply(scene.getLight());
		Scene translatedScene = new Scene(polys, scene.getLight()); //new scene
		
		return translatedScene;
	}

	/**
	 * This should scale the scene.
	 * 
	 * @param scene
	 * @return
	 */
	public static Scene scaleScene(Scene scene) {
		// TODO fill this in.
		//return null;
		
		float xMin = Float.MAX_VALUE;
		float yMin = Float.MAX_VALUE;
		float xMax = Float.MIN_VALUE;
		float yMax = Float.MIN_VALUE;
		
		for(Polygon p : scene.getPolygons()) {
			for(Vector3D vert : p.vertices) {
				if(vert.x < xMin) {
					xMin = Math.round(vert.x); //find the min x value for all vertices in polygons
				}
				if(vert.y < yMin) {
					yMin = Math.round(vert.y); //find the min y value for all vertices in polygons
				}
				if(vert.x > xMax) {
					xMax = Math.round(vert.x); //find the max x value for all vertices in polygons
				}
				if(vert.y > yMax) {
					yMax = Math.round(vert.y); //find the max y value for all vertices in polygons
				}
				
			}
		}
		
		float boundHeight = yMax - yMin;
		float boundWidth = xMax - xMin;
		
		if(boundHeight <= 0) { boundHeight = 1; } //so we do not divide by 0
		if(boundWidth <= 0) { boundWidth = 1; } //so we do not divide by 0
		
		float xScale = (GUI.CANVAS_WIDTH) / (boundWidth); //scaling by the screen width so that it scales inside it perfectly
		float yScale = (GUI.CANVAS_HEIGHT) / (boundHeight);
		
		float scale = Math.abs(Math.min(xScale, yScale)); //we use whatever had the smallest value and make it positive
		
		if(scale == 1.0f) { return scene; } //doesnt need to scale if the scale is the same
		
		Transform newScale = Transform.newScale(scale, scale, scale);
		ArrayList<Polygon> polys = new ArrayList<>();
		
		for(Polygon p : scene.getPolygons()) {
			Vector3D[] verts = p.getVertices();
			for(int i = 0; i<=2; i++) {
				verts[i] = newScale.multiply(verts[i]); //multiply this current vertix by the scale vector to scale it back after translating 
			}
			polys.add(new Polygon(verts[0], verts[1], verts[2], p.getReflectance()));			
		}		
		
		//Vector3D scaleLight = newScale.multiply(scene.getLight());
		Scene scaleScene = new Scene(polys, scene.getLight()); //add this new scene
		
		return scaleScene;
		
	
		
	}

	/**
	 * Computes the edgelist of a single provided polygon, as per the lecture
	 * slides.
	 */
	public static EdgeList computeEdgeList(Polygon poly) {
		Vector3D[] vectors = Arrays.copyOf(poly.getVertices(), 3); //got help for a more effecient way to write
		//instead of writing vector3d v1  = vectors[0] etc etc

		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;

		for (Vector3D v : vectors) {
			if (v.y > maxY) { //y is greater than the maxY then round that y and then reassign to the maxY
				maxY = Math.round(v.y);
			}
			if (v.y < minY) { //if y is less than the minY round it and then reassign it to the minY
				minY = Math.round(v.y);
			}
		}
		EdgeList edgeList = new EdgeList(minY, maxY); //create a new edgelist based off that minY and maxY


		for (int i = 0; i < poly.getVertices().length; i++) { //while its less than the number of vertices
			Vector3D a = vectors[i]; //a is the start position or start node
			Vector3D b = vectors[(i + 1) % 3]; //b or end position or end node. This is done so we dont go over 3

			
			float ax = Math.round(a.x);  //rounding floats for accuracy
			float bx = Math.round(b.x);
			float az = Math.round(a.z);
			float bz = Math.round(b.z);
			float ay = Math.round(a.y);
			float by = Math.round(b.y);
			
			float slopeX = (bx - ax) / (by - ay); //x slope we incremeent or decrement 
			float slopeZ = (bz - az) / (by - ay); //y slope we incremeent or decrement 

			float x = ax;
			int y = Math.round(ay);
			float z = az;

			if (ay < by) { //if the y of the first one is greater than the second one we are going down and there for incrementing x and z
				while (y <= Math.round(by)) {
					//edgeList.setLeftX((int)y, x);
					//edgeList.setLeftZ((int)y, z);
					edgeList.edges[y-edgeList.startY][0] = x; //rather than using setters i accessed the edgelist from the classes straight away
					edgeList.edges[y-edgeList.startY][2] = z;
					x += slopeX; //incrment x by slope
					z += slopeZ; //incrment z by slope
					y++; //increment to the next y int
				}
			} else {
				while (y >= Math.round(by)) { //same process but decrement
					//edgeList.setLeftX((int)y, x);
					//edgeList.setLeftZ((int)y, z);
					edgeList.edges[y-edgeList.startY][1] = x;
					edgeList.edges[y-edgeList.startY][3] = z;
					x -= slopeX;
					z -= slopeZ;
					y--;
				}
			}
		}
		return edgeList;	
	}

	/**
	 * Fills a zbuffer with the contents of a single edge list according to the
	 * lecture slides.
	 * 
	 * The idea here is to make zbuffer and zdepth arrays in your main loop, and
	 * pass them into the method to be modified.
	 * 
	 * @param zbuffer
	 *            A double array of colours representing the Color at each pixel
	 *            so far.
	 * @param zdepth
	 *            A double array of floats storing the z-value of each pixel
	 *            that has been coloured in so far.
	 * @param polyEdgeList
	 *            The edgelist of the polygon to add into the zbuffer.
	 * @param polyColor
	 *            The colour of the polygon to add into the zbuffer.
	 */
	public static void computeZBuffer(Color[][] zbuffer, float[][] zdepth, EdgeList polyEdgeList, Color polyColor) {
		for (int y = polyEdgeList.getStartY(); y < polyEdgeList.getEndY(); y++) { //iterates through all the y's and will stop once it reaches the end
			float slopeZ =  (polyEdgeList.getRightZ(y) - polyEdgeList.getLeftZ(y)) / (polyEdgeList.getRightX(y) - polyEdgeList.getLeftX(y)); //from the lecture notes
				//calculates the slope
			int x = Math.round(polyEdgeList.getLeftX(y)); 
			float z = polyEdgeList.getLeftZ(y) + (slopeZ*(x - polyEdgeList.getLeftX(y))); 

			while (x <= Math.round(polyEdgeList.getRightX(y)) - 1) {
				if (y >= 0 && x >= 0 && y < GUI.CANVAS_HEIGHT && x < GUI.CANVAS_WIDTH && z < zdepth[x][y]) { //one problem we were having was <= width or height = out of bounds
					zbuffer[x][y] = polyColor; //The color of the polygon to add into the zbuffer
					zdepth[x][y] = z; //stopre the float storing the z value of the each pixel that has been coloured in so far 
				}
				z += slopeZ; //increment the z by the slope
				x++; //increment the x value 
			}
		}

	}
}

// code for comp261 assignments

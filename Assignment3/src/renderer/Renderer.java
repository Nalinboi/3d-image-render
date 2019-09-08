package renderer;

import java.awt.Color;
import java.util.List;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Renderer extends GUI {
	Scene scene;
	public Color polyColor = new Color(255,255,255);
	public static ArrayList<Vector3D> lightSources = new ArrayList<>();
	public static ArrayList<Vector3D> lightColors = new ArrayList<>();
	
	
	@Override
	protected void onLoad(File file) {
		// TODO fill this in.

		/*
		 * This method should parse the given file into a Scene object, which
		 * you store and use to render an image.
		 */
		BufferedReader br = null;
		String line;
		

		float[] point = new float[9]; //verticies, polygons only have 3 verticies, and each verticies have x,y,z (9)
		int[] rgb = new int[3]; //storing rgb
		// System.out.println("grr");

		try {

			br = new BufferedReader(new FileReader(file)); // "nodeID-lat-lon.tab"
			
			List<Scene.Polygon> polygons = new ArrayList<Scene.Polygon>();
			
			int polyNumber = Integer.parseInt(br.readLine());
			System.out.println(polyNumber);
			int count = 0;			
			
			while (((line = br.readLine()) != null) && (count!=polyNumber)) { //each line is a polygon, last line is light source
				String[] tokens = line.split(",");
					
				rgb[0] = Integer.parseInt(tokens[0]);
				rgb[1] = Integer.parseInt(tokens[1]);
				rgb[2] = Integer.parseInt(tokens[2]);
				
				Color c = new Color(rgb[0], rgb[1], rgb[2]);
				
				
				point[0] = Float.parseFloat((tokens[3]));
				point[1] = Float.parseFloat((tokens[4]));
				point[2] = Float.parseFloat((tokens[5]));
				point[3] = Float.parseFloat((tokens[6]));
				point[4] = Float.parseFloat((tokens[7]));
				point[5] = Float.parseFloat((tokens[8]));
				point[6] = Float.parseFloat((tokens[9]));
				point[7] = Float.parseFloat((tokens[10]));
				point[8] = Float.parseFloat((tokens[11]));
				
				Vector3D vertex1 = new Vector3D(point[0], point[1], point[2]);
				Vector3D vertex2 = new Vector3D(point[3], point[4], point[5]);
				Vector3D vertex3 = new Vector3D(point[6], point[7], point[8]);
				
				
				polygons.add(new Scene.Polygon(vertex1, vertex2, vertex3, c));
				count++;							
			}
			String lightsource = line;
			
			String[] vectorSplit = lightsource.split(",");
			
			float x = Float.parseFloat((vectorSplit[0]));
			float y = Float.parseFloat((vectorSplit[1]));
			float z = Float.parseFloat((vectorSplit[2]));
						
			Vector3D myvector = new Vector3D(x,y,z);
			
			scene = new Scene(polygons, myvector);
			
			
						
			//System.out.println(nodeMap);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		scene = Pipeline.scaleScene(scene);
	}

	@Override
	protected void onKeyPress(KeyEvent ev) {
		// TODO fill this in.

		/*
		 * This method should be used to rotate the user's viewpoint.
		 */
		//rotates up down left and right
		if (ev.getKeyCode() == KeyEvent.VK_LEFT
				|| Character.toUpperCase(ev.getKeyChar()) == 'A')
			scene = Pipeline.rotateScene(scene, 0, 0.5f);
		else if (ev.getKeyCode() == KeyEvent.VK_RIGHT
				|| Character.toUpperCase(ev.getKeyChar()) == 'D')
			scene = Pipeline.rotateScene(scene, 0, -0.5f);
		else if (ev.getKeyCode() == KeyEvent.VK_UP
				|| Character.toUpperCase(ev.getKeyChar()) == 'W')
			scene = Pipeline.rotateScene(scene, -0.5f, 0);
		else if (ev.getKeyCode() == KeyEvent.VK_DOWN
				|| Character.toUpperCase(ev.getKeyChar()) == 'S')
			scene = Pipeline.rotateScene(scene, 0.5f, 0);
//		else if (Character.toUpperCase(ev.getKeyChar()) == 'L') //was going to use these for light source
//			scene = Pipeline.rotateScene(scene, -0.5f, 0);
//		else if (Character.toUpperCase(ev.getKeyChar()) == 'K')
//			scene = Pipeline.rotateScene(scene, -0.5f, 0);
	}

	@Override
	protected BufferedImage render() {
		// TODO fill this in.
		/*
		 * This method should put together the pieces of your renderer, as
		 * described in the lecture. This will involve calling each of the
		 * static method stubs in the Pipeline class, which you also need to
		 * fill in.
		 */
		
		
			if (scene == null) {
				return null;
			}
	//
//			scene = Pipeline.translateScene(scene);
//			scene = Pipeline.scaleScene(scene);

			Color[][] zBuffer = new Color[CANVAS_WIDTH][CANVAS_HEIGHT];
			float[][] zDepth = new float[CANVAS_WIDTH][CANVAS_HEIGHT];

			EdgeList edges;

			for (int x = 0; x < CANVAS_WIDTH; x++) {
				for (int y = 0; y < CANVAS_HEIGHT; y++) {
					zBuffer[x][y] = Color.white;
					zDepth[x][y] = Float.POSITIVE_INFINITY;
				}
			}

			for (Scene.Polygon p : scene.getPolygons()) {
				if (!Pipeline.isHidden(p)) {
				Color c = Pipeline.getShading(p, scene.getLight(), polyColor, new Color(getAmbientLight()[0], getAmbientLight()[1], getAmbientLight()[2]));
					edges = Pipeline.computeEdgeList(p);
					Pipeline.computeZBuffer(zBuffer, zDepth, edges, c);
				}
			}
			return convertBitmapToImage(zBuffer);
		}
		//return null;
	
//	public static void addLightSource() {
//		lightSources.add(new Vector3D((float) (Math.random() - Math.random()), (float) (Math.random() - Math.random()), (float) (Math.random() - Math.random())));
//		LightColors.add((int) Math.random()*255), (int) Math.random()*255), (int) Math.random()*255))
//	}
	

	/**
	 * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
	 * indexed by column then row and has imageHeight rows and imageWidth
	 * columns. Note that image.setRGB requires x (col) and y (row) are given in
	 * that order.
	 */
	private BufferedImage convertBitmapToImage(Color[][] bitmap) {
		BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	public static void main(String[] args) {
		new Renderer();
	}
}

// code for comp261 assignments

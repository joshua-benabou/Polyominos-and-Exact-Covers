import java.awt.*;

public class ColoredPolygon {
	Polygon polygon;
	Color color;
	
	public ColoredPolygon(int[] xcoords, int[] ycoords, Color color) {
		int npoints=xcoords.length;
		polygon=new Polygon(xcoords, ycoords, npoints);
		this.color=color;
	}
}

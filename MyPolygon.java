import java.awt.Color;
import java.awt.Polygon;

@SuppressWarnings("serial")
public class MyPolygon extends Polygon
{
	protected Color col;
	public int orig_x;
	public int orig_y;
	public int angle;

	public MyPolygon(int[] xpoints, int[] ypoints, int npoints, int angle) {
		this.angle = angle;
		this.npoints = npoints;
		this.xpoints = new int[npoints];
		this.ypoints = new int[npoints];
		System.arraycopy(xpoints, 0, this.xpoints, 0, npoints);
		System.arraycopy(ypoints, 0, this.ypoints, 0, npoints);
	}

	public final void move(int x, int y) {
		for (int i = 0; i < this.npoints; i++) {
			this.xpoints[i] += (x - this.orig_x);
			this.ypoints[i] += (y - this.orig_y);
		}
		this.orig_x = x;
		this.orig_y = y;
	}

	public final void rotate(int x, int y) {
		double d1 = Math.PI / this.angle;
		for (int i = 0; i < this.npoints; i++) {
			double d2 = (this.xpoints[i] - x) * Math.sin(d1) + (this.ypoints[i] - y) * Math.cos(d1);
			double d3 = (this.xpoints[i] - x) * Math.cos(d1) - (this.ypoints[i] - y) * Math.sin(d1);
			this.ypoints[i] = ((int)Math.round(d2) + y);
			this.xpoints[i] = ((int)Math.round(d3) + x);
		}
	}

	public final void flip(int x, int y) {
		if (contains(x, y)) {
			for (int i = 0; i < this.npoints; i++) {
				this.xpoints[i] = -this.xpoints[i];
			}
		}
	}

	public final void setColor(Color color) {
		this.col = color;
	}

	public final Color getColor() {
		return this.col;
	}
}

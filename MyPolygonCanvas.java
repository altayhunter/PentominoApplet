import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Polygon;

@SuppressWarnings("serial")
public class MyPolygonCanvas extends Panel implements Runnable
{
	Image offscreen;
	Graphics offgraphics;
	MyPolygon elect;
	int indexElect;
	int index;
	int X;
	int Y;
	int angle;
	boolean outside;
	boolean rot = false;
	boolean flipIsAllowed;
	boolean rotateIsAllowed;
	Thread rotation;
	MyPolygon[] polyArray;
	Color bgcolor;

	public MyPolygonCanvas() {
	}

	public MyPolygonCanvas(int numPolygon, Graphics offgraphics,
	                       Image offscreen, Color bgcolor,
						   boolean rotateIsAllowed, boolean flipIsAllowed,
						   int angle) {
		this.polyArray = new MyPolygon[numPolygon];
		this.bgcolor = bgcolor;
		this.offscreen = offscreen;
		this.offgraphics = offgraphics;
		this.flipIsAllowed = flipIsAllowed;
		this.rotateIsAllowed = rotateIsAllowed;
		this.angle = angle;
	}

	public final int addPolygon(int[][] points, int npoints, Color color) {
		int[] x_points = new int[npoints];
		int[] y_points = new int[npoints];
		for (int i = 0; i < npoints; i++) {
			x_points[i] = points[i][0];
			y_points[i] = points[i][1];
		}
		MyPolygon polygon = new MyPolygon(x_points, y_points, npoints, this.angle);
		polygon.setColor(color);
		this.polyArray[this.index] = polygon;
		return this.index++;
	}

	@Override
	public void run() {
		while (this.rotateIsAllowed) {
			this.elect.rotate(this.X, this.Y);
			repaint();
			try {
				Thread.sleep(750L);
			} catch (InterruptedException exception) {
			}
		}
	}

	@Override
	public void update(Graphics graphics) {
		this.offgraphics.setColor(this.bgcolor);
		this.offgraphics.fillRect(0, 0, getSize().width, getSize().height);
		for (int i = 0; i < this.index; i++) {
			if (i != this.indexElect) {
				this.offgraphics.setColor(this.polyArray[i].getColor());
				this.offgraphics.fillPolygon(this.polyArray[i]);
			}
		}
		this.offgraphics.setColor(this.polyArray[this.indexElect].getColor());
		this.offgraphics.fillPolygon(this.polyArray[this.indexElect]);
		graphics.drawImage(this.offscreen, 0, 0, this);
	}

	@Override
	public void paint(Graphics graphics) {
		update(graphics);
	}

	@Override
	public boolean mouseDown(Event event, int x, int y) {
		int i = 0;
		for (int j = 0; j < this.index; j++) {
			MyPolygon polygon = this.polyArray[j];
			if (polygon.contains(x, y)) {
				this.outside = false;
				this.indexElect = j;
				this.elect = polygon;
			} else {
				i++;
			}
			if (i == this.index) {
				this.outside = true;
				return true;
			}
		}
		this.elect.orig_x = x;
		this.elect.orig_y = y;
		if (event.metaDown() && !this.outside) {
			this.X = x;
			this.Y = y;
			this.rot = true;
			this.rotation = new Thread(this);
			this.rotation.start();
			return true;
		}
		if (event.clickCount == 2 && this.flipIsAllowed) {
			this.elect.flip(x, y);
			repaint();
		}
		return true;
	}

	@Override
	public boolean mouseDrag(Event event, int x, int y) {
		if (!this.outside) {
			this.elect.move(x, y);
			MyPolygon polygon = new MyPolygon(this.elect.xpoints, this.elect.ypoints, this.elect.npoints, this.elect.angle);
			polygon.setColor(this.elect.getColor());
			this.polyArray[this.indexElect] = polygon;
			repaint();
		}
		return true;
	}

	@Override
	public boolean mouseUp(Event event, int x, int y) {
		if (this.rot) {
			this.rotation.stop();
		}
		return true;
	}
}

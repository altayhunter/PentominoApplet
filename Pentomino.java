import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

@SuppressWarnings("serial")
public class Pentomino extends MyPolygonCanvas
{
	int small_index;
	int blockCount = 4;
	boolean release = false;
	boolean setup = false;
	boolean flip = false;
	boolean AppletInfo = false;
	boolean begin = false;
	static final int EMPTY = 0;
	static final int FULL = 1;
	MyPolygon[] smallPolyArray;
	Color[] pieceColor;
	Vector<String> info = new Vector<String>(35, 5);
	Button quitButton;
	Button resetButton;
	Button infoButton;
	String codbase;
	int[][] grid = new int[9][18];
	int[][] smallPolygonGrid = new int[12][3];
	int[][] blocks = { { 280, 170 }, { 315, 170 }, { 280, 205 }, { 315, 205 } };
	int[][] home = { { 0, 48 }, { 68, 74 }, { 37, 133 }, { 31, 192 }, { 13, 275 }, { 29, 319 }, { 469, 70 }, { 513, 86 }, { 524, 87 }, { 499, 189 }, { 484, 296 }, { 482, 348 } };
	int[][] gridCheck;
	
	public Pentomino(int numPolygons, Color bgcolor, boolean rotateIsAllowed,
	                 boolean flipIsAllowed, int angle, String datafile) throws IOException {
		this.polyArray = new MyPolygon[numPolygons];
		this.smallPolyArray = new MyPolygon[numPolygons];
		this.bgcolor = bgcolor;
		this.flipIsAllowed = flipIsAllowed;
		this.rotateIsAllowed = rotateIsAllowed;
		this.angle = angle;
		this.codbase = datafile;
		setLayout(null);
		this.quitButton = new Button("quit");
		this.resetButton = new Button("begin");
		this.infoButton = new Button(" info ");
		Font font = new Font("Arial", 1, 13);
		this.quitButton.setFont(font);
		this.resetButton.setFont(font);
		this.infoButton.setFont(font);
		Color color = new Color(131, 153, 177);
		this.quitButton.setBackground(color);
		this.resetButton.setBackground(color);
		this.infoButton.setBackground(color);
		this.quitButton.setBounds(190, 370, 60, 25);
		this.resetButton.setBounds(280, 370, 60, 25);
		this.infoButton.setBounds(370, 370, 60, 25);
		add(this.quitButton);
		add(this.resetButton);
		add(this.infoButton);
		initColorArray();
		addPolygon(getCoord(0), 5, this.pieceColor[0]);
		addPolygon(getCoord(1), 12, this.pieceColor[1]);
		addPolygon(getCoord(2), 9, this.pieceColor[2]);
		addPolygon(getCoord(3), 9, this.pieceColor[3]);
		addPolygon(getCoord(4), 9, this.pieceColor[4]);
		addPolygon(getCoord(5), 11, this.pieceColor[5]);
		addPolygon(getCoord(6), 7, this.pieceColor[6]);
		addPolygon(getCoord(7), 7, this.pieceColor[7]);
		addPolygon(getCoord(8), 7, this.pieceColor[8]);
		addPolygon(getCoord(9), 11, this.pieceColor[9]);
		addPolygon(getCoord(10), 9, this.pieceColor[10]);
		addPolygon(getCoord(11), 9, this.pieceColor[11]);
		initGrid();
		drawSmallPolygons();
		initSmallPolygonGrid(EMPTY);
		readTokens();
	}

	private final void readTokens() throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(this.codbase))) {
			String line;
			while ((line = br.readLine()) != null) {
				this.info.addElement(line);
			}
		}
	}
	
	private final void initGrid() {
		int j = 65;
		for (int k = 0; k < 9; k++) {
			int i = 175;
			for (int m = 0; m < 18; m += 2) {
				this.grid[k][m] = i;
				this.grid[k][(m + 1)] = j;
				i += 35;
			}
			j += 35;
		}
	}
	
	private final void drawGrid() {
		this.offgraphics.setColor(Color.black);
		for (int i = 1; i < 8; i++) {
			this.offgraphics.drawLine(175 + i * 35, 65, 175 + i * 35, 344);
			this.offgraphics.drawLine(175, 65 + i * 35, 454, 65 + i * 35);
		}
		for (int j = this.blockCount - 1; j >= 0; j--) {
			this.offgraphics.fillRect(this.blocks[j][0], this.blocks[j][1], 35, 35);
		}
		this.offgraphics.drawRect(175, 65, 279, 279);
		if (this.blockCount < 4) {
			this.offgraphics.drawString("Click four different squares", 228, 50);
		}
		else if (this.begin) {
			this.offgraphics.drawString("Click on a shape and drag it on the board", 182, 50);
		}
		else if (this.resetButton.getLabel().equals("reset")) {
			this.offgraphics.drawString("Good luck :-)", 275, 50);
		}
		for (int k = 5; k >= 0; k--) {
			this.offgraphics.drawRect(35, 35 + k * 60, 100, 60);
			this.offgraphics.drawRect(495, 35 + k * 60, 100, 60);
		}
	}
	
	private void intersectWarn() {
		int i = 17;
		int j = 17;
		this.gridCheck = new int[8][8];
		int n;
		for (int k = 7; k >= 0; k--) {
			for (int m = 14; m >= 0; m -= 2) {
				i += this.grid[k][m];
				j += this.grid[k][(m + 1)];
				for (n = this.index - 1; n >= 0; n--) {
					if (n < this.blockCount) {
						Rectangle localRectangle = new Rectangle(this.blocks[n][0], this.blocks[n][1], 35, 35);
						if (localRectangle.contains(i, j)) {
							this.gridCheck[k][(m / 2)] += 1;
						}
					}
					if ((this.smallPolygonGrid[n][2] != FULL) && (this.polyArray[n].contains(i, j))) {
						this.gridCheck[k][(m / 2)] += 1;
					}
				}
				i = 17;
				j = 17;
			}
		}
		this.offgraphics.setColor(Color.red);
		for (int m = 7; m >= 0; m--) {
			for (n = 14; n >= 0; n -= 2) {
				if (this.gridCheck[m][(n / 2)] > 1) {
					this.offgraphics.drawRect(this.grid[m][n], this.grid[m][(n + 1)], 35, 35);
				}
			}
		}
		repaint();
	}
	
	private void snapToGrid() {
		int i = this.polyArray[this.indexElect].xpoints[0];
		int j = this.polyArray[this.indexElect].ypoints[0];
		for (int k = 8; k >= 0; k--) {
			for (int m = 16; m >= 0; m -= 2) {
				int n = this.grid[k][m];
				int o = this.grid[k][(m + 1)];
				if ((i + 35 == Math.max(n, i + 35)) &&
						(j + 35 == Math.max(o, j + 35)) &&
						(i - 35 == Math.min(n, i - 35)) &&
						(j - 35 == Math.min(o, j - 35))) {
					double d1 = n - i;
					double d2 = o - j;
					if (d1 < 0.0D) {
						d1 *= -1.0D;
					}
					if (d2 < 0.0D) {
						d2 *= -1.0D;
					}
					if ((d1 < 17.5D) && (d2 < 17.5D)) {
						this.polyArray[this.indexElect].orig_x = i;
						this.polyArray[this.indexElect].orig_y = j;
						this.polyArray[this.indexElect].move(n, o);
						return;
					}
				}
			}
		}
	}
	
	@Override
	public void update(Graphics graphics) {
		if (this.offscreen == null) {
			this.offscreen = createImage(650, 500);
			this.offgraphics = offscreen.getGraphics();
		}
		this.offgraphics.setFont(new Font("Arial", 0, 14));
		this.offgraphics.setColor(Color.white);
		this.offgraphics.fillRect(0, 0, 700, 500);
		if (!this.AppletInfo) {
			drawGrid();
			for (int i = 11; i >= 0; i--) {
				this.offgraphics.setColor(this.smallPolyArray[i].getColor());
				this.offgraphics.fillPolygon(this.smallPolyArray[i]);
			}
			for (int j = 0; j < 12; j++) {
				if ((j != this.indexElect) && (this.smallPolygonGrid[j][2] == EMPTY)) {
					this.offgraphics.setColor(this.polyArray[j].getColor());
					this.offgraphics.fillPolygon(this.polyArray[j]);
				}
			}
			if (this.smallPolygonGrid[this.indexElect][2] == EMPTY) {
				this.offgraphics.setColor(this.polyArray[this.indexElect].getColor());
				this.offgraphics.fillPolygon(this.polyArray[this.indexElect]);
			}
			if (this.release) {
				intersectWarn();
			}
		} else {
			AppletInfo();
		}
		graphics.drawImage(this.offscreen, 0, 0, this);
	}
	
	@Override
	public boolean mouseDown(Event event, int x, int y) {
		super.mouseDown(event, x, y);
		this.release = false;
		// TODO: Reject duplicate blocks
		if (this.setup && this.blockCount < 4 && !checkOffboard(x, y)) {
			int i = (x - 175) / 35;
			int j = (y - 65) / 35;
			this.blocks[this.blockCount][0] = this.grid[j][(2 * i)];
			this.blocks[this.blockCount][1] = this.grid[j][(2 * i + 1)];
			this.blockCount += 1;
			if (this.blockCount == 4) {
				this.setup = false;
				this.begin = true;
			}
			repaint();
			return true;
		} else if (checkOffboard(x, y) && this.blockCount > 3) {
			this.begin = false;
			for (int i = 0; i < 12; i++) {
				if ((this.smallPolygonGrid[i][2] != EMPTY) && (insideSmallPolygonGrid(x, y, i))) {
					this.indexElect = i;
					this.elect = this.polyArray[i];
					this.elect.orig_x = x;
					this.elect.orig_y = y;
					this.smallPolygonGrid[i][2] = EMPTY;
					this.smallPolyArray[i].setColor(Color.lightGray);
					repaint();
					return true;
				}
			}
			if (event.clickCount == 2) {
				this.flip = true;
			}
		}
		return true;
	}
	
	private final boolean checkOffboard(int x, int y) {
		return (x < 175) || (x > 455) || (y < 65) || (y > 345);
	}
	
	private final boolean insideSmallPolygonGrid(int x, int y, int shape) {
		int i = this.smallPolygonGrid[shape][0];
		int j = this.smallPolygonGrid[shape][1];
		return (x == Math.max(x, i)) && (y == Math.max(y, j)) && (x < i + 100) && (y < j + 60);
	}
	
	@Override
	public boolean mouseUp(Event event, int x, int y) {
		if (!this.outside || insideSmallPolygonGrid(x, y, this.indexElect)) {
			if (this.rot) {
				this.rotation.stop();
				this.rot = false;
			} else if (checkOffboard(x, y)) {
				this.smallPolygonGrid[this.indexElect][2] = FULL;
				this.smallPolyArray[this.indexElect].setColor(this.pieceColor[this.indexElect]);
				reInitPolyArray(this.indexElect);
				repaint();
			} else if (this.flip) {
				this.flip = false;
			} else {
				this.release = true;
				snapToGrid();
				repaint();
			}
		}
		return true;
	}
	
	@Override
	public boolean action(Event event, Object what) {
		if (event.target == this.resetButton) {
			this.setup = true;
			this.blockCount = 0;
			resetSmallPolygonColors();
			this.resetButton.setLabel("reset");
			for (int i = 11; i >= 0; i--) {
				this.smallPolygonGrid[i][2] = FULL;
				reInitPolyArray(i);
			}
			repaint();
		} else if (event.target == this.quitButton) {
			Frame parent = (Frame)getParent();
			parent.dispose();
		} else if (" info ".equals(what)) {
			this.AppletInfo = true;
			this.infoButton.setLabel("close");
			repaint();
		} else if ("close".equals(what)) {
			this.AppletInfo = false;
			this.infoButton.setLabel(" info ");
			repaint();
		}
		return true;
	}
	
	private int[][] getCoord(int shape) {
		switch (shape) {
			case 0: 
				return new int[][] { { 175, 65 }, { 350, 65 }, { 350, 100 }, { 175, 100 }, { 175, 65 } };
			case 1: 
				return new int[][] { { 350, 65 }, { 385, 65 }, { 385, 100 }, { 420, 100 }, { 420, 135 }, { 385, 135 }, { 385, 170 }, { 350, 170 }, { 350, 135 }, { 315, 135 }, { 315, 100 }, { 350, 100 }, { 350, 65 } };
			case 2: 
				return new int[][] { { 385, 65 }, { 455, 65 }, { 455, 170 }, { 385, 170 }, { 385, 135 }, { 420, 135 }, { 420, 100 }, { 385, 100 }, { 385, 65 } };
			case 3: 
				return new int[][] { { 175, 100 }, { 210, 100 }, { 210, 135 }, { 280, 135 }, { 280, 205 }, { 245, 205 }, { 245, 170 }, { 175, 170 }, { 175, 100 } };
			case 4: 
				return new int[][] { { 210, 100 }, { 315, 100 }, { 315, 135 }, { 350, 135 }, { 350, 170 }, { 280, 170 }, { 280, 135 }, { 210, 135 }, { 210, 100 } };
			case 5: 
				return new int[][] { { 175, 170 }, { 245, 170 }, { 245, 205 }, { 280, 205 }, { 280, 275 }, { 245, 275 }, { 245, 240 }, { 210, 240 }, { 210, 205 }, { 175, 205 }, { 175, 170 } };
			case 6: 
				return new int[][] { { 280, 240 }, { 350, 240 }, { 350, 170 }, { 385, 170 }, { 385, 275 }, { 280, 275 }, { 280, 240 } };
			case 7: 
				return new int[][] { { 385, 170 }, { 455, 170 }, { 455, 240 }, { 420, 240 }, { 420, 275 }, { 385, 275 }, { 385, 170 } };
			case 8: 
				return new int[][] { { 175, 205 }, { 210, 205 }, { 210, 310 }, { 245, 310 }, { 245, 345 }, { 175, 345 }, { 175, 205 } };
			case 9: 
				return new int[][] { { 210, 240 }, { 245, 240 }, { 245, 275 }, { 315, 275 }, { 315, 310 }, { 280, 310 }, { 280, 345 }, { 245, 345 }, { 245, 310 }, { 210, 310 }, { 210, 240 } };
			case 10: 
				return new int[][] { { 280, 310 }, { 315, 310 }, { 315, 275 }, { 350, 275 }, { 350, 310 }, { 420, 310 }, { 420, 345 }, { 280, 345 }, { 280, 310 } };
			case 11: 
				return new int[][] { { 350, 275 }, { 420, 275 }, { 420, 240 }, { 455, 240 }, { 455, 345 }, { 420, 345 }, { 420, 310 }, { 350, 310 }, { 350, 275 } };
		}
		return null;
	}
	
	private void reInitPolyArray(int shape) {
		int npoints = this.polyArray[shape].npoints;
		int[] x_points = new int[npoints];
		int[] y_points = new int[npoints];
		int[][] coordinates = getCoord(shape);
		for (int i = npoints - 1; i >= 0; i--) {
			x_points[i] = coordinates[i][0];
			y_points[i] = coordinates[i][1];
		}
		MyPolygon polygon = new MyPolygon(x_points, y_points, npoints, 2);
		polygon.setColor(this.pieceColor[shape]);
		this.polyArray[shape] = polygon;
		this.polyArray[shape].orig_x = polygon.xpoints[0];
		this.polyArray[shape].orig_y = polygon.ypoints[0];
		this.polyArray[shape].move(this.home[shape][0], this.home[shape][1]);
	}
	
	private final void initSmallPolygonGrid(int state) {
		for (int i = 11; i >= 0; i--) {
			if (i < 6) {
				this.smallPolygonGrid[i][0] = 35;
				this.smallPolygonGrid[i][1] = (35 + i * 60);
			} else {
				this.smallPolygonGrid[i][0] = 495;
				this.smallPolygonGrid[i][1] = (35 + (i - 6) * 60);
			}
			this.smallPolygonGrid[i][2] = state;
		}
	}
	
	private void addSmallPolygon(int[] xpoints, int[] ypoints, int npoints) {
		MyPolygon polygon = new MyPolygon(xpoints, ypoints, npoints, 0);
		polygon.setColor(Color.lightGray);
		this.smallPolyArray[this.small_index] = polygon;
		this.small_index++;
	}
	
	private void drawSmallPolygons() {
		int[] i_xpoints = { 55, 115, 115, 55, 55 };
		int[] i_ypoints = { 59, 59, 71, 71, 59 };
		int[] x_xpoints = { 79, 91, 91, 103, 103, 91, 91, 79, 79, 67, 67, 79, 79 };
		int[] x_ypoints = { 107, 107, 119, 119, 131, 131, 143, 143, 131, 131, 119, 119, 107 };
		int[] u_xpoints = { 73, 73, 97, 97, 73, 73, 85, 85, 73 };
		int[] u_ypoints = { 179, 167, 167, 203, 203, 191, 191, 179, 179 };
		int[] z_xpoints = { 67, 79, 79, 103, 103, 91, 91, 67, 67 };
		int[] z_ypoints = { 227, 227, 239, 239, 263, 263, 251, 251, 227 };
		int[] n_xpoints = { 61, 97, 97, 109, 109, 85, 85, 61, 61 };
		int[] n_ypoints = { 293, 293, 305, 305, 317, 317, 305, 305, 293 };
		int[] w_xpoints = { 67, 91, 91, 103, 103, 91, 91, 79, 79, 67, 67 };
		int[] w_ypoints = { 347, 347, 359, 359, 383, 383, 371, 371, 359, 359, 347 };
		int[] v_xpoints = { 551, 563, 563, 527, 527, 551, 551 };
		int[] v_ypoints = { 47, 47, 83, 83, 71, 71, 47 };
		int[] p_xpoints = { 532, 557, 557, 545, 545, 533, 533 };
		int[] p_ypoints = { 107, 107, 131, 131, 143, 143, 107 };
		int[] l_xpoints = { 533, 545, 545, 557, 557, 533, 533 };
		int[] l_ypoints = { 161, 161, 197, 197, 209, 209, 161 };
		int[] f_xpoints = { 527, 539, 539, 563, 563, 551, 551, 539, 539, 527, 527 };
		int[] f_ypoints = { 227, 227, 239, 239, 251, 251, 263, 263, 251, 251, 227 };
		int[] y_xpoints = { 533, 545, 545, 569, 569, 521, 521, 533, 533 };
		int[] y_ypoints = { 293, 293, 305, 305, 317, 317, 305, 305, 293 };
		int[] t_xpoints = { 551, 563, 563, 551, 551, 527, 527, 551, 551 };
		int[] t_ypoints = { 347, 347, 383, 383, 371, 371, 359, 359, 347 };
		addSmallPolygon(i_xpoints, i_ypoints, 5);
		addSmallPolygon(x_xpoints, x_ypoints, 13);
		addSmallPolygon(u_xpoints, u_ypoints, 9);
		addSmallPolygon(z_xpoints, z_ypoints, 9);
		addSmallPolygon(n_xpoints, n_ypoints, 9);
		addSmallPolygon(w_xpoints, w_ypoints, 11);
		addSmallPolygon(v_xpoints, v_ypoints, 7);
		addSmallPolygon(p_xpoints, p_ypoints, 7);
		addSmallPolygon(l_xpoints, l_ypoints, 7);
		addSmallPolygon(f_xpoints, f_ypoints, 11);
		addSmallPolygon(y_xpoints, y_ypoints, 9);
		addSmallPolygon(t_xpoints, t_ypoints, 9);
	}
	
	private final void resetSmallPolygonColors() {
		for (int i = 11; i >= 0; i--) {
			this.smallPolyArray[i].setColor(this.pieceColor[i]);
		}
	}
	
	private void initColorArray() {
		this.pieceColor = new Color[12];
		this.pieceColor[0] = new Color(255, 0, 0);
		this.pieceColor[1] = new Color(150, 150, 255);
		this.pieceColor[2] = new Color(0, 200, 200);
		this.pieceColor[3] = new Color(255, 150, 255);
		this.pieceColor[4] = new Color(0, 200, 0);
		this.pieceColor[5] = new Color(150, 255, 255);
		this.pieceColor[6] = new Color(200, 200, 0);
		this.pieceColor[7] = new Color(0, 0, 200);
		this.pieceColor[8] = new Color(255, 150, 150);
		this.pieceColor[9] = new Color(200, 0, 200);
		this.pieceColor[10] = new Color(255, 255, 150);
		this.pieceColor[11] = new Color(150, 255, 150);
	}
	
	private void AppletInfo() {
		this.offgraphics.setColor(Color.lightGray);
		this.offgraphics.fillRect(30, 35, 10, 360);
		this.offgraphics.fillRect(590, 35, 10, 360);
		this.offgraphics.setColor(Color.black);
		this.offgraphics.drawLine(45, 36, 585, 36);
		this.offgraphics.drawLine(45, 69, 585, 69);
		this.offgraphics.drawLine(45, 394, 180, 394);
		this.offgraphics.drawLine(440, 394, 585, 394);
		int y = 57;
		for (int i = 0; i < this.info.size(); i++) {
			this.offgraphics.drawString(this.info.elementAt(i), 38, y);
			y += 20;
		}
	}
}

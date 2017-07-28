import java.awt.Color;
import java.io.IOException;

public class Main
{
	public static void main(String argv[]) throws IOException {
		Pentomino p = new Pentomino(12, Color.white, true, true, 2, "pentoInfo.dat");
		p.setSize(650, 450);
		PentominoFrame pf = new PentominoFrame("Pentomino");
		pf.setResizable(false);
		pf.add(p);
		pf.setSize(630, 440);
	}
}

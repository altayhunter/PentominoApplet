import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;

@SuppressWarnings("serial")
class PentominoFrame extends Frame
{
	public PentominoFrame(String title) {
		super(title);
		setBackground(Color.white);
		setVisible(true);
	}
	
	@Override
	public void setVisible(boolean b) {
		if (b) {
			pack();
			Dimension screen = getToolkit().getScreenSize();
			setLocation((screen.width - getSize().width) / 2, (screen.height - getSize().height) / 2);
			requestFocus();
		}
		super.setVisible(b);
	}
	
	@Override
	public boolean handleEvent(Event event) {
		if (event.id == Event.WINDOW_DESTROY) {
			dispose();
			return true;
		}
		return super.handleEvent(event);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	@Override
	public synchronized Dimension getMinimumSize() {
		return new Dimension(630, 440);
	}
}

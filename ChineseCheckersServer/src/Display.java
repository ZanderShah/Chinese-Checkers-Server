import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Display extends JPanel {

	public Display() {
		super(new GridLayout(2, 0));
		setPreferredSize(new Dimension(100, 500));

		add(new JLabel(""));

		this.setVisible(true);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.add(new Display());
		frame.setVisible(true);
	}
}

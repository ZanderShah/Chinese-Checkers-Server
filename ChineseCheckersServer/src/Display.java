import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class Display extends JPanel {
	static int[][] board;
	private final Dimension SIZE = new Dimension(1024, 768);
	private final int SPACE = 50;
	private final int DIAMETER = 40;
	private final int BORDER = 2;
	private final int WIDTH = SPACE * 11 + DIAMETER;
	private final int HEIGHT = (int) (16 * SPACE * Math.sqrt(3) / 2 + DIAMETER);

	/**
	 * Sets up the board and GUI
	 */
	public Display() {
		board = new int[17][17];
		for (int i = 0; i < 17; i++)
			for (int j = 0; j < 17; j++)
				board[i][j] = -1;

		for (int i = 4; i <= 12; i++)
			for (int j = 4; j <= 12; j++)
				board[i][j] = 0;

		fillTriangle(-1, board, 1, 16, 12);
		fillTriangle(1, board, 2, 9, 13);
		fillTriangle(-1, board, 3, 7, 12);
		fillTriangle(1, board, 4, 0, 4);
		fillTriangle(-1, board, 5, 7, 3);
		fillTriangle(1, board, 6, 9, 4);

		this.setPreferredSize(SIZE);
	}

	public void updateBoard(int[][] board) {
		board = board;
	}

	public int[][] getBoard() {
		return board;
	}

	/**
	 * Paints the pieces on the board
	 */
	public void paintComponent(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.clearRect(0, 0, (int) SIZE.getWidth(), (int) SIZE.getHeight());
		// Initialize left shift
		// double left = 0;
		for (int i = 0; i < 17; i++)
			// , left += SPACE / Math.sqrt(3) - 2)
			for (int j = 0; j < 17; j++) {
				if (board[i][j] != -1) {
					int x = (int) (getWidth() / 2 - WIDTH / 2 + 4 * SPACE / 2 - DIAMETER / 2) + (j * SPACE - i * SPACE / 2);
					int y = (int) ((getHeight() / 2 - HEIGHT / 2) + (i * SPACE * Math.sqrt(3) / 2));
					g.setColor(Color.BLACK);
					g.fillOval(x - BORDER, y - BORDER, DIAMETER + 2 * BORDER, DIAMETER + 2 * BORDER);
					// 0 = empty position
					if (board[i][j] == 0)
						g.setColor(Color.LIGHT_GRAY);
					// 1-6 player positions
					else if (board[i][j] == 1)
						g.setColor(Color.RED);
					else if (board[i][j] == 2)
						g.setColor(Color.ORANGE);
					else if (board[i][j] == 3)
						g.setColor(Color.YELLOW);
					else if (board[i][j] == 4)
						g.setColor(Color.GREEN);
					else if (board[i][j] == 5)
						g.setColor(Color.BLUE);
					else if (board[i][j] == 6)
						g.setColor(Color.MAGENTA.darker());
					g.fillOval(x, y, DIAMETER, DIAMETER);
				}
			}
	}

	/**
	 * Fills the board array with integers that correspond to each player
	 * 
	 * @param attitude whether or not the triangle is upright or inverted
	 * @param board the board
	 * @param player the number of the player from 1 to 6
	 * @param row the row of the tip of the triangle
	 * @param col the col of the tip of the triangle
	 */
	public static void fillTriangle(int attitude, int[][] board, int player, int row, int col) {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j <= i; j++)
				board[attitude > 0 ? row + i : row - i][attitude > 0 ? col + j : col - j] = player;
	}
}

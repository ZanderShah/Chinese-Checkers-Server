import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class Display extends JPanel
{
	static int[][] board;
	private final Dimension SIZE = new Dimension(900, 800);
	private final int SPACE = 32;
	private final int DIAMETER = 30;

	/**
	 * Sets up the board and GUI
	 */
	public Display()
	{
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

	public void updateBoard(int[][] board)
	{
		this.board = board;
	}

	public int[][] getBoard()
	{
		return board;
	}

	/**
	 * Paints the pieces on the board
	 */
	public void paintComponent(Graphics g)
	{
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.clearRect(0, 0, 900, 800);
		// Initialize left shift
//		double left = 0;
		for (int i = 0; i < 17; i++)//, left += SPACE / Math.sqrt(3) - 2)
			for (int j = 0; j < 17; j++)
			{
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
					g.setColor(Color.MAGENTA);
				// -1 = invalid position
				else
					g.setColor(Color.WHITE);
				g.fillOval((int) ((j * SPACE) - (SPACE / 2)) /*- left + SIZE.width / 2 - 4.5 * SPACE)*/,
						(int) (i * SPACE * Math.sqrt(3) / 2), DIAMETER, DIAMETER);
			}
	}

	/**
	 * Fills the board array with integers that correspond to each player
	 * @param attitude whether or not the triangle is upright or inverted
	 * @param board the board
	 * @param player the number of the player from 1 to 6
	 * @param row the row of the tip of the triangle
	 * @param col the col of the tip of the triangle
	 */
	public static void fillTriangle(int attitude, int[][] board, int player,
			int row, int col)
	{
		for (int i = 0; i < 4; i++)
			for (int j = 0; j <= i; j++)
				board[attitude > 0 ? row + i : row - i][attitude > 0 ? col + j
						: col - j] = player;
	}
}

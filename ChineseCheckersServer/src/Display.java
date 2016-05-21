import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Display extends JPanel {
	private int[][] board;
	private int turn;
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
		
		fillTriangle(-1, board, 0, 16, 12);
		fillTriangle(1, board, 0, 9, 13);
		fillTriangle(-1, board, 0, 7, 12);
		fillTriangle(1, board, 0, 0, 4);
		fillTriangle(-1, board, 0, 7, 3);
		fillTriangle(1, board, 0, 9, 4);
		
		this.setPreferredSize(SIZE);
	}
	
	public int[] setUpBoard(int noOfPlayers)
	{
		switch(noOfPlayers)
		{
		case 1:
			fillTriangle(-1, board, 1, 16, 12);
			return new int[] {1};
		case 2:
			fillTriangle(-1, board, 1, 16, 12);
			fillTriangle(1, board, 4, 0, 4);
			return new int[] {1,4};
		case 3:
			fillTriangle(1, board, 2, 9, 13);
			fillTriangle(1, board, 4, 0, 4);
			fillTriangle(1, board, 6, 9, 4);
			return new int[] {2,4,6};
		case 4:
			fillTriangle(1, board, 2, 9, 13);
			fillTriangle(-1, board, 3, 7, 12);
			fillTriangle(-1, board, 5, 7, 3);
			fillTriangle(1, board, 6, 9, 4);
			return new int[] {2,3,5,6};
		case 5:
			fillTriangle(-1, board, 1, 16, 12);
			fillTriangle(1, board, 2, 9, 13);
			fillTriangle(-1, board, 3, 7, 12);
			fillTriangle(1, board, 4, 0, 4);
			fillTriangle(-1, board, 5, 7, 3);
			return new int[] {1,2,3,4,5};
		default:
			fillTriangle(-1, board, 1, 16, 12);
			fillTriangle(1, board, 2, 9, 13);
			fillTriangle(-1, board, 3, 7, 12);
			fillTriangle(1, board, 4, 0, 4);
			fillTriangle(-1, board, 5, 7, 3);
			fillTriangle(1, board, 6, 9, 4);
			return new int[] {1,2,3,4,5,6};
		}
	}

	public void update(int[][] board, int t) {
		this.board = board;
		turn = t;
	}

	public int[][] getBoard() {
		return board;
	}
	
	public int getNoOfPlayers()
	{
		String noOfPlayers = JOptionPane.showInputDialog(null, "Please enter the number of players from 1-6", "Number of Players", JOptionPane.INFORMATION_MESSAGE);
		while(!isNumber(noOfPlayers) || Integer.parseInt(noOfPlayers) < 1 || Integer.parseInt(noOfPlayers) > 6)
		{
			if(!isNumber(noOfPlayers))
				noOfPlayers = JOptionPane.showInputDialog(null, "You must enter a NUMBER. Please re-enter the number of players from 1-6", "NUMBER of Players", JOptionPane.INFORMATION_MESSAGE);
			else
				noOfPlayers = JOptionPane.showInputDialog(null, "Please re-enter the number of players from 1-6", "Players must be between 1 and 6", JOptionPane.INFORMATION_MESSAGE);
		}
		
		return Integer.parseInt(noOfPlayers);
	}
	
	public boolean isNumber(String str)
	{
		for(int i = 0; i < str.length(); i++)
		{
			if(!Character.isDigit(str.charAt(i)))
				return false;
		}
		return true;
	}
	
	public int getTimeOut()
	{
		String time = JOptionPane.showInputDialog(null, "Please enter the timeout amount in seconds: ", "Set Timeout", JOptionPane.INFORMATION_MESSAGE);
		while(!isNumber(time))
			time = JOptionPane.showInputDialog(null, "You must enter a NUMBER. Please re-enter the timeout time in seconds: ", "Set Timeout", JOptionPane.INFORMATION_MESSAGE);
		
		return Integer.parseInt(time);
	}

	/**
	 * Paints the pieces on the board
	 */
	public void paintComponent(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.clearRect(0, 0, getWidth(), getHeight());
		for (int i = 0; i < 17; i++) {
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
		
		g.setColor(Color.BLACK);
		g.setFont(g.getFont().deriveFont(Font.PLAIN, 32));
		g.drawString("Current player: " + (turn), 5, 36);
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Display extends JPanel{
	static JFrame frame;
	static int[][] board;
	static final Dimension SIZE = new Dimension(900, 800);
	static final int SPACE = 32;
	static final int DIAMETER = 30;
//
//	public Display() {
//		super(new GridLayout(2, 0));
//		setPreferredSize(new Dimension(600, 600));
//
//		add(new JLabel(""));
//
//		this.setVisible(true);
//	}
	public void go()
	{
		//this.setBackground(Color.WHITE);
	}

	public void paintComponent(Graphics g)
	{
		
		//Initialize left shift
		double left = 0;
		for(int i = 0; i < 17; i++)
		{
			for(int j = 0; j < 17; j++)
			{
				// 0 = empty position
				if(board[i][j] == 0)
					g.setColor(Color.LIGHT_GRAY);
				// 1-6 player positions
				else if(board[i][j] == 1)
					g.setColor(Color.RED);
				else if(board[i][j] == 2)
					g.setColor(Color.ORANGE);
				else if(board[i][j] == 3)
					g.setColor(Color.YELLOW);
				else if(board[i][j] == 4)
					g.setColor(Color.GREEN);
				else if(board[i][j] == 5)
					g.setColor(Color.BLUE);
				else if(board[i][j] == 6)
					g.setColor(Color.MAGENTA);
				// -1 = invalid position
				else
					g.setColor(Color.WHITE);
				g.fillOval((int)((j*SPACE)-left+SIZE.width/2 - 4.5*SPACE), i*SPACE + SIZE.height/10, DIAMETER, DIAMETER);
			}
			left += SPACE/Math.sqrt(3)-2;
		}
	}
	
	public static void fillTriangle(int attitude, int[][] board, int player, int row, int col){
		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j <= i; j++)
				board[attitude > 0? row + i:row-i][attitude >0? col + j: col-j] = player;
		}
	}
	
	public static void main(String[] args) {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(SIZE);
		Display d = new Display();
		d.go();
		
		
		board = new int[17][17];
		for(int i = 0; i < 17; i++)
		{
			for(int j = 0; j < 17; j++)
			{
				board[i][j] = -1;
			}
		}
		
		
		for(int i = 4; i <= 12; i++)
		{
			for(int j = 4; j <= 12; j++)
				board[i][j] = 0;
		}
		
		fillTriangle(-1, board, 1, 16, 12);
		fillTriangle(1, board, 2, 9, 13);
		fillTriangle(-1, board, 3, 7, 12);
		fillTriangle(1, board, 4, 0, 4);
		fillTriangle(-1, board, 5, 7, 3);
		fillTriangle(1, board, 6, 9, 4);
		
		
		frame.getContentPane().add(d);
		frame.setBackground(Color.WHITE);
		frame.pack();
		frame.setVisible(true);
		
		
		
		
	}
}

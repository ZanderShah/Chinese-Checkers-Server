import java.awt.Color;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Server extends JFrame
{
	// Initialize variables
	private ServerSocket serverSocket;
	private ArrayList<Thread> threads;
	private ArrayList<Client> clients;
	private static boolean gameOver, gameStarted;
	private int turn;
	private static int[][] board;
	private static Display display;

	public static void main(String[] args)
	{
		// Create the board
		display = new Display();

		// Create the server
		new Server().go();
	}
	
	public Server() {
		setContentPane(display);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.WHITE);
		pack();
		setVisible(true);
	}

	/**
	 * 
	 */
	public void go()
	{
		System.out.println("Waiting for player connections..");
		Socket client = null;
		int playersConnected = 0;
		turn = 1;
		threads = new ArrayList<Thread>();
		clients = new ArrayList<Client>();
		gameOver = false;
		board = display.getBoard();

		// Connect players
		try
		{
			serverSocket = new ServerSocket(420);

			while (playersConnected != 6)// ///////////////////////////////////////////////////////1
			{
				client = serverSocket.accept();
				playersConnected++;
				System.out.printf("Client #%d connected!%n", playersConnected);
				clients.add(new Client(client, playersConnected));
				threads.add(new Thread(new PlayerThread(
						clients.get(clients.size() - 1))));
				threads.get(threads.size() - 1).start();
			}
		}
		catch (Exception e)
		{
			System.out.println("Connection error!");
			e.printStackTrace();
		}

		System.out.println("All clients connected :)");

		// Give each player their colour and tell them new game (2 1-6)
		for (int i = 0; i < playersConnected; i++)
			clients.get(i).newGame(i + 1);

		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[0].length; j++)
				// If the location has a player piece
				if (board[i][j] != 0 && board[i][j] != -1)
					//Tells everyone to place pieces
					//3 (colour) (row) (col)
					shout("3 " + board[i][j] + " " + i + " " + j);

		gameStarted = true;
	}

	public void checkForWin()
	{
		System.out.println("Checking for wins...");
		// Check to see if a cycle has been made and make sure that it's not
		// the very first turn
		if (turn > 6)
		{
			//Check to see if a cycle has been made and make sure that it's not the very first turn
			if((turn-1) %6+1 == 1 && turn != 1)
			{
				//Check to see if any players have won.
				boolean[] wins = new boolean[7];
				wins[1] = checkTriangle(-1, 4, board, 16, 12);
				wins[2] = checkTriangle(1, 5, board, 9, 13);
				wins[3] = checkTriangle(-1, 6, board, 7, 12);
				wins[4] = checkTriangle(1, 1, board, 0, 4);
				wins[5] = checkTriangle(-1, 2, board, 7, 3);
				wins[6] = checkTriangle(1, 3, board, 9, 4);
				
				for(int player = 1; player <= 6; player++)
					if(wins[player])
					{
						gameOver = true;
						gameStarted = false;
						System.out.printf("Player %d has won!", (player+2)%6+1);
						
						shout("7 " + (player+2)%6+1);
					}
			}
		}
		
		System.out.println("Done checking for wins");
	}

	public static boolean checkTriangle(int attitude, int player, int[][] board,
			int row, int col)
	{
		boolean win = true;
		boolean hasWin = false;
		for (int i = 0; i < 4; i++)
			for (int j = 0; j <= i; j++)
			{
				// If the place is empty
				if (board[attitude > 0 ? row + i : row - i][attitude > 0 ? col
						+ j
						: col - j] == 0)
					win = false;
				if (board[attitude > 0 ? row + i : row - i][attitude > 0 ? col
						+ j
						: col - j] == player)
					hasWin = false;
			}
		return win && hasWin;
	}
	
	public void shout(String command)
	{
		for (Client c : clients)
		{
			c.sendCommand(command);
		}
	}

	static boolean valid(int row, int col, int goalRow, int goalCol)
	{
		if (Math.abs(row - goalRow) <= 1 && Math.abs(col - goalCol) <= 1
				&& row - goalRow != -(col - goalCol))
			return true;

		return hop(row, col, goalRow, goalCol, new boolean[17][17]);
	}

	static boolean hop(int row, int col, int goalRow, int goalCol,
			boolean[][] vis)
	{
		if (row == goalRow && col == goalCol)
			return true;

		vis[row][col] = true;

		boolean ret = false;

		if (inBounds(row + 2, col) && board[row + 1][col] > 0
				&& board[row + 2][col] == 0
				&& !vis[row + 2][col]
				&& hop(row + 2, col, goalRow, goalCol, vis))
			ret = true;

		if (inBounds(row + 2, col + 2) && board[row + 1][col + 1] > 0
				&& board[row + 2][col + 2] == 0 && !vis[row + 2][col + 2]
				&& hop(row + 2, col + 2, goalRow, goalCol, vis))
			ret = true;

		if (inBounds(row - 2, col - 2) && board[row - 1][col - 1] > 0
				&& board[row - 2][col - 2] == 0 && !vis[row - 2][col - 2]
				&& hop(row - 2, col - 2, goalRow, goalCol, vis))
			ret = true;

		if (inBounds(row - 2, col) && board[row - 1][col] > 0
				&& board[row - 2][col] == 0 && !vis[row - 2][col]
				&& hop(row - 2, col, goalRow, goalCol, vis))
			ret = true;

		if (inBounds(row, col + 2) && board[row][col + 1] > 0
				&& board[row][col + 2] == 0 && !vis[row][col + 2]
				&& hop(row, col + 2, goalRow, goalCol, vis))
			ret = true;

		if (inBounds(row, col - 2) && board[row][col - 1] > 0
				&& board[row][col - 2] == 0 && !vis[row][col - 2]
				&& hop(row, col - 2, goalRow, goalCol, vis))
			ret = true;

		return ret;
	}

	static boolean inBounds(int row, int col)
	{
		return row >= 0 && col >= 0 && row < 17 && col < 17;
	}

	class PlayerThread implements Runnable
	{
		private Client client;
		private int colour;

		PlayerThread(Client client)
		{
			this.client = client;
			colour = client.getColour();
		}

		public void run()
		{
			while (!gameOver)
			{
				try
				{
					// If it is the player's turn and the has has started
					if ((turn - 1) % 6 + 1 == colour && gameStarted)// //////////////////////////////
					{
						// Get the move from the client
						int[][] move = client.getMove();

						// If the player didn't time out
						if (move != null)
						{
							// Tell the client of an invalid move
							if (!inBounds(move[0][0], move[0][1])
									|| !inBounds(move[1][0], move[1][1])
									|| board[move[0][0]][move[0][1]] != colour
									|| board[move[1][0]][move[1][1]] != 0
									|| !valid(move[0][0], move[0][1],
											move[1][0],
											move[1][1]))
							{
								client.invalidMove();
								System.out.printf("Move from player %d was invalid!%n", colour);
							}
							else {
								shout("1 " + move[0][0] + " " + move[0][1] + " " + move[1][0] + " " + move[1][1]);
								board[move[0][0]][move[0][1]] = 0;
								board[move[1][0]][move[1][1]] = colour;
							}
						}

						checkForWin();
						turn = (turn + 1) % 6;
						
						display.update(board, turn);
						display.repaint();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}

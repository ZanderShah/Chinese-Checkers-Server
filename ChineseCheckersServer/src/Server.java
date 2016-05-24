import java.awt.Color;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Server extends JFrame
{
	private static final boolean SINGLE_TEST = false;

	// Initialize variables
	private ServerSocket serverSocket;
	private ArrayList<Thread> threads;
	private ArrayList<Client> clients;
	private static boolean gameOver, gameStarted;
	private int turn;
	private static int noOfPlayers, timeOut;
	private static int[] players;
	private static int[][] board;
	private static Display display;

	public static void main(String[] args)
	{
		// Create the board
		display = new Display();

		// Create the server
		new Server().go();
	}

	/**
	 * Creates a new Server object
	 */
	public Server()
	{
		super("Chinese Checkers Server");
		setContentPane(display);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.WHITE);
		pack();
		setVisible(true);
	}

	/**
	 * Runs the server
	 */
	public void go()
	{
		// Fetches information about the players and the game
		noOfPlayers = display.getNoOfPlayers();
		timeOut = display.getTimeOut();
		players = display.setUpBoard(noOfPlayers);

		// Initializes player connections
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

			while (playersConnected != noOfPlayers)
			{
				client = serverSocket.accept();
				playersConnected++;
				System.out.printf("Client #%d connected!%n", playersConnected);
				clients.add(new Client(client, players[playersConnected - 1],
						timeOut));
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
		{
			clients.get(i).newGame(players[i]);
		}

		for (int i = 0; i < board.length; i++)
		{
			for (int j = 0; j < board[0].length; j++)
			{
				// If the location has a player piece
				if (board[i][j] != 0 && board[i][j] != -1)
				{
					// Tells everyone to place pieces
					// 3 (colour) (row) (col)
					shout("3 " + board[i][j] + " " + i + " " + j);
				}
			}
		}

		display.update(board, players[turn-1]);
		gameStarted = true;
	}

	/**
	 * Checks to see if a player has won. A win occurs when a player's opposite
	 * triangle is completely filled and at least one of the spots is occupied
	 * by a piece of that player.
	 */
	public void checkForWin()
	{
		// Check to see if any players have won.
		boolean[] wins = new boolean[7];
		wins[1] = checkTriangle(-1, 4, board, 16, 12);
		wins[2] = checkTriangle(1, 5, board, 9, 13);
		wins[3] = checkTriangle(-1, 6, board, 7, 12);
		wins[4] = checkTriangle(1, 1, board, 0, 4);
		wins[5] = checkTriangle(-1, 2, board, 7, 3);
		wins[6] = checkTriangle(1, 3, board, 9, 4);

		for (int player = 1; player <= 6; player++)
			if (wins[player])
			{
				gameOver = true;
				gameStarted = false;
				System.out.printf("Player %d has won!", (player + 2) % 6 + 1);

				shout("7 " + (player + 2) % 6 + 1);
			}
	}

	/**
	 * Checks each piece in the triangle to determine whether a win has occurred
	 * @param attitude whether the triangle is upright or inverted
	 * @param player the number of the player
	 * @param board the game board
	 * @param row the row of the starting position to check
	 * @param col the col of the starting position to check
	 * @return whether a win has occurred
	 */
	public static boolean checkTriangle(int attitude, int player,
			int[][] board,
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
					hasWin = true;
			}
		return win && hasWin;
	}

	/**
	 * Sends a command to all players connected to the server
	 * @param command the message to be sent
	 */
	public void shout(String command)
	{
		for (Client c : clients)
		{
			c.sendCommand(command);
		}
	}

	/**
	 * Checks to see if a move is valid
	 * @param row the initial row of the move
	 * @param col the initial col of the move
	 * @param goalRow the final row of the move
	 * @param goalCol the final col of the move
	 * @return
	 */
	static boolean valid(int row, int col, int goalRow, int goalCol)
	{
		if (Math.abs(row - goalRow) <= 1 && Math.abs(col - goalCol) <= 1
				&& row - goalRow != -(col - goalCol))
			return true;
		System.out.println("Cannot move directly, trying to hop");
		if (hop(row, col, goalRow, goalCol, new boolean[17][17]))
			return true;
		System.out.println("Cannot hop");
		return false;
	}

	/**
	 * Checks to see if a hop is possible from one coordinate to another
	 * @param row the initial row of the move
	 * @param col the initial col of the move
	 * @param goalRow the final row of the move
	 * @param goalCol the final col of the move
	 * @param vis the boolean array of visited moves
	 * @return
	 */
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

	/**
	 * Checks to see if a coordinate is within a 17 by 17 array
	 * @param row the row of the coordinate
	 * @param col the col of the coordinate
	 * @return whether the coordinate is within the array
	 */
	static boolean inBounds(int row, int col)
	{
		if (row >= 0 && col >= 0 && row < board.length && col < board[0].length)
			return true;
		System.out.println("Move out of bounds");
		return false;
	}

	/**
	 * Keeps track of each player
	 */
	class PlayerThread implements Runnable
	{
		private Client client;
		private int colour;

		/**
		 * Creates a new player
		 * @param client the Object used to communicate with the player
		 */
		PlayerThread(Client client)
		{
			this.client = client;
			colour = client.getColour();
		}

		/**
		 * Communicates moves between the player and the server
		 */
		public void run()
		{
			while (!gameOver)
			{
				try
				{
					// Be nice to the JVM
					Thread.sleep(10);

					// If it is the player's turn and the has has started
					if (players[turn - 1] == colour && gameStarted)// //////////////////////////////
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
								System.out.printf(
										"Move from player %d was invalid!%n",
										colour);
							}
							else
							{
								shout("1 " + move[0][0] + " " + move[0][1]
										+ " " + move[1][0] + " " + move[1][1]);
								board[move[0][0]][move[0][1]] = 0;
								board[move[1][0]][move[1][1]] = colour;
							}
						}

						checkForWin();
						
						if(players[0] == colour)
							display.addTurn();
						
						turn = (turn % noOfPlayers) + 1;
						
						
						
						display.update(board, players[turn - 1]);
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

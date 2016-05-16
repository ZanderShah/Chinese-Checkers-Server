import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Server extends JPanel
{
	//Initialize variables
	private ServerSocket serverSocket;
	private ArrayList<Thread> threads;
	private ArrayList<Client> clients;
	private static boolean gameOver, gameStarted;
	private int turn;
	private static int[][] board;
	private static Display display;

	public static void main(String[] args)
	{
		//Create the board
		display = new Display();
		display.go();
		
		//Create the server
		new Server().go();
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

		//Connect players
		try
		{
			serverSocket = new ServerSocket(1337);

			while (playersConnected != 6)/////////////////////////////////////////////////////////1
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

		//Give each player their colour and tell them new game (2 1-6)
		for (int i = 0; i < playersConnected; i++)
			clients.get(i).newGame(i + 1);

		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[0].length; j++)
				//If the location has a player piece
				if (board[i][j] != 0 && board[i][j] != -1)
					//Tells everyone to place pieces
					//3 (colour) (row) (col)
					shout(new byte[] {3, (byte) board[i][j], (byte) i, (byte) j});

		gameStarted = true;
		
		checkForWins();
	}
	
	public void checkForWins()
	{
		System.out.println("Checking for wins...");
		while(!gameOver)
		{
			//Check to see if a cycle has been made and make sure that it's not the very first turn
			if(turn %6 == 1 && turn != 1)
			{
				//Check to see if any players have won.
				boolean[] wins = new boolean[7];
				wins[1] = checkTriangle(-1, board, 16, 12);
				wins[2] = checkTriangle(1, board, 9, 13);
				wins[3] = checkTriangle(-1, board, 7, 12);
				wins[4] = checkTriangle(1, board, 0, 4);
				wins[5] = checkTriangle(-1, board, 7, 3);
				wins[6] = checkTriangle(1, board, 9, 4);
				
				for(int player = 1; player <= 6; player++)
					if(wins[player])
					{
						gameOver = true;
						gameStarted = false;
						System.out.printf("Player %d has won!", player);
						byte[] winMessage = new byte[2];
						winMessage[0] = 7;
						winMessage[1] = (byte)player;
						shout(winMessage);
					}
			}
		}
	}

	public static boolean checkTriangle(int attitude, int[][] board,
			int row, int col)
	{
		boolean win = true;
		for (int i = 0; i < 4; i++)
			for (int j = 0; j <= i; j++)
				//If the place is empty
				if(board[attitude > 0 ? row + i : row - i][attitude > 0 ? col + j
						: col - j] == 0)
					win = false;
		return win;
	}
	
	public void shout(byte[] command)
	{
		for (Client c : clients)
		{
			c.sendCommand(command);
		}
	}

	static boolean valid(int r, int c, int gR, int gC)
	{
		if (Math.abs(r - gR) <= 1 && Math.abs(c - gC) <= 1
				&& r - gR != -(c - gC))
			return true;

		return hop(r, c, gR, gC, new boolean[17][17]);
	}

	static boolean hop(int r, int c, int gR, int gC, boolean[][] vis)
	{
		if (r == gR && c == gC)
			return true;

		vis[r][c] = true;

		boolean ret = false;

		if (inBounds(r + 2, c) && board[r + 1][c] > 0 && board[r + 2][c] == 0
				&& !vis[r + 2][c] && hop(r + 2, c, gR, gC, vis))
			ret = true;

		if (inBounds(r + 2, c + 2) && board[r + 1][c + 1] > 0
				&& board[r + 2][c + 2] == 0 && !vis[r + 2][c + 2]
				&& hop(r + 2, c + 2, gR, gC, vis))
			ret = true;

		if (inBounds(r - 2, c - 2) && board[r - 2][c - 2] > 0
				&& board[r - 2][c - 2] == 0 && !vis[r - 2][c - 2]
				&& hop(r - 2, c - 2, gR, gC, vis))
			ret = true;

		if (inBounds(r - 2, c) && board[r - 1][c] > 0
				&& board[r - 2][c] == 0 && !vis[r - 2][c]
				&& hop(r - 2, c, gR, gC, vis))
			ret = true;

		if (inBounds(r, c + 2) && board[r][c + 1] > 0
				&& board[r][c + 2] == 0 && !vis[r][c + 2]
				&& hop(r, c + 1, gR, gC, vis))
			ret = true;

		if (inBounds(r, c - 2) && board[r][c - 1] > 0
				&& board[r][c - 2] == 0 && !vis[r][c - 2]
				&& hop(r, c - 2, gR, gC, vis))
			ret = true;

		return ret;
	}

	static boolean inBounds(int r, int c)
	{
		return r >= 0 && c >= 0 && r < 17 && c < 17;
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

		@Override
		public void run()
		{
			while (!gameOver)
			{
				try
				{
					//If it is the player's turn and the has has started
					if ((turn-1)%6+1 == colour && gameStarted)////////////////////////////////
					{
						//Get the move from the client
						int[][] move = client.getMove();

						//If the player didn't time out
						if (move != null) {
							//Tell the client of an invalid move
							if (!inBounds(move[0][0], move[0][1])
									|| !inBounds(move[1][0], move[1][1])
									|| board[move[0][0]][move[0][1]] != colour
									|| board[move[1][0]][move[1][1]] != 0
									|| !valid(move[0][0], move[0][1], move[1][0],
											move[1][1]))
							{
								client.invalidMove();
							}
							else
								shout(new byte[] {1, (byte) move[0][0], (byte) move[0][1], (byte) move[1][0], (byte) move[1][1]});
						}

						turn++;// = (turn + 1) % 6;///////////////////////////////////////////
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

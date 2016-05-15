import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Server extends JPanel
{
	private ServerSocket serverSocket;
	private ArrayList<Thread> threads;
	private ArrayList<Client> clients;
	private static boolean gameOver, gameStarted;
	private int turn;
	private static int[][] board;
	private static Display display;

	public static void main(String[] args)
	{
		display = new Display();
		display.go();
		new Server().go();
	}

	public void go()
	{
		System.out.println("Waiting for player connections..");
		Socket client = null;
		int playersConnected = 0;
		turn = 1;
		threads = new ArrayList<Thread>();
		clients = new ArrayList<Client>();
		board = display.getBoard();

		try
		{
			serverSocket = new ServerSocket(1337);

			while (playersConnected != 1)
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

		for (int i = 0; i < playersConnected; i++)
			clients.get(i).newGame(i + 1);

		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[0].length; j++)
				if (board[i][j] != 0 && board[i][j] != -1)
					shout(new byte[] {3, (byte) board[i][j], (byte) i, (byte) j});

		gameStarted = true;
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
					if (turn == colour && gameStarted)
					{
						int[][] move = client.getMove();

						if (move != null) {
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

						turn = (turn + 1) % 6;
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

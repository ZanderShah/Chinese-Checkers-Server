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
	private int turn, board[][];
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
			whisper(String.format("2 %d", i + 1), i);
		
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[0].length; j++)
				if (board[i][j] != 0 && board[i][j] != -1)
					shout(String.format("3 %d %d %d", board[i][j], i, j));
		
		gameStarted = true;
	}

	public void shout(String message)
	{
		for (Client c : clients)
		{
			c.getOut().println(message);
			c.getOut().flush();
		}
	}

	public void whisper(String message, int recipient)
	{
		clients.get(recipient).getOut().println(message);
		clients.get(recipient).getOut().flush();
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
						client.getOut().println(4);
						client.getOut().flush();

						int[][] move = client.getMove();
						System.out.println(move[0][0] + " " + move[0][1]);

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

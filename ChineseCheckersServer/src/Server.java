import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Server extends JPanel
{
	private ServerSocket serverSocket;
	private ArrayList<Thread> threads;
	private ArrayList<Client> clients;
	private static boolean gameOver;
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

			while (playersConnected != 6)
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
					if (turn == colour)
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

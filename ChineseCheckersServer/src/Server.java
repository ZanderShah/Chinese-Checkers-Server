import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server
{
	private ServerSocket serverSocket;
	private ArrayList<Thread> threads;
	private ArrayList<Client> clients;
	private boolean gameStillRunning;

	public static void main(String[] args)
	{
		new Server().go();
	}

	public void go()
	{
		System.out.println("Waiting for player connections..");
		Socket client = null;
		int playersConnected = 0;
		threads = new ArrayList<Thread>();
		clients = new ArrayList<Client>();

		try
		{
			serverSocket = new ServerSocket(1337);

			while (playersConnected != 6)
			{
				client = serverSocket.accept();
				playersConnected++;
				System.out.printf("Client #%d connected!", playersConnected);
				clients.add(new Client(client, playersConnected));
				threads.add(new Thread(new Player(
						clients.get(clients.size() - 1))));
				threads.get(threads.size() - 1).start();
			}
		}
		catch (Exception e)
		{
			System.out.println("Connection error!");
			e.printStackTrace();
		}

		gameStillRunning = true;
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
	
	public boolean isGame()
	{
		return gameStillRunning;
	}
	
	static class Player implements Runnable
	{
		private Client client;
		private int colour;

		Player(Client client)
		{
			this.client = client;
			colour = client.getColour();
		}

		@Override
		public void run()
		{
		
		}
	}

}


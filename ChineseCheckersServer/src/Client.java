import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client
{
	private static final int TIMEOUT = 2000;
	
	private Socket sock;
	private InputStream in;
	private BufferedReader br;
	private OutputStream out;
	private PrintWriter pw;
	private int colour;

	public Client(Socket socket, int colour)
	{
		try
		{
			sock = socket;
			in = sock.getInputStream();
			br = new BufferedReader(new InputStreamReader(in));
			out = sock.getOutputStream();
			pw = new PrintWriter(out);
			this.colour = colour;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public int getColour()
	{
		return colour;
	}

	/**
	 * Tells the client that a new game is beginning
	 * 
	 * @param c The colour of this client.
	 */
	public void newGame(int c)
	{
		colour = c;
		pw.println("2 " + c);
		pw.flush();
	}

	/**
	 * Gets a move from the client
	 * 
	 * @return An array defining the move done by the client. The first row in
	 *         the array indicates the row and column of the piece's initial
	 *         position. The second row in the array indicates the row and
	 *         column of the piece's final position.
	 */
	public int[][] getMove()
	{
		try
		{
			while (br.ready()) br.readLine();
			System.out.printf("Player %d queried for move%n", colour);
			pw.println("4");
			pw.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		long start = System.currentTimeMillis();
		MoveThread m = new MoveThread(br, colour);
		Thread t = new Thread(m);

		t.start();

		// Query for a move every 10ms until the timeout is reached or the move is recieved
		while (m.getMove() == null && System.currentTimeMillis() - start < TIMEOUT) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		m.timeout();

		int[][] move = m.getMove();

		// Tell a player of timeout
		if (move == null)
		{
			System.out.printf("Player %d timed out while choosing a move%n", colour);
			pw.println("6");
			pw.flush();
		}

		return move;
	}

	public void invalidMove() {
		pw.println("5");
		pw.flush();
	}
	
	public void sendCommand(String command) {
		pw.println(command);
		pw.flush();
	}

	public void sendMove(int[][] m)
	{
		sendCommand("1 " + m[0][0] + " " + m[0][1] + " " + m[1][0] + " " + m[1][1]);
	}
}

/**
 * Thread for receiving a move from the client. Can be timed out if the player
 * takes too long.
 */
class MoveThread implements Runnable
{
	private boolean timeout = false;
	private int[][] move;
	private boolean moveReceived = false;
	private BufferedReader in;
	int colour;

	public MoveThread(BufferedReader in, int c)
	{
		move = new int[2][2];
		this.in = in;
		colour = c;
		move[0][0] = -1;
		move[0][1] = -1;
		move[1][0] = -1;
		move[1][1] = -1;
	}

	public void run()
	{
		try
		{
			while (!in.ready() && !timeout){};
			if (!timeout)
			{
				//If the first number is 1 (indicating a player wants to move)
				String[] command = in.readLine().split(" ");
				if (Integer.parseInt(command[0]) == 1)
				{
					move[0][0] = Integer.parseInt(command[1]);
					move[0][1] = Integer.parseInt(command[2]);
					move[1][0] = Integer.parseInt(command[3]);
					move[1][1] = Integer.parseInt(command[4]);
					moveReceived = true;
					System.out.printf("Move recieved from player %d: [%d %d] -> [%d %d]%n",
									colour, move[0][0], move[0][1], move[1][0], move[1][1]);
				}
			}
		}
		catch (Exception e)
		{

		}
	}

	public boolean timeout()
	{
		timeout = true;
		return moveReceived;
	}

	public int[][] getMove()
	{
		if (moveReceived)
		{
			return move;
		}
		else
		{
			return null;
		}
	}
}

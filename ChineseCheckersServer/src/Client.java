import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Client
{
	private Socket sock;
	private InputStream in;
	private OutputStream out;
	private int colour;

	public Client(Socket socket, int colour)
	{
		try
		{
			sock = socket;
			in = sock.getInputStream();
			out = sock.getOutputStream();
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
		try
		{
			out.write(c);
			out.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
			System.out.printf("Player %d queried for move%n", colour);
			out.write(4);
			out.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		MoveThread m = new MoveThread(in, colour);
		Thread t = new Thread(m);
		
		t.start();
		
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		m.timeout();
		
		int[][] move = m.getMove();
		
		if (move == null)
		{
			try
			{
				System.out.printf("Player %d timed out while choosing a move%n", colour);
				out.write(6);
				out.flush();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return move;
	}

	public void invalidMove() {
		try {
			out.write(5);
			out.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendCommand(byte[] command) {
		try {
			out.write(command, 0, command.length);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	private InputStream in;
	int colour;

	public MoveThread(InputStream in, int c)
	{
		move = new int[2][2];
		this.in = in;
		colour = c;
		Arrays.fill(move, new int[] { -1, -1 });
	}

	@Override
	public void run()
	{
		try
		{
			while (in.available() < 5 && !timeout){};
			
			if (!timeout)
			{
				int command = in.read();
				if (command == 1)
				{
					move[0][0] = in.read();
					move[0][1] = in.read();
					move[1][0] = in.read();
					move[1][1] = in.read();
					moveReceived = true;
					System.out.printf("Move recieved from player %d: [%d %d] -> [%d %d]%n", colour, move[0][0], move[0][1], move[1][0], move[1][1]);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public boolean timeout()
	{
		timeout = true;
		return moveReceived;
	}

	public int[][] getMove()
	{
		if (moveReceived) {
			return move;
		} else {
			return null;
		}
	}

	public void sendMove(int[][] m)
	{

	}
}

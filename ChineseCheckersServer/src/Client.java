import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class Client
{
	private Socket sock;
	private BufferedReader in;
	private PrintWriter out;
	private int colour;
	private InputStream is;

	public Client(Socket socket, int colour)
	{
		try
		{
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			sock = socket;
			is = socket.getInputStream();
			this.colour = colour;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public BufferedReader getIn()
	{
		return in;
	}

	public PrintWriter getOut()
	{
		return out;
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
			out.write(4);
			out.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		MoveThread m = new MoveThread(is);
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
		
		
		if (!m.timeout())
		{
			try
			{
				out.write(6);
				out.flush();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return m.getMove();
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

	public MoveThread(InputStream in)
	{
		move = new int[2][2];
		this.in = in;
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
		return move;
	}

	public void sendMove(int[][] m)
	{

	}
}

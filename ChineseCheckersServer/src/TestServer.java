import java.net.ServerSocket;

public class TestServer {

	public static void main(String[] args) throws Exception {
		ServerSocket ss = new ServerSocket(6000);
		
		Client c = new Client(ss.accept(), 0);
		
		c.getMove();
	}
}
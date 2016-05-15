import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TestClient {

	public static void main(String[] args) throws Exception {
		Socket serv = new Socket("localhost", 6000);
		
		InputStream in = serv.getInputStream();
		OutputStream out = serv.getOutputStream();
		
		in.read();
		Thread.sleep(2016);
		out.write(new byte[] {1, 1, 1, 1, 1});
		out.flush();
		in.read();
	}
}
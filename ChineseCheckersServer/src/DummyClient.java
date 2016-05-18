import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class DummyClient {

	public static void main(String[] args) throws Exception {
		Socket[] s = new Socket[5];
		
		for (int i = 0; i < 5; i++) {
			s[i] = new Socket("localhost", 420);
		}
		
		System.out.println("Press enter to exit");
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		br.readLine();
	}
}
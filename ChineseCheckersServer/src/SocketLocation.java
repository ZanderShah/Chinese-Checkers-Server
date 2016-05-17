public class SocketLocation {
	private String ip;
	private int port;

	SocketLocation(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public String getIP() {
		return ip;
	}

}

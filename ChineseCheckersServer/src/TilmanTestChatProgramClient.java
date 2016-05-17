import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

class TilmanTestChatProgramClient {

	JButton sendButton, clearButton;
	JTextField textField;
	JTextArea msgArea;
	JPanel southPanel;
	JScrollPane scroll;
	PrintWriter out;
	boolean finished = false;
	
	
	

	public static void main(String[] args) throws Exception {
		new TilmanTestChatProgramClient().go();
	}

	public void go() throws Exception {
		JFrame window = new JFrame("Chat Client");

		setUpWindow(window);

		SocketLocation s = where();
		//String user = who();

		setUpPanel();

		window.add(BorderLayout.CENTER, scroll);
		window.add(BorderLayout.SOUTH, southPanel);
		window.setSize(400, 400);
		window.setVisible(true);

		Socket mySocket = new Socket(s.getIP(), s.getPort());

		out = new PrintWriter((mySocket.getOutputStream()));
		//out.println(user);
		out.flush();
		InputStreamReader myStream = new InputStreamReader(
				mySocket.getInputStream());
		BufferedReader myReader = new BufferedReader(myStream);

		// TODO get a list of all people that are online

		while (mySocket.isConnected()) {
			if (myReader.ready()) {
				String msg = myReader.readLine();
				msgArea.append(msg + "\n");
			}

			if (finished)
				mySocket.close();

		}

		out.close();
		mySocket.close();

		// call a method that connects to the server
		// after connecting loop and keep appending[.append()] to the JTextArea
	}

	public void setUpPanel() {
		southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(2, 0));

		sendButton = new JButton("SEND");
		ActionListener send = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = textField.getText();
				if (!s.equals("")) {
					textField.setText("");
					out.println(s);
					out.flush();
					msgArea.append("You: " + s + "\n");
					DefaultCaret caret = (DefaultCaret) msgArea.getCaret();
					caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
				}
			}
		};

		sendButton.addActionListener(send); // If the button is hit

		clearButton = new JButton("CLEAR");
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textField.setText("");
			}
		});

		JLabel errorLabel = new JLabel("");

		textField = new JTextField(10);
		textField.addActionListener(send); // If enter is hit

		msgArea = new JTextArea();
		msgArea.setEditable(false);
		scroll = new JScrollPane(msgArea);

		southPanel.add(textField);
		southPanel.add(sendButton);
		southPanel.add(errorLabel);
		southPanel.add(clearButton);
	}

	// ****** Inner Classes for Action Listeners ****

	// To complete this you will need to add action listeners to both buttons
	// clear - clears the textfield
	// send - send msg to server (also flush), then clear the JTextField

	// public boolean validIP(String s){
	//
	// }

	public String who() {
		String user = JOptionPane.showInputDialog("What is your name?", "Tman");
		if (user.equals("") || user == null)
			user = "Default";
		return user;
	}

	public SocketLocation where() {
		// SocketLocation s = new SocketLocation("10.242.190.128", 6969);
		SocketLocation s = new SocketLocation("localhost", 420);
//		String str = JOptionPane.showInputDialog("What is the ip?", s.getIP());
//		int p = Integer.parseInt(JOptionPane.showInputDialog("Which port?",
//				s.getPort()));
		return new SocketLocation(s.getIP(), s.getPort());
	}

	public void setUpWindow(JFrame window) {
		window.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowClosed(WindowEvent e) {
				finished = true;
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
			}
		});
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
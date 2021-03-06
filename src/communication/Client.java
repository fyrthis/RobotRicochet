package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;

import launcher.Debug;

public class Client extends Observable implements Runnable {

	//PATTERN SINGLETON
	private Client() {
		connected = false;
		this.addObserver(Debug.get());
	}

	private static class ClientHolder
	{		
		private final static Client instance = new Client(); 
	}
	public static Client getInstance()
	{
		return ClientHolder.instance;
	}


	//FIELDS
	private Socket socket;

	private PrintWriter out;
	private BufferedReader in;

	private boolean connected = false;

	private int port;
	private String hostname = "127.0.0.1";

	public void connect() throws ConnectException, UnknownHostException, IOException {
		connect(port, hostname);
	}

	public void connect(int port, String hostname) throws UnknownHostException, java.net.ConnectException, IOException {
		if(connected) { return; } //Already connected

		Debug.get();
		System.out.println("(Client:"+Debug.curName+")(Client:connect) : connecting to host...");
		socket = new Socket(hostname, port);
		System.out.println("(Client:"+Debug.curName+")(Client:connect) : Connected to host");

		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);

		connected  = true;
		new Thread(this).start();

	}

	public void disconnect() {
		if(socket != null && connected) {
			this.connected = false;
		}
	}

	public void sendMessage(String msg) throws IOException
	{
		if(connected) {
			out.println(msg+"\n");
			out.flush();
		}
	}


	@Override public void run() { 
		listenMessages();
	}
	
	public void listenMessages()
	{
		String msg;
		try {
			while(connected && (msg = in.readLine())!= null)
			{
				System.out.println("(Client:"+Debug.curName+")(Client:listenMessages) received : "+msg+" ");
				this.setChanged();
				this.notifyObservers(msg);
			}
		}
		catch(Exception e) { e.printStackTrace(); }
		finally {
			try {
				socket.close(); //From doc : Closing this socket will also close the socket's InputStream and OutputStream.
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}




	//GETTERS AND SETTERS
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
}
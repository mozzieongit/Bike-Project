package zeug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class User {
	private Socket socket;
	private PrintWriter pw;
	private BufferedReader br;
	
	private Thread t ;

	public int id = -1;
	
	private ReceiveListener receivelistener;
	
	public boolean connected = true;
	
	public User(Socket socket, final ReceiveListener receivelistener) {
		this.socket = socket;
		this.receivelistener = receivelistener;
		try {
			pw = new PrintWriter(socket.getOutputStream());
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(IOException e) {
			connected = false;
			e.printStackTrace();
		}
		
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Start listening on User...");
				while(Main.running) {
					try {
						String s = br.readLine();
						
					//	System.out.println("S: " + s);
						
						try {
							receivelistener.receive(s);
						}catch(Exception ex) {
							shutdown();
							//ex.printStackTrace();
						}
					}catch(IOException e) {
						shutdown();
						System.out.println("Disconnected!");
						break;
					}
				}
			}
		});
		
		t.start();
	}
	
	public void send(String s) {
		if(pw != null) {
			pw.write(s);
			pw.flush();
		}
	}
	
	public void disconnect() {
		connected = false;
		try {
			t.join();
			pw.close();
			br.close();
			socket.close();
		}catch(IOException e) {
			//e.printStackTrace();
		}catch(Exception ec) {
			//ec.printStackTrace();
		}
		System.out.println("Disconnected!");
	}
	
	public void shutdown() {
		connected = false;
		try {
			pw.close();
			br.close();
			socket.close();
		}catch(IOException e) {
			//e.printStackTrace();
		}catch(Exception ec) {
			//ec.printStackTrace();
		}
		
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void setID(int Id) {
		this.id = Id;
	}
	
	interface ReceiveListener{
		public void receive(String s);
	}
}

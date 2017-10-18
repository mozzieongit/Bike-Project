package fahrradsicherung;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class User {

	
	private Socket socket;
	private PrintWriter os;
	private BufferedReader is;
	
	private Thread t;
	
	public int ID = -1;
	
	private ReceiveListener receiveListener;
	
	private boolean connected = true;
	public User(Socket socket,ReceiveListener receiveListener){
		this.socket = socket;
		this.receiveListener  = receiveListener;
		try {
			os = new PrintWriter(socket.getOutputStream());
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			connected = false;
			e.printStackTrace();
		}
		
		t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("Start listening on User...");
				while(Main.running){
					try {
						String s = is.readLine();

						System.out.println("S: " + s);
							try{
								receiveListener.receive(s);

								/*if(Main.ConsoleOnly){
									System.out.println("Nachricht: " + s + "\n");
								}
								window.addText(Color.BLUE, "", s);*/
							}catch(Exception ex){
								Main.window.addText(Color.RED, "ERROR", ex.getLocalizedMessage());
							}
							
					} catch (IOException e) {

						disconnect();
						break;
					}
					
					
					
				}
			}
		});
		
		t.start();
	}

	
	public void send(String s){
		if(os != null){
			os.write(s);
			os.flush();
		}
	}
	
	public void disconnect(){
		connected = false;
		try {
			os.close();
			is.close();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnected(){
		return connected;
	}
	
	public void setID(int ID){
		this.ID = ID;
	}

	
	
	interface ReceiveListener{
		public void receive(String s);
		
	}
	
	
	
}

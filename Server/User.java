package fahrradsicherung;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class User {

	
	private Socket socket;
	private ObjectOutputStream os;
	private ObjectInputStream is;
	
	private Thread t;
	
	private String name = "";
	private String pw = "";
	public int ID = -1;
	
	private boolean connected = true;
	public User(Socket socket,Window window){
		this.socket = socket;
		try {
			os = new ObjectOutputStream(socket.getOutputStream());
			is = new ObjectInputStream(socket.getInputStream());
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
						String s = is.readUTF();
						
							try{
								String[] split = s.split(Main.split);
								
								if(ID == -1){
									name = split[0];

									ID = window.addClient(name);
								}

									if(Main.ConsoleOnly){
										System.out.println("Nachricht: " + s + "\n");
									}
									window.addText(Color.BLUE, name, s);
							}catch(Exception ex){
								window.addText(Color.RED, "ERROR", ex.getLocalizedMessage());
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
			try {
				os.writeUTF(s);
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	@Override
	public String toString() {
		String s = ID + ": " + name + " ";
		if(isConnected()){
			s += " is connected!";
		}else{
			s += " is disconnected!";
		}
		return s;
	}
	
	
	
	
	
	
	
}

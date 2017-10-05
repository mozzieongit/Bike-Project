package fahrradsicherung;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	
	private ServerSocket serverSocket;
	
	private List<User> users = new ArrayList<User>();
	private int port;
	private Window window;
	public Server(int port){
		this.port = port;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	public void start(Window window){
		this.window = window;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(Main.running){
					try {
					//	System.out.println("Server started!");
						Socket socket = serverSocket.accept();
						users.add(new User(socket,window));
						
						
						
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					
				}
			}
		}).start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(Main.running){
					
					for(int i = users.size() -1; i >= 0;i--){
						User user = users.get(i);
						if(user.isConnected() == false){
						
							window.removeClient(user.ID);
							for(int i1 = i ; i1 < users.size();i1++){
								users.get(i1).ID --;
							}
							users.remove(i);
						}
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}).start();
		
	}
	
	public void stop(){
		Main.running = false;
		
		for(int i = 0; i < users.size();i++){
			users.get(i).disconnect();
		}
		System.out.println("Server stopped!");
	}
	public void broadcast(String text){
		for(User user:users){
			user.send(text);
		}
	}
	
	public User getUser(int index){
		return users.get(index);
	}
	
	public int getPort(){
		return port;
	}
}

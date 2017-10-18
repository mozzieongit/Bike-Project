package fahrradsicherung;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	
	private ServerSocket serverSocket;
	
	private List<ClientApp> apps = new ArrayList<ClientApp>();
	private List<ClientRasp> rasps = new ArrayList<ClientRasp>();
	private int port;
	
	private Server copyOfThis;
	public Server(int port){
		this.port = port;
		
		this.copyOfThis = copyOfThis;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		apps.add(new ClientApp("Steffen", "PW"));
		rasps.add(new ClientRasp("ID", "PW"));
		
	}
	
	
	private BufferedReader is;
	private Socket socket;
	public void start(Window window){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(Main.running){
					try {
						System.out.println("Wait...");
						 socket = serverSocket.accept();
						System.out.println("Someone connected!");
						window.addText(Window.DARK_GREEN, "Unknown", "Someone connected!\n");
				
						 is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
								
								
								String s = "";
								
								while(true){
									try {
										s = is.readLine();
									} catch (IOException e) {
										e.printStackTrace();
										break;
									}
									window.addText(Window.DARK_GREEN, "Unknown", s + "\n");
									System.out.println("s: " + s);
									
										try{
											String[] split = s.split(Main.split);
											//APP LOGIN : 1&"Name"&"Passwort"
											//RASP LOGIN: 11&"Name"&"Passwort"
											
	
											int GerätArt = Integer.parseInt(split[0]);
											if(GerätArt == 1){
												//Login App
												ClientApp app = getApp(split[1], split[2]);
												if(app != null){
													window.addText(Color.BLACK, split[1], "Erfolgreich eingeloggt\n");
													System.out.println("Eingeloggt!");
													app.connect(socket);
														app.send("1");
														// TODO: GERÄTEINFOS FEHLEN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
													break;
												}else{
													window.addText(Window.DARK_GREEN, "Unknown", "Name oder Passwort ist falsch!\n");
													//Name oder Passwort ist falsch
													System.out.println("Name oder Passwort ist falsch!");
												}
											}
											else if(GerätArt == 11){
												//Login Rasp
												ClientRasp rasp = getRasp(split[1], split[2]);
												if(rasp != null){
													window.addText(Color.BLACK, split[1], "Erfolgreich eingeloggt\n");
													System.out.println("Eingeloggt!");
													rasp.connect(socket,copyOfThis);
														rasp.send("11");
													break;
												}else{
													window.addText(Window.DARK_GREEN, "Unknown", "Name oder Passwort ist falsch!\n");
													//ID oder Passwort ist falsch
													System.out.println("ID oder Passwort ist falsch!");
												}
												
											}
											else{
												System.out.println("Fail wurde nicht eingeloggt! (" + s + ")");
											}
											
										}catch(Exception ex){
											ex.printStackTrace();
											Main.window.addText(Color.RED, "ERROR", "\n" + ex.getLocalizedMessage());
											break;
										}
								}
						
						
						
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
					
				
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}).start();
		
	}
	
	public ClientApp getApp(String name,String pw){
		for(int i = 0;i < apps.size();i++){
			ClientApp app = apps.get(i);
			if(app.getName().equals(name) && app.pwRight(pw)){
				return app;
			}
		}
		return null;
		
	}
	public ClientRasp getRasp(String ID,String pw){
		for(int i = 0;i < rasps.size();i++){
			ClientRasp rasp = rasps.get(i);
			if(rasp.getID().equals(ID) && rasp.pwRight(pw)){
				return rasp;
			}
		}
		return null;
		
	}
	
	public void stop(){
		Main.running = false;
		
		for(int i = 0; i < apps.size();i++){
			apps.get(i).disconnect();
		}
		for(int i = 0; i < rasps.size();i++){
			rasps.get(i).disconnect();
		}
		System.out.println("Server stopped!");
	}
	
	public void broadcastToAllAppsWithGerätID(String ID,String text){

		for(int i = 0; i < apps.size();i++){
			ClientApp app = apps.get(i);
			if(app.hasGerätWithID(ID)){
				app.send(text);
			}
		}
	}
	
	public void broadcast(String text){

		for(int i = 0; i < apps.size();i++){
			ClientApp app = apps.get(i);
			app.send(text);
		}
	}
	
	public int getPort(){
		return port;
	}
}

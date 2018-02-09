package zeug;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.net.*;
import java.io.*;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

//Einrichten des Servers
public class Server {
	
	private ServerSocket serversocket;
	
	private static List<App> apps = new ArrayList<App>();
	private static List<Rasp> rasps = new ArrayList<Rasp>();
	Thread sleep;
	Thread main;
	
	private int port;
	
	private Server copyOfThis;
	
	//Erstellen des Servers
	public Server(int port){
		
		System.out.println();
		
		//IP an Datenbank uebergeben, damit Clients auf Server zugreifen koennen
		setIP();
		
		this.port = port;
		
		this.copyOfThis = this;
		
		//Server erstellen
		try{
			serversocket = new ServerSocket(port);
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//Zugangsdaten von Raspberrys aus Datei einlesen
		try {
			BufferedReader br = new BufferedReader(new FileReader(Main.location + "Rasps.txt"));
			String zeile = "";
			while((zeile = br.readLine()) != null) {
				String[] split = zeile.split(Main.split);
				
				rasps.add(new Rasp(split[0], split[1]));
			}
			br.close();
		} catch (IOException e) {
			//e.printStackTrace();
			out.println("File not found!");
		}
		
		//Zugangsdaten von Apps aus Datei einlesen
		try {
			BufferedReader br = new BufferedReader(new FileReader(Main.location + "Apps.txt"));
			String zeile = "";
			while((zeile = br.readLine()) != null) {
				String[] split = zeile.split(Main.split);
				
				apps.add(new App(split[0], split[1]));
			}
			br.close();
		} catch (IOException e) {
			//e.printStackTrace();
			out.println("File not found!");
		}		
	}
	
	private BufferedReader br;
	private PrintWriter pw;
	
	private Socket socket;
	
	//Server starten
	public void start(){
		main = new Thread(new Runnable() {
			@Override
			public void run(){
				while(Main.running){
					try{
						//Warten auf Verbindung
						out.println("Waiting...");
						//Verbindung annehmen
						socket = serversocket.accept();
						//out.println("Connected!");
						
						//Oeffnen der Streams zur Kommunikation
						br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						pw = new PrintWriter(socket.getOutputStream());
						
						String s = "";
						
						
						while(Main.running){
							//Warten auf Zugangsdaten von Client
							try{
								s = br.readLine();
							}catch(IOException e){
								e.printStackTrace();
							}
							
							//Ausgabe von Input
							out.println("Client: " + s);
							
							try{
								//Aufsplitten des angekommenen Strings
								String[] split = s.split(Main.split);
								
								//Erster Teil des Strings wird in int geändert
								int art = Integer.parseInt(split[0]);
								
								//Pruefen, ob erster Teil des Strings "1" ist und damit von der App kommt
								if(art == 1){
									//Pruefen, ob die Zugangsdaten richtig sind und dann entsprechende Instanz von App erhalten
									App app = getApp(split[1], split[2]);
									
									//Wenn App vorhanden ist, wird diese eingeloggt
									if(app != null){
										out.println("Eingeloggt");
										app.connect(socket);
										
										//Bestaetigung an App senden
										app.send("1");
										
										//Anzahl an mit App verknuepften Rasps ermitteln
										int size = app.getSize();
										
										//Thread fuer 750ms ruhen lassen, damit App sich einrichten kann
										Thread.sleep(750);
										
										//Informationen ueber verknuepfte Rasps an App senden
										for(int j=0;j < size;j++) {
											app.send(app.getData(j));
										}
										break;
									}else{
										System.out.println("Falsches Passwort oder falscher Benutzername!");
										if(pw != null) {
											pw.write("21&\n");
											pw.flush();
										}
										pw.close();
										br.close();
										break;
									}
									
								//Pruefen, ob erster Teil des Strings "11" ist und damit von einem Rasp kommt 	
								}else if(art == 11){
									//Pruefen, ob Zugangsdaten stimmen und die Instanz von Rasp erhalten
									Rasp rasp = getRasp(split[1], split[2]);
									//Wenn Rasp vorhanden ist, wird dieser eingeloggt 
									if(rasp != null) {
										out.println("Eingeloggt!");
										rasp.connect(socket, copyOfThis);
										
										//Bestaetigung an Rasp senden
										rasp.send("11");
										break;
									}else {
										out.println("ID oder Passwort falsch!");
									}
								}else{
									out.println("Error: nicht eingeloggt! (" + s + ")");
								}
							}catch(Exception ec) {
								ec.printStackTrace();
								break;
							}
						}
						
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}
		});
		
		main.start();
		sleep = new Thread(new Runnable() {
			@Override
			public void run() {
				while(Main.running) {
					try {
						Thread.sleep(300);
					}catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		//sleep.start();
	}
	
	//Getter fuer Instanzen von App
	public App getApp(String n, String pw) {
		for(int i = 0; i < apps.size();i++) {
			App app = apps.get(i);
			if(app.getName().equals(n) && app.pwRight(pw)) {
				return app;
			}
		}
		return null;
	}
	
	//Getter fuer Instanzen von Rasps
	public static Rasp getRasp(String id, String pw) {
		for(int i = 0;i < rasps.size();i++) {
			Rasp rasp = rasps.get(i);
			if(rasp.getID().equals(id) && rasp.pwRight(pw)) {
				out.println("Rasp übergeben!");
				return rasp;
			}
		}
		return null;
	}
	
	//Hinzufuegen von neuen Rasps
	public static Rasp newRasp(String id, String pw) {
		rasps.add(new Rasp(id, pw));
		for(int i = 0;i < rasps.size();i++) {
			Rasp rasp = rasps.get(i);
			if(rasp.getID().equals(id) && rasp.pwRight(pw)) {
				System.out.println("Neuer Rasp erstellt!");
				return rasp;
			}
		}
		return null;
	}
	
	//Datensicherung und stoppen des Servers
	public void stop() {
		Main.running = false;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		//Datensicherung der Zugangsdaten der Apps
		try {
			PrintStream diskWriter = new PrintStream(new File(Main.location + "Apps.txt"));
			for(int i=0;i < apps.size();i++) {
				if(i>0) {
					diskWriter.print("\n");
				}
				diskWriter.print(apps.get(i).getAppData());
			}
			diskWriter.close();
			
			//Datensicherung der Zugangsdaten der Rasps	
			diskWriter = new PrintStream(new File(Main.location + "Rasps.txt"));
			for(int i=0;i < rasps.size();i++) {
				if(i>0) {
					diskWriter.print("\n");
				}
				diskWriter.print(rasps.get(i).getRaspData());
			}
			diskWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			//out.println("File not found");
		}
		
		try {
			PrintStream diskWriter = new PrintStream(new File("Location.txt"));
			diskWriter.print(Main.location);
			diskWriter.close();
		} catch (FileNotFoundException e1) {
			//e1.printStackTrace();
		}
		
		out.println("App Threads stopped!");
		
		//Apps disconnecten
		for(int i = 0; i < apps.size(); i++) {
			apps.get(i).disconnect();
		}
		
		//Rasps disconnecten
		for(int i = 0; i < rasps.size();i++) {
			rasps.get(i).disconnect();
		}
		
		out.println("Client Threads stopped!");
		/*
		try {
			main.join();
			//sleep.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
		out.println("Server Threads stopped!");
		
		System.exit(0);
		out.println("Server stopped!");
	}
	
	//Nachrichten an Apps die Rasp mit ID haben
	public static void broadcastID(String id, String text) {
		for(int i = 0; i < apps.size();i++) {
			App app = apps.get(i);
			if(app.hasGeraetWithID(id)) {
				if(app.isConnected()) {
				app.send(text);
				}
			}
		}
	}
	
	//Benachrichtigung an Apps die Rasp mit ID haben
	public void broadcastIDNotiAlarm(String id, String titel) {
		for(int i = 0; i < apps.size();i++) {
			App app = apps.get(i);
			if(app.hasGeraetWithID(id)) {
				System.out.println("Notification: " + titel + ", " + app.getName(id) + " wurde bewegt!");
				app.sendNotification(titel, app.getName(id) + " wurde bewegt!");
			}
		}
	}
	
	//Broadcast an alle Apps
	public void broadcast(String text) {
		for(int i = 0; i < apps.size();i++) {
			try{
				App app = apps.get(i);
				app.send(text);
			}catch(NullPointerException e) {
				//e.printStackTrace();
			}
			
		}
	}
	
	//Getter fuer Port
	public int getPort() {
		return port;
	}
	
	//Ausgabe von Informationen
    static String displayInterfaceInformation(NetworkInterface netint) throws SocketException {
  Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
        	if(inetAddress.toString().startsWith("/192")) {
        		return inetAddress.toString().substring(1, inetAddress.toString().length());
        	}
        }
        return "";
    }
    
    //Auslesen + Setzen von IP in Datenbank
	public static void setIP(){
		String IP = "";
		/*try {
			IP = InetAddress.getLocalHost().getHostAddress().toString();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}*/
		
		//Pruefen, welche IP genutzt werden soll
		if(Main.localIP.equals("f")||Main.localIP.equals("F")) {
			try {
				URL whatismyip = new URL("http://checkip.amazonaws.com");
				BufferedReader in = new BufferedReader(new InputStreamReader( whatismyip.openStream()));
				IP = in.readLine();
			}catch(MalformedURLException ecX){
			 	ecX.printStackTrace();
			}catch(IOException euc) {
				euc.printStackTrace();
			}
		}else {
		
	       Enumeration<NetworkInterface> nets;
	       
	       try {
				nets = NetworkInterface.getNetworkInterfaces();
		        for (NetworkInterface netint : Collections.list(nets)) {
		        
		            String s2 = displayInterfaceInformation(netint);
		            if(s2.length() != 0) {
		            	IP = s2;
		            	break;
		            }
		        }
			} catch (SocketException e1) {
				e1.printStackTrace();
			}
		}
		
		System.out.println("IP: " + IP);
		
		//Uebergeben von IP an PHP-Skript, welches die IP an Datenbank weitergibt
    	try{
	    	CloseableHttpClient httpclient = HttpClients.createDefault();
	
	    	HttpPost httpPost = new HttpPost("https://llololl904.000webhostapp.com/Bike-Project/setIP.php");
	    	List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	    	nvps.add(new BasicNameValuePair("IP", IP));
	    	httpPost.setEntity(new UrlEncodedFormEntity(nvps));
	    	CloseableHttpResponse response2 = httpclient.execute(httpPost);
	
	    	try {
	    	    //Erfolg?
	    		System.out.println(response2.getStatusLine());
	    	    
	    	    HttpEntity entity = response2.getEntity();

	    	    EntityUtils.consume(entity);
	    	} finally {
	    	    response2.close();
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}	
    }	
}

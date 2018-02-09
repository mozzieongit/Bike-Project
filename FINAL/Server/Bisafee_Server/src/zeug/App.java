package zeug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import zeug.User.ReceiveListener;

public class App {
	private final String name;
	private final String pw;
	public String token;
	
	private User user;
	private List<Rasp> Geraete = new ArrayList<Rasp>();
	private List<String> GeraeteNamen = new ArrayList<String>();
	
	//Konstruktor
	//Daten werden gesetzt (und eingelesen)
	public App(String name, String pw) {
		this.name = name;
		this.pw = pw;
		
		
		
		try {
			//Einlesen von Daten
			BufferedReader br = new BufferedReader(new FileReader(Main.location + "App" + name + "data.txt"));
			String zeile = "";
			
			//Token fuer Benachrichtigungen einlesen und setzen
			zeile = br.readLine();
			token = zeile;
			
			//Solange die eingelesene Zeile != null ist
			while((zeile = br.readLine()) != null) {
				String[] split = zeile.split(Main.split);
				
				//Abfragen, ob Rasp mit den Zugangsdaten vorhanden ist + Instanz getten
				Rasp rasp = Server.getRasp(split[0], split[1]);
				
				/*if(rasp == null) {
					rasp = Server.newRasp(split[0], split[1]);
				}*/
				
				//Pruefen, ob Rasp wirklich vorhanden ist
				if(rasp != null) {
					Geraete.add(rasp);
					GeraeteNamen.add(split[2]);
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Pruefen, ob angegebenes Passwort richtig ist
	public boolean pwRight(String Pw) {
		return pw.equals(Pw);
	}
	
	//Annahme des Sockets + Stringverarbeitung
	public void connect(Socket socket) {
		
		user = new User(socket, new ReceiveListener() {
			public void receive(String s) {
				String[] split = s.split(Main.split);
				System.out.println("Received (App): " + s);
				try {
					int art = Integer.parseInt(split[0]);
					switch(art) {
					case 2:
						//Gerät hinzufügen
						//"2&Geraet ID&Gerät Pw&Gerätename"
						Rasp rasp = Server.getRasp(split[1], split[2]);
						
						/*if(rasp == null) {
							rasp = Server.newRasp(split[1], split[2]);
						}*/
						if(rasp != null) {
							Geraete.add(rasp);
							GeraeteNamen.add(split[3]);
							send("2" + Main.split + split[1] + Main.split + split[3] + Main.split + rasp.getStatus() + Main.split +  rasp.getData());
						}else {
							System.out.println("Falsches PW oder falsche ID!");
							//send("21");
						}
						break;
					case 3:
						//Gerät entfernen
						//"3&Gerät ID"
						
						for(int i=0; i < Geraete.size();i++) {
							if(Geraete.get(i).getID().equals(split[1])) {
								Geraete.remove(i);
								GeraeteNamen.remove(i);
								break;
							}
						}
						
						send("3" + Main.split + split[1]);
						
						break;
					case 4:
						//Gerätealarm aktivieren
						//"4&Gerät ID"
						for(int i=0; i < Geraete.size();i++) {
							if(Geraete.get(i).getID().equals(split[1])) {
								//if(Geraete.get(i).isConnected()) {
									Geraete.get(i).SwitchScharf();
									Server.broadcastID(Geraete.get(i).getID(), "4" + Main.split + split[1]);
								if(Geraete.get(i).isConnected()) {
									Geraete.get(i).send("4");
								}else {
									System.out.println("Rasp nicht verbunden! (aber success)");
								}
								break;
							}
						}
						
						break;
					case 5:
						//Gerätealarm deaktivieren
						//"5&Gerät ID"
						
						for(int i=0; i < Geraete.size();i++) {
							if(Geraete.get(i).getID().equals(split[1])) {
								//if(Geraete.get(i).isConnected()) {
									Geraete.get(i).SwitchNotScharf();
									Server.broadcastID(Geraete.get(i).getID(),"5" + Main.split + split[1]);
								if(Geraete.get(i).isConnected()) {	
									Geraete.get(i).send("5");
								}else {
									System.out.println("Rasp nicht verbunden! (aber success)");
								}
								break;
							}
						}
						
						break;
					case 9:
						//Annahme des Token
						token = split[3];
						//System.out.println("Token: " + token);
						
						break;
					default:
						System.out.println("Nachricht konnte nicht verarbeitet werden! (" + art + ")");
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	//Disconnecten + Datenspeicherung
	public void disconnect() {
		PrintStream diskWriter;
		
		try {
			diskWriter = new PrintStream(new File(Main.location + "App" + name + "data.txt"));
			diskWriter.println(token);
			for(int i=0;i < Geraete.size();i++) {
				if(i>0) {
					diskWriter.print("\n");
				}
				diskWriter.print(Geraete.get(i).getInfo() + GeraeteNamen.get(i));
			}
			diskWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			//out.println("File not found");
		}
		/*
		if(user !=null) {
			user.disconnect();
		}
		*/
	}
	
	//Pruefen, ob App ein Geraet mit entsprechender ID hat
	public boolean hasGeraetWithID(String id) {
		for(Rasp rasp : Geraete) {
			if(rasp.getID().equals(id)) {
				return true;
			}
		}
		return false;
	}
	
	//Senden von Nachrichten
	public void send(String t) {
		t += Main.split + "\n";
		user.send(t);
		System.out.println("Send: " + t);
	}
	
	//Senden von benachrichtigungen
	public void sendNotification(String titel,String nachricht) {
		if(token.length() != 0) {
			NotificationManager.Senden(token, titel, nachricht);
		}
	}
	
	//pruefen, ob App verbunden ist
	public boolean isConnected() {
		if(user != null && user.isConnected()) {
			return true;
		}else {
			return false;
		}
	}
	
	//Benutzernamen getten
	public String getName() {
		return name;
	}
	
	//Getter fuer Passwort
	public String getPW() {
		return pw;
	}
	
	//Status als String
	/*public String toString() {
		String s = name + ": " + name + " ";
		if(isConnected()) {
			s += "is connected";
		}else {
			s += "is disconnected";
		}
		return s;
	}*/
	
	//Daten von bestimmten Rasp getten fuer Strings beim einloggen
	public String getData(int i) {
		String s = "2" + Main.split;
		s += Geraete.get(i).getID() + Main.split + GeraeteNamen.get(i) + Main.split + Geraete.get(i).getStatus() + Main.split + Geraete.get(i).getData();
		return s;
	}
	
	//Getter fuer Anzahl der Rasps
	public int getSize() {
		return Geraete.size();
	}
	
	//Getter fuer GeraeteNamen
	public String getName(String id) {
		for(int i=0;i < Geraete.size();i++) {
			if(Geraete.get(i).getID().equals(id)) {
				return GeraeteNamen.get(i);
			}
		}
		return null;
	}
	
	//Getter fuer App-Stammdaten
	public String getAppData() {
		String s = "";
		
		s += name + Main.split + pw;
		
		return s;
	}	
}

package zeug;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import zeug.User.ReceiveListener;

public class Rasp {
	private final String id;
	private final String pw;
	
	private String scharf;
	
	//Umschalten des Status
	public void Switch(String s) {
		scharf = s;
	}
	
	//Scharf schalten
	public void SwitchScharf() {
		Switch("true");
	}
	
	//entschaerfen
	public void SwitchNotScharf() {
		Switch("false");
	}
	
	private User user;
	
	private List<LatLng> GPS_Pos = new ArrayList<LatLng>();
	private List<String> GPS_Times = new ArrayList<String>();
	
	//Konstruktor + Daten einlesen bzw setzen
	public Rasp(String ID,String Pw) {
		this.id = ID;
		this.pw = Pw;
		
		try {
			try {
				//Oeffnen von Datei + einlesen
				BufferedReader br = new BufferedReader(new FileReader(Main.location + "Rasp" + id + "data.txt"));
				
				String zeile = "";
				zeile = br.readLine();
				if(!zeile.equals(null)) {
				
				scharf = zeile;
				
				zeile = br.readLine();
					
				String[] split = zeile.split(Main.split);
				
				//Setzen der Koordinaten
				for(int i = 0;i < split.length;i++) {
					String[] split2 = split[i].split(Main.split2);
					Double laengengrad = Double.parseDouble(split2[0]);
					Double breitengrad = Double.parseDouble(split2[1]);
					
					GPS_Pos.add(new LatLng(laengengrad, breitengrad));
					//out.println("Run: " + i);
				}
	
				zeile = br.readLine();
				String[] split3 = zeile.split(Main.split);
				
				//Setzen der GPS Zeiten
				for(int i = 0;i < split3.length;i++) {
					GPS_Times.add(split3[i]);
				}
				}else{
					
				}
					
				br.close();
			}catch(FileNotFoundException ex) {
				//ex.printStackTrace();
				Double s = Double.parseDouble("0.0");
				GPS_Pos.add(new LatLng(s, s));
				GPS_Times.add("00 00 12 01 01 2018");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Pruefen, ob Passwort richtig ist
	public boolean pwRight(String Pw) {
		return pw.equals(Pw);
	}
	
	//Annahme des Rasps + Stringverarbeitung
	public void connect(Socket socket,final Server server) {
		user = new User(socket, new ReceiveListener() {
			@Override
			public void receive(String s) {
				String[] split = s.split(Main.split);
				out.println("Received (Rasp): " + s);
				
				try {
					int art = Integer.parseInt(split[0]);
					switch(art) {
					case 1:
						//GPS/Ping
						//1&Laenge&Breite
						
						Double laengengrad = Double.parseDouble(split[1]);
						Double breitengrad = Double.parseDouble(split[2]);
						
						if(GPS_Pos.size() >= 5) {
							GPS_Pos.remove(0);
							GPS_Times.remove(0);
						}
						
						GPS_Pos.add(new LatLng(laengengrad, breitengrad));
						GPS_Times.add(new SimpleDateFormat("ss mm hh dd MM yyyy").format(Calendar.getInstance().getTime()));
						
						server.broadcastID(id, "6" + Main.split + id + Main.split + laengengrad + Main.split + breitengrad);
						break;
					case 2:
						//Alarm scharf
						//2
						
						SwitchScharf();
						
						server.broadcastID(id,  "4" + Main.split + id);
						break;
					case 3:
						//Alarm unscharf
						//3
						
						SwitchNotScharf();
						
						server.broadcastID(id, "5" + Main.split + id);
						break;
					case 4:
						//ALAAARM
						//4
						
						server.broadcastIDNotiAlarm(id, "Alarm");
						
						break;
					default:
						out.println("Nachricht konnte nicht verarbeitet werden! (" + art + ")");
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	//Rasp disconnecten + Datensicherung
	public void disconnect() {
		
		try {
			if(GPS_Pos.size()!=0) {
			PrintStream diskWriter = new PrintStream(new File(Main.location + "Rasp" + id + "data.txt"));
			
			diskWriter.println(scharf);
			
			for(int i=0;i < GPS_Pos.size();i++) {
				if(i>0) {
					diskWriter.print(Main.split);
				}
				diskWriter.print(GPS_Pos.get(i).laengengrad + Main.split2 + GPS_Pos.get(i).breitengrad);
			}
			
			diskWriter.println();
			
			for(int i=0;i < GPS_Times.size();i++) {
				if(i>0) {
					diskWriter.print(Main.split);
				}
				diskWriter.print(GPS_Times.get(i));
			}
			
			diskWriter.close();
			}
		} catch (FileNotFoundException ex) {
			
		}
		
		try{
			user.disconnect();
		}catch(NullPointerException e) {}
	}
	
	//Senden von Nachrichten
	public void send(String t) {
		user.send(t);
	}
	
	//Pruefen, ob rasp verbunden ist
	public boolean isConnected() {
		if(user != null && user.isConnected()) {
			return true;
		}else {
			return false;
		}
	}
	
	//Getter fuer ID
	public String getID() {
		return id;
	}
	
	//Status als String
	/*public String toString() {
		String s = id + ": " + id + " ";
		if(isConnected()) {
			s += "is connected";
		}else {
			s += "is disconnected";
		}
		
		return s;
	}*/
	
	//Getter fuer Stammdaten
	public String getInfo() {
		String s = id + Main.split + pw + Main.split;
		return s;
	}
	
	//Getter fuer Koordinaten und Zeiten
	public String getData() {
		String s = "";
		for(int i = 0;i < GPS_Pos.size();i++) {
			if(i!=0) {
				s += ";";
			}
			s += GPS_Pos.get(i).breitengrad + "," + GPS_Pos.get(i).laengengrad;
		}
		
		s += Main.split;
		
		for(int i=0; i < GPS_Times.size();i++) {
			if(i!=0) {
				s += ";";
			}
			s += GPS_Times.get(i);
		}
		
		return s;
	}
	
	//Getter fuer ID + PW
	public String getRaspData() {
		String s = id + Main.split + pw;
		return s;
	}
	
	//Getter fuer Status
	public String getStatus(){
		return scharf;
	}
	
}

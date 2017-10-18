package fahrradsicherung;

import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fahrradsicherung.User.ReceiveListener;

public class ClientRasp {


	private final String ID;
	private final String PW;
	
	private User user;
	
	private List<LatLng> GPS_Pos = new ArrayList<LatLng>();
	private List<String> GPS_Times = new ArrayList<String>();
	
	
	public ClientRasp(String ID,String pw) {
		
		this.ID = ID;
		this.PW = pw;
		
		
	}
	
	public boolean pwRight(String pw){
		return PW.equals(pw);
	}
	
	public void connect(Socket socket,Server server){
	
		
		user = new User(socket, new ReceiveListener() {
			@Override
			public void receive(String s) {
				//Nachricht von der App interpretieren
				String[] split = s.split(Main.split);
				
				try{
					int art = Integer.parseInt(split[0]);
					switch(art){
					case 11: //11,"Name","Passwort" 
						//Schon in der Serverklasse behandelt!
						break;
					case 1: //1,"Längengrad","Breitengrad"
						Double längengrad = Double.parseDouble(split[1]);
						Double breitengrad = Double.parseDouble(split[2]);
						GPS_Pos.add(new LatLng(längengrad, breitengrad));
						GPS_Times.add(new SimpleDateFormat("ss mm hh dd MM yyyy").format(Calendar.getInstance().getTime()));
								
						// TODO ZEITEN FEHLEN NOCH !!!!!!!!!!!!!!!!!!!!!!!!!
						server.broadcastToAllAppsWithGerätID(ID, "6" + Main.split + ID + Main.split + längengrad + Main.split + breitengrad);
						break;
					case 2: //2
						server.broadcastToAllAppsWithGerätID(ID, "4" + Main.split + ID);

						break;
					case 3: //3
						server.broadcastToAllAppsWithGerätID(ID, "5" + Main.split + ID);

						break;
					case 4: //4
						server.broadcastToAllAppsWithGerätID(ID, "7" + Main.split + ID);

						break;
					default:
						System.err.println("Nachrichtenart konnte nicht verarbeitet werden! (" + art + ")");
					}
					
					
					
				}catch(Exception e){
					System.err.println("Nachricht konnte nicht verarbeitet werden! (" + s + ")");
				}
				
			}
		});
	}
	
	public void disconnect(){
		user.disconnect();
	}
	
	public void send(String text){
		user.send(text);
	}
	
	public boolean isConnected(){
		if(user != null && user.isConnected()){
			return true;
		}else{
			return false;
		}
	}


	public String getID() {
		return ID;
	}

	@Override
	public String toString() {
		String s = ID + ": " + ID + " ";
		if(isConnected()){
			s += " is connected!";
		}else{
			s += " is disconnected!";
		}
		return s;
	}


}

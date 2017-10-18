package fahrradsicherung;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import fahrradsicherung.User.ReceiveListener;

public class ClientApp {
	
	private final String name;
	private final String PW;
	
	private User user;
	private List<ClientRasp> Geräte = new ArrayList<ClientRasp>();
	private List<String> GeräteNamen = new ArrayList<String>();
	
	
	
	public ClientApp(String name,String pw) {
		
		this.name = name;
		this.PW = pw;
		
		
	}
	
	public boolean pwRight(String pw){
		return PW.equals(pw);
	}
	
	public void connect(Socket socket){
	
		
		user = new User(socket, new ReceiveListener() {
			@Override
			public void receive(String s) {
				//Nachricht von der App interpretieren
				String[] split = s.split(Main.split);
				System.out.println("Receive " + s);
				try{
					int art = Integer.parseInt(split[0]);
					switch(art){
					case 1: //1&Name"&"Passwort"
						//Schon in der Serverklasse behandelt!
						break;
					case 2: //2,"Gerät ID","Gerät PW","Gerätename" 
						
						break;
					case 3: //3,"Gerät ID" 

						break;
					case 4: //4,"Gerät ID"

						break;
					case 5: //5,"Gerät ID" 

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
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * Hier stand mal etwas über die politischen Theorien :D
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	public void disconnect(){
		user.disconnect();
	}
	
	public boolean hasGerätWithID(String ID){
		for(ClientRasp rasp : Geräte){
			if(rasp.getID().equals(ID)){
				return true;
			}
		}
		return false;
	}
	
	public void send(String text){
		user.send(text);
		System.out.println("Senden: " + text);
	}
	
	public boolean isConnected(){
		if(user != null && user.isConnected()){
			System.out.println("YES2");
			if(user == null){

				System.out.println("YES3");
			}
			return true;
		}else{
			System.out.println("YES1");
			return false;
		}
	}


	public String getName() {
		return name;
	}
	
	public String getPW(){
		return PW;
	}

	@Override
	public String toString() {
		String s = name + ": " + name + " ";
		if(isConnected()){
			s += " is connected!";
		}else{
			s += " is disconnected!";
		}
		return s;
	}

}

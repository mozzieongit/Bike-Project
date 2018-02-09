package zeug;

import static java.lang.System.in;
import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;



public class Main {
	
	public static boolean running = true;
	private Server server;
	public static String location;
	
    public static String split = "&";
    public static String split2 = ",";
    public static String localIP; //Variable zum Speichern, welche IP genutzt werden soll
    
    int port = 8080;
    
	public static void main(String[] args) {
		new Main();
	}
	
	public Main(){
		out.println("Zum Speichern der Daten bitte mit \"end\" beenden!");
		Scanner keyboard = new Scanner(in);
		
		//Abfragen, welche IP genutzt werden soll
		//t ^= lokaler IP (192.168.xxx.xxx)
		System.out.print("Lokale IP? <t/f> ");
		localIP = keyboard.nextLine();
		
		//Auslesen von Dateipfad von anderen Dateien, sonst abfragen
		try {
			BufferedReader br = new BufferedReader(new FileReader("Location.txt"));
			location = br.readLine();
			br.close();
		}catch (IOException e) {
			out.print("Bitte Dateipfad zu Dateien angeben (\"\\\" am Ende nicht vergessen!): ");
			location = keyboard.nextLine();
		}
		
		
		String output;
		
		server = new Server(port);
		
		server.start();
		
		
		while(running) {
			if(keyboard.hasNextLine()) {
				output = keyboard.nextLine();
				//Beenden des Servers ermoeglichen
				if(output.equals("end")) {
					running = false;	
				}else {
					server.broadcast(output);
				}
			}
		}
		
		//Server stoppen
		server.stop();
		keyboard.close();
		
	}
}

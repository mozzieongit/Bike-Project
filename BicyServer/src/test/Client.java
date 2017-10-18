package test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client {

	
	private Socket socket;
	
	private BufferedReader ois;
	private PrintWriter oos;
	
	private boolean running = true;
	
	public Client(String IP,int port){
		try {
			socket = new Socket(IP, port);
			ois = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			oos = new PrintWriter(socket.getOutputStream());
			
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					System.out.println("Start listening...");
					while(running){
						String line;
						try {
							line = ois.readLine();
							System.out.println(line);
						} catch (IOException e) {
							running = false;
							e.printStackTrace();
						}
						
					}
					System.out.println("Stop listening...");
					
				}
			});
			t.start();
			
			

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
			//LOGIN
		//	send("NAME" );
			

			String line;
			Scanner scanner = new Scanner(System.in);
			while(running){
				if(scanner.hasNextLine()){
					line = scanner.nextLine();
					if(line == "end"){
						//CLIENT BEENDET
						running = false;
					}else{
						//EINGABE WIRD GESENDET
						send(line);
					}
				}
			}
			scanner.close();
			try {
				socket.close();
				ois.close();
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	private void send(String text){
			oos.write(text + "\n");
			oos.flush();
			
			System.out.println("Sende: " + text);
		
	}

	public static void main(String[] args) {
		String IP = "127.0.0.1";
		int port = 8080;
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("Geben sie die IP ein (Default: 127.0.0.1): \n");
		String line = scanner.nextLine();
		if(line.length() != 0){
			IP = line;
		}
		System.out.println("Geben sie den Port ein (Default: 8080): \n");
		 line = scanner.nextLine();
		if(line.length() != 0){
			port = Integer.parseInt(line);
		}
		
		
		
		new Client(IP,port);

	}
	
	
}

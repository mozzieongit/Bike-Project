package zeug;

import static java.lang.System.in;
import static java.lang.System.out;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class komm {
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	boolean running = true;
	
	void setup() {
		
	
	String output;
	Scanner keyboard = new Scanner(in);
	
	try {
		ServerSocket all = new ServerSocket(8080);
	
		Socket client = all.accept();
		
		oos = new ObjectOutputStream(client.getOutputStream());
	    ois = new ObjectInputStream(client.getInputStream());
		
		out.println("Client verbunden");
		
	} catch (IOException e) {
		e.printStackTrace();
	}
		
	Thread listen = new Thread(new Runnable() {
		
    	@Override
    	public void run() {
	    	boolean runmaster = true;
	    	boolean verarbeiten = false;
			String input;
			data ver = new data();
			
			while(running) {
				
			try {
				input = ois.readUTF();
				out.println("Client: " + input);
				switch(input) {
					case "toggle":
						verarbeiten = !verarbeiten;
						out.println("Verarbeiten: " + verarbeiten);
						break;
					case "status":
						out.println("Verarbeiten: " + verarbeiten);
						break;
					default:
						if(verarbeiten) {
						ver.verarbeitung(input);
						break;
						}
				}
			} catch (IOException e) {
				e.printStackTrace();
				//runmaster = false;
			}
			}
		}	
	});
	listen.start();
		
	while(running) {
		if(keyboard.hasNextLine()) {
			output = keyboard.nextLine();
			if(output.equals("end")) {
				running = false;
				try {
					oos.close();
					listen.stop();
					ois.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}else {
				senden(output);
			}
		}
	}

	keyboard.close();
	
	}
	
	public void senden(String send) {
		if(oos == null){
			System.out.println("Fehler: oos = null";
		}else{
			try{
				out.println("Senden: " + send);
				oos.writeUTF(send);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
	    
	}


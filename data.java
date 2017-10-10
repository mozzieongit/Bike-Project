package zeug;

import static java.lang.System.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.StringTokenizer;

public class data  {

	enum typ {
		fahrrad, app
	}
	
	enum status {
		bereit, scharf, nc, alarm
	}
	
	
	void add(String s) throws FileNotFoundException {
		//int ID;
		String pw, name, id;
		
		StringTokenizer st = new StringTokenizer(s, ",");
		
		id = st.nextToken();
		pw = st.nextToken();
		name = st.nextToken();
		
		Scanner scanner = new Scanner(new File("device.txt"));
		PrintStream write = new PrintStream("device.txt");
		
		
		
		

	}
	
	void verarbeitung(String s) /*throws FileNotFoundException*/{
		int wahl;
		String s1, send;
		StringTokenizer st = new StringTokenizer(s, ",");
		s1 = st.nextToken();
		
		wahl = Integer.parseInt(s1);
		
		komm k = new komm();
		
		out.println("Wahl: " + wahl + "\t\tStringToken: " + s1);
		out.println("String: " + s);
		
		switch(wahl) {
			/*case 2: 
				add(s);
				break;*/
			default:
				send = "Nicht implementiert oder falsche Eingabe!";
				k.senden(send);
				//out.println("Nicht implementiert oder falsche Eingabe!");
				break;
		}
	}
}

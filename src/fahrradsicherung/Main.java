package fahrradsicherung;


import java.util.Scanner;

public class Main {

	private Server server;
	public static boolean running = true;
	
	
	public static boolean ConsoleOnly = false;
	
	public static final String split = "&";
	
	public static void main(String[] args) {
		new Main();

	}


	int port = 8080;
	public Main(){
		Scanner scanner = new Scanner(System.in);
		
		/*
		System.out.println("+++ Server +++");
		printMenu();

		System.out.println("Server starten[S]");
		System.out.println("Port setzen[P]");
		
		while(true){
		
		String line = scanner.nextLine();
			if(line.equals("S")){
				break;
			}else if(line.equals("P")){
				System.out.print("Port setzen auf: ");
				port = scanner.nextInt();
				System.out.println("\n\n");
				printMenu();

				System.out.println("Server starten[S]");
				System.out.println("Port setzen[P]");
			}
		}
*/
		server = new Server(port);
		Window window = new Window(server);
		
		
		
		server.start(window);

		if(Main.ConsoleOnly){
			String line;
			while(running){
				if(scanner.hasNextLine()){
					line = scanner.nextLine();
					if(line == "end"){
						running = false;
					}else{
						server.broadcast(line);
					}
				}
			}
			server.stop();
		}
		scanner.close();
	}

	
	
	private void printMenu(){
		System.out.println("Einstellungen: ");
		System.out.println("Port: " + port + " \n\n");
		
	}
}

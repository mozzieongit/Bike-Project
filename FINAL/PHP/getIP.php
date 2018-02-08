



		<?php
			#Verbinden mit der mySql Datenbank
			$connect = mysqli_connect("localhost", "id2040624_user", "Geheim4711", "id2040624_name");

			
			#Ein Array für die Rückgabe zum Sender erstellen
			$response = array();    
			$response["success"] = false;  
			
			
			function getIP() {
				global $connect,$response;	#Wichtige Variablen übergeben

				$statement = mysqli_prepare($connect, "SELECT * FROM IP"); #Parameter: Verbidnung, SQL Query String
				mysqli_stmt_execute($statement);	#Befehl ausführen
				mysqli_stmt_store_result($statement);	#Ergebnisse speichern
				mysqli_stmt_bind_result($statement,$ip);	#Variable $ip dem Eintrag der Datenbank zuweisen	
				
				
				while(mysqli_stmt_fetch($statement)){
				$response["success"] = true;  
				$response["IP"] = $ip;	#Dem Rückgabe-Array die IP übergeben
					
				}
				mysqli_stmt_close($statement);    #Verbindung schließen
			}
			getIP();  #Funktion aufrufen
			echo json_encode($response);	#Array im Json Format zurückgegeben
			
		?>

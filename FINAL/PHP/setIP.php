	



			<?php
				#Verbinden mit der mySql Datenbank
				$connect = mysqli_connect("localhost", "id2040624_user", "Geheim4711", "id2040624_name");

				if(isset($_POST["IP"])){ #Überprüfen ob eine IP übergeben wurde
					 
				#Ein Array für die Rückgabe zum Sender erstellen
				$response = array();
				$response["success"] = false; 
				
				$ip = $_POST["IP"]; #Übergebene IP einer Variable zuweisen
				 
				 
				 
				 function setIP() {
					global $connect,$ip,$response;	#Wichtige Variablen übergeben
					if ( !$connect ) {	#Wenn keine Verbindung möglich war, das Program beenden
					  die( 'connect error: '.mysqli_connect_error() );
					}
					#Löschung der Einträge
					$statement = mysqli_prepare($connect, "DELETE FROM IP"); #Parameter: Verbidnung, SQL Query String
					mysqli_stmt_execute($statement);	#Befehl ausführen
					mysqli_stmt_close($statement);     #Verbindung schließen
					
				   $statement = mysqli_prepare($connect, "INSERT INTO IP (ip) VALUES (?)"); #Parameter: Verbidnung, SQL Query String
			  
				   mysqli_stmt_bind_param($statement, "s",$ip); #Dem Query String eine Variable zuweisen
				   mysqli_stmt_execute($statement);		#Befehl ausführen
					mysqli_stmt_close($statement);      #Verbindung schließen
				  
					$response["success"] = true;  
				}
				setIP(); #Funktion aufrufen
				
				echo json_encode($response);	#Array im Json Format zurückgegeben
				}
			?>
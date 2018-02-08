<?php

	//Key für den Firebase-Server
	$server_key = "AAAAXeuhA4s:APA91bHvDfVvL_urFu5nvmT-qT_EOH8tJU3pMlblohwDm4ZFwMkz3TVxEu87YoQ9_1WDTrlcokQ9LyBTTXKdRbCKw-47cVSyHjBTuux_ZOSiRPCkuxuvnzYpPm9fzRmAo0-tPeyq6fD1";
	
	//Url des Servers
	$fcm_server_url = "https://fcm.googleapis.com/fcm/send";
	
    	$to = $_POST["to"];			//An wen wird es gesendet?
	$title = $_POST["title"];	//Titel der Notification
	$time = $_POST["time"];		//Zeit an der der Alarm ausgelöst wurde
   	$content_text = $_POST["content"];	//Inhalt der Notification
	
	$httpheader = array('Content-Type:application/json','Authorization:key='.$server_key);
	$post_content = array('to' => $to,'data' => array('title' => $title,'content-text' => $content_text,'time' => $time));	//Array aus den Daten für den Firebase-Server erstellen
	
	
	$curl_connection = curl_init();	//Curl Verbindung vorbereiten
	
	curl_setopt($curl_connection, CURLOPT_URL, $fcm_server_url );	//Url setzen
	curl_setopt($curl_connection, CURLOPT_POST,true );				//Post soll ausgeführt werden
	curl_setopt($curl_connection, CURLOPT_HTTPHEADER,$httpheader );	//Key wird übergeben
	curl_setopt($curl_connection, CURLOPT_RETURNTRANSFER, true );	
	curl_setopt($curl_connection, CURLOPT_POSTFIELDS ,json_encode($post_content) );	//Inhalt der Notification
	
	$answerFromServer = curl_exec($curl_connection);	//Wird ausgeführt...
	
	curl_close($curl_connection);	//Verbindung wird geschlossen
	
	echo "Antwort vom Server: ".$answerFromServer;	//Rückgabe an den Server von Bjarne
	

?>

#!/usr/bin/env python
# CLIENT
# PYTHON 2.7

# Written by Jannik Peters 2017/2018

import RPi.GPIO as GPIO
import MFRC522
import socket
import sys

import threading
import time

import urllib2
import json

# Abrufen der ServerIP; Konvertieren von JSON-Format in Dictionary
try:
    tmp = json.loads(urllib2.urlopen("http://llololl904.000webhostapp.com/Bike-Project/getIP.php").read())
    # Konvertieren von Dictionaryinhalt, mit Key IP, mit Unicode-Strings in ascii-Strings
    ipaddress = tmp['IP'].encode('ascii', 'ignore')

except:
    print >>sys.stderr, "Error Getting IP"
    ipaddress = '192.168.0.11'

port = 8080

# zu testzwecken, um eine IP beim Start zu bestimmen
#if sys.argv[1] == "ip":
#     ipaddress = sys.argv[2]

# Warnmeldung der GPIO-Benutzung abstellen
GPIO.setwarnings(False)

# Klasse mit Methoden fuer die Funktionen
class Client:
    al_state = True                    # Alarmstatus
    position = '53.5310&8.6123'         # Position durch _get_gps geaendert
    position_old = '53.5310&8.6123'     # Alte Position

    def _get_gps(self):                 # Threadinhalt zum Abrufen der Positionsdaten aus der Datei, erstellt durch testGPSwriter.py
        while True:
            f = open('currentData.py', 'r')
            text = f.read()             # Datei lesen
            f.close()
            b = text.split(',')         # Inhalt aufbereiten --> Array
            if b[0] == 'No fix':
                print 'No fix'
            else:
                b[0] = float(b[0])
                b[1] = float(b[1])

                print b
                Client.position = '%.4f&%.4f' % (b[0], b[1])    # Positionsdaten in Klassenattribute schreiben
                print "Client: ", Client.position
                print "Old: ", Client.position_old
                if Client.position != Client.position_old:  # and Client.al_state == True:
                    if Client.al_state:
                        # Wenn Position geaendert wurde, Alarm an Server senden
                        print >>sys.stderr, "Moved"
                        sock.sendall("4\n")
                        print >> sys.stderr, "Send 4"

            Client.position_old = Client.position
            time.sleep(1)

    def get_gps(self):
        threading.Thread(target=self._get_gps).start()          # Aufruf der GPS-Abfrage-Methode

    def _send_pos(self):
        while True:
            sock.sendall("1&%s\n" % Client.position)  # Positionsdaten an Server senden
            print >>sys.stderr, "Send Postition"
            time.sleep(30)

    def send_pos(self):
        threading.Thread(target=self._send_pos).start()

    def _recv_tcp(self):            # TCP-Nachrichten empfangen und auswerten

        while True:
            data = sock.recv(128)
            print(data)
            if data:
                if data[0] == '4':          # Alarmstatus wird auf "aktiv" gesetzt
                    #print "Change State to True"
                    Client.al_state = True
                elif data[0] == '5':        # Alarmstatus wird auf "inaktiv" gesetzt
                    #print "Change State from", Client.al_state, "to False"
                    Client.al_state = False

    def recv_tcp(self):
        threading.Thread(target=self._recv_tcp).start()         # Aufruf des TCP Empfangs

    def _nfc_auth(self):                # Umschalten des Alarms mit NFC-Chip
        card_placed = False
        while True:
            try:
                while True:
                    MIFAREReader = MFRC522.MFRC522()
                    authcode = [70, 65, 72, 82, 82, 65, 68, 67, 72] # die ersten 9 Ziffern sind der Authentifizierungscode
                    #Scan for cards
                    (status,TagType) = MIFAREReader.MFRC522_Request(MIFAREReader.PICC_REQIDL)
                    #If a card is found
                    if status == MIFAREReader.MI_OK and card_placed == False:
                        card_placed = True
                        # Get the UID of the card
                        (status,uid) = MIFAREReader.MFRC522_Anticoll()
                        if status == MIFAREReader.MI_OK:
                            # This is the default key for authentication
                            key = [0xFF,0xFF,0xFF,0xFF,0xFF,0xFF]
                            # Select the scanned tag
                            MIFAREReader.MFRC522_SelectTag(uid)
                            # Authenticate
                            status = MIFAREReader.MFRC522_Auth(MIFAREReader.PICC_AUTHENT1A, 8, key, uid)
                            # Check if authenticated
                            if status == MIFAREReader.MI_OK:
                                # Read block 8
                                data = MIFAREReader.MFRC522_Read(8)
                                if data[:9] == authcode:
                                    print >>sys.stderr, 'sending "Authentifiziert"'
                                    if Client.al_state:
                                        #print(Client.al_state)
                                        Client.al_state = False
                                        sock.sendall("3\n")
                                        #print(Client.al_state)
                                    else:
                                        Client.al_state = True
                                        sock.sendall("2\n")
                                    print >>sys.stderr, 'send State'
                                    time.sleep(1)
                                    #return "Authentifiziert"
                                #elif ...
                    elif status != MIFAREReader.MI_OK:
                        card_placed = False
            except:
                continue
    def nfc_auth(self):
        threading.Thread(target=self._nfc_auth).start()     # Aufruf der NFC-Abfrage

    def _script_gps(self):          # Thread um Positionsdaten zu manipulieren, falls GPS-Modul keinen Empfang hat
        x = 15                      # Zu TESTZWECKEN
        while True:
            time.sleep(x)
            Client.position = "53.5310&8.6123"
            x += 15

    def script_gps(self):
        threading.Thread(target=self._script_gps).start()   # Aufruf des GPS Skriptes


#Create Socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)       # Definieren des Netzwerkmoduls

# Connect the socket to the port where the server is listening
server_address = (ipaddress, port)              # Serveraddresse bestimmen

print >>sys.stderr, 'connecting to %s port %s' % server_address

try:
    try:
        base = Client()     # Klasse instaziieren

        f = open('currentData.py', 'r')
        text = f.read()  # Datei lesen
        f.close()
        text = text.split(',')

        text[0] = float(text[0])
        text[1] = float(text[1])

        Client.position = '%.4f&%.4f' % (text[0], text[1])
        Client.position_old = '%.4f&%.4f' % (text[0], text[1])


        sock.connect(server_address)            # Mit dem Server verbinden
        sock.sendall("11&ID&anderesPW&%s\n" % text)      # Anmeldung an Server senden
        datalog = sock.recv(10)                 # Anmeldebestaetigung erwarten
        if datalog == "11":
            print >>sys.stderr, 'logged in successful'

            t0 = base.get_gps()                 # Threads starten
            print("Started GPS-Tracking")
            t1 = base.recv_tcp()
            print("Started tcp")
            t2 = base.nfc_auth()
            print("Started nfc")
            t4 = base.send_pos()
            print("Started Ping")

            #if sys.argv[1] == "script":
             #   base.script_gps()
              #  print("Started script")

            t1.join()               # Auf beenden der Threads warten


        else:
            print >>sys.stderr, 'Wrong Login'
            next
    except:
        next

except:
    print >>sys.stderr, 'closing socket'
    sock.close()

